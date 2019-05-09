package server.exception.factory;

import java.util.concurrent.ThreadFactory;

/**
 * @Author: Dunfu Peng
 * @Date: 2019/4/5 13:42
 */
public class ExceptionHandlingThreadFactory implements ThreadFactory {
    private Thread.UncaughtExceptionHandler handler;
    public ExceptionHandlingThreadFactory(Thread.UncaughtExceptionHandler handler){
        this.handler=handler;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread=new Thread();
        //在这里设置异常提醒
        thread.setUncaughtExceptionHandler(handler);
        return thread;
    }
}
