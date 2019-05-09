package server.handler.message.impl;

import domain.Message;
import domain.Response;
import domain.ResponseHeader;
import domain.Task;
import enumeration.ResponseType;
import org.springframework.stereotype.Component;
import server.handler.message.MessageHandler;
import util.ProtoStuffUtil;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Component("MessageHandler.broadcast")
public class BroadcastMessageHandler extends MessageHandler {
    @Override
    public void handle(Message message, Selector server, SelectionKey client, BlockingQueue<Task> queue, AtomicInteger onlineUsers) throws InterruptedException {
        try {
            byte[] response = ProtoStuffUtil.serialize(
                    new Response(
                            ResponseHeader.builder()
                                    .type(ResponseType.NORMAL)
                                    .sender(message.getHeader().getSender())
                                    .timestamp(message.getHeader().getTimestamp()).build(),
                            message.getBody()
                    )
            );
            super.broadcast(response,server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
