package server;

import domain.Message;
import domain.Task;
import lombok.extern.slf4j.Slf4j;
import server.exception.handler.InterruptedExceptionHandler;
import server.handler.message.MessageHandler;
import server.task.TaskManagerThread;
import server.util.SpringContextUtil;
import util.ProtoStuffUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Dunfu Peng
 * @Date: 2019/4/4 19:22
 */
@Slf4j
public class ChatServer {
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    public static final int PORT = 9000;
    public static final String QUIT = "QUIT";
    private AtomicInteger onlineUsers;

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    private ExecutorService readPool;

    private BlockingQueue<Task> downloadTaskQueue;
    private TaskManagerThread taskManagerThread;
    private InterruptedExceptionHandler exceptionHandler;
    private ListenerThread listenerThread;

    public ChatServer(){
        log.info("服务器启动.....");
    }


    /**
     * 线程结束的方式推荐采取中断
     * 在while循环开始处检查是否中断，并提供一个方法来将自己中断
     * 不要在外部将线程中断
     *
     * 如果要中断一个阻塞在某个地方的线程，最好是继承自Thread，先关闭所依赖的资源，再关闭当前线程
     */
    private class ListenerThread extends Thread {
        @Override
        public void interrupt() {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                super.interrupt();
            }
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    selector.select();
                    for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); ) {
                        SelectionKey key = it.next();
                        it.remove();
                        if (key.isAcceptable()) {
                            handleAcceptRequest();
                        } else if (key.isReadable()) {
                            key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
                            readPool.execute(new ReadEventHandler(key));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.run();
        }

        public void shutdown(){
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 关闭服务器
     */
    public void shutdownServer(){
        try {
            taskManagerThread.shutdown();
            listenerThread.shutdown();
            readPool.shutdown();
            serverSocketChannel.close();
            System.exit(0);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 处理客户端的连接请求
     */
    private void handleAcceptRequest() {
        try {
            SocketChannel client = serverSocketChannel.accept();
            //接收的客户端也要切换为非阻塞模式
            client.configureBlocking(false);
            //监控客户端的读操作是否就绪
            client.register(selector, SelectionKey.OP_READ);
            log.info("服务器连接客户端：{}", client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处于线程池中的线程会随着线程池的shutdown方法而关闭
     */
    private class ReadEventHandler implements Runnable {

        private ByteBuffer buf;
        private SocketChannel client;
        private ByteArrayOutputStream baos;
        private SelectionKey key;

        public ReadEventHandler(SelectionKey key) {
            this.key = key;
            this.client = (SocketChannel) key.channel();
            this.buf = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
            this.baos = new ByteArrayOutputStream();
        }

        @Override
        public void run() {
            try {
                int size;
                while ((size = client.read(buf)) > 0) {
                    buf.flip();
                    baos.write(buf.array(), 0, size);
                    buf.clear();
                }
                if (size == -1) {
                    return;
                }
                log.info("读取完毕，继续监听");
                //继续监听读取事件
                key.interestOps(key.interestOps() | SelectionKey.OP_READ);
                key.selector().wakeup();
                byte[] bytes = baos.toByteArray();
                baos.close();
                Message message = ProtoStuffUtil.deserialize(bytes, Message.class);
                MessageHandler messageHeader = SpringContextUtil.getBean("MessageHeader", message.getHeader().getType().toString().toLowerCase());
                try {
                    messageHeader.handle(message, selector, key, downloadTaskQueue, onlineUsers);
                } catch (InterruptedException e) {
                    log.error("服务器线程被中断");
                    exceptionHandler.handle(client,message);
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
