package server.exception.handler;

import domain.Message;
import domain.Response;
import domain.ResponseHeader;
import domain.Task;
import enumeration.ResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import server.exception.TaskException;
import server.property.PromptMsgProperty;
import util.ProtoStuffUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * UncaughtExceptionHandler异常处理器可以处理ExecutorService
 * 通过execute方法提交的线程中抛出的RuntimeException
 * @Author: Dunfu Peng
 * @Date: 2019/5/14 22:55
 */
@Component("taskExceptionHandler")
@Slf4j
public class TaskExceptionHandler implements Thread.UncaughtExceptionHandler{
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            if (e instanceof TaskException){
                TaskException taskException=(TaskException)e;
                Task task=taskException.getInfo();
                Message message=task.getMessage();
                byte[] response= ProtoStuffUtil.serialize(
                        new Response(
                                ResponseHeader.builder()
                                        .type(ResponseType.PROMPT)
                                        .sender(message.getHeader().getSender())
                                        .timestamp(message.getHeader().getTimestamp())
                                        .build(),
                                PromptMsgProperty.TASK_FAILURE.getBytes(PromptMsgProperty.charset)
                        )
                );
                log.info("返回任务执行失败信息");
                task.getReceiver().write(ByteBuffer.wrap(response));
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
