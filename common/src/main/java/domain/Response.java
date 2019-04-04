package domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Dunfu Peng
 * @Date: 2019/4/3 11:17
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private ResponseHeader header;
    private byte[] body;
}
