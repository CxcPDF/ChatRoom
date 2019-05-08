package server.task;

import domain.Task;
import server.exception.factory.ExceptionHandlingThreadFactory;
import server.http.HttpConnectionManager;
import server.util.SpringContextUtil;

import java.util.concurrent.*;

/**
 * @Author: Dunfu Peng
 * @Date: 2019/4/5 13:00
 */
public class TaskManagerThread extends Thread{

    private ExecutorService taskPool;
    private BlockingQueue<Task> taskBlockingQueue;
    private HttpConnectionManager httpConnectionManager;
    private ExecutorService crawlerPool;


    public TaskManagerThread(BlockingQueue<Task> taskBlockingQueue){
        this.taskPool=new ThreadPoolExecutor(
                5,10,1000,
                TimeUnit.MICROSECONDS,new ArrayBlockingQueue<>(10),
                new ExceptionHandlingThreadFactory(SpringContextUtil)
        )
    }
}
