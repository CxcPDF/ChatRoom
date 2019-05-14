package server.exception.handler;

import domain.Message;
import domain.Response;
import domain.ResponseHeader;
import enumeration.ResponseType;
import org.springframework.stereotype.Component;
import server.property.PromptMsgProperty;
import util.ProtoStuffUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Author: Dunfu Peng
 * @Date: 2019/5/14 22:51
 */

@Component("interruptedExceptionHandler")
public class InterruptedExceptionHandler {
    public void handle(SocketChannel channel, Message message){
        try {
            byte[] response= ProtoStuffUtil.serialize(
                    new Response(
                            ResponseHeader.builder()
                                    .type(ResponseType.PROMPT)
                                    .sender(message.getHeader().getSender())
                                    .timestamp(message.getHeader().getTimestamp()).build(),
                            PromptMsgProperty.SERVER_ERROR.getBytes(PromptMsgProperty.charset)
                    )
            );
            channel.write(ByteBuffer.wrap(response));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
