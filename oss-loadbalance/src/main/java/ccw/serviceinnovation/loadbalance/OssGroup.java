package ccw.serviceinnovation.loadbalance;

import lombok.Data;

import java.util.List;


/**
 * 陈翔
 */
@Data
public class OssGroup implements Server {

    private String groupName;

    private List<String> nodeList;

    @Override
    public String getStringFormat() {
        return groupName;
    }
}
