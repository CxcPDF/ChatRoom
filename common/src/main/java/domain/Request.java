package domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author: Dunfu Peng
 * @Date: 2019/4/3 11:14
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    private String url;
    private Map<String,String> params;
}
