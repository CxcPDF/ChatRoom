package server.handler.task;

import domain.Response;
import domain.Task;
import server.exception.TaskException;
import server.http.HttpConnectionManager;
import server.task.TaskManagerThread;
import util.ProtoStuffUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Author: Dunfu Peng
 * @Date: 2019/4/5 14:12
 */
public abstract class BaseTaskHandler implements Runnable{
    protected Task info;
    protected HttpConnectionManager manager;

    abstract protected Response process() throws IOException,InterruptedException;

    abstract protected void init(TaskManagerThread parentThread);

    @Override
    public void run() {
        try {
            info.getReceiver().write(ByteBuffer.wrap(ProtoStuffUtil.serialize(process())));
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new TaskException(info);
        } catch (IOException e) {
            e.printStackTrace();
            throw new TaskException(info);
        }
    }

    public void init(Task info,HttpConnectionManager manager,TaskManagerThread parentThread){
        this.info=info;
        this.manager=manager;
        init(parentThread);
    }
}
