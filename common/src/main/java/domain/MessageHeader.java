package domain;

import enumeration.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Dunfu Peng
 * @Date: 2019/4/3 11:07
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageHeader {

    private String sender;
    private String receiver;
    private MessageType type;
    private Long timestamp;
}
