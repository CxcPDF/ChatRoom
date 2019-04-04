package domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Dunfu Peng
 * @Date: 2019/4/3 11:07
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private MessageHeader header;
    private byte[] body;
}
