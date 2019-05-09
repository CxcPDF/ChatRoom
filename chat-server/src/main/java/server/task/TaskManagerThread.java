package server.task;

import domain.Task;
import lombok.extern.slf4j.Slf4j;
import server.exception.factory.ExceptionHandlingThreadFactory;
import server.handler.task.BaseTaskHandler;
import server.http.HttpConnectionManager;
import server.util.SpringContextUtil;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * 消费者
 * 负责从阻塞队列中取出任务并提交给线程池
 *
 * @Author: Dunfu Peng
 * @Date: 2019/4/5 13:00
 */
@Slf4j
public class TaskManagerThread extends Thread{

    private ExecutorService taskPool;
    private BlockingQueue<Task> taskBlockingQueue;
    private HttpConnectionManager httpConnectionManager;
    private ExecutorService crawlerPool;


    public TaskManagerThread(BlockingQueue<Task> taskBlockingQueue){
        this.taskPool=new ThreadPoolExecutor(
                5,10,1000,
                TimeUnit.MICROSECONDS,new ArrayBlockingQueue<>(10),
                new ExceptionHandlingThreadFactory(SpringContextUtil.getBean("taskExceptionHandler")),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        this.taskBlockingQueue=taskBlockingQueue;
        this.httpConnectionManager=SpringContextUtil.getBean("httpConnectionManager");
        this.crawlerPool=new ThreadPoolExecutor(
                5,10,1000,
                TimeUnit.MILLISECONDS,new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public void shutdown(){
        taskPool.shutdown();
        crawlerPool.shutdown();
        Thread.currentThread().interrupt();
    }

    @Override
    public void run() {
        Task task;
        try {
            while (!Thread.currentThread().isInterrupted()){
                task=taskBlockingQueue.take();
                log.info("{}已从阻塞队列中取出",task.getReceiver().getRemoteAddress());
                BaseTaskHandler taskHandler=SpringContextUtil.getBean("BaseTaskHandler",task.getType().toString().toLowerCase());
                taskHandler.init(task,httpConnectionManager,this);
                System.out.println(taskHandler);
                taskPool.execute(taskHandler);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ExecutorService getCrawlerPool(){
        return crawlerPool;
    }
}
