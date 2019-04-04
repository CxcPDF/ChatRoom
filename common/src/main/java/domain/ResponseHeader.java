package domain;

import enumeration.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Dunfu Peng
 * @Date: 2019/4/3 11:18
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseHeader {

    private String sender;
    private ResponseType type;
    private Integer responseCode;
    private Long timestamp;
}
