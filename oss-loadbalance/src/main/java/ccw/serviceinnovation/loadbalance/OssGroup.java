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

    public OssGroup(String groupName, List<String> nodeList) {
        this.groupName = groupName;
        this.nodeList = nodeList;
    }

    @Override
    public String getStringFormat() {
        return groupName;
    }

    public String getConf(){
        StringBuilder sb = new StringBuilder();
        int n = nodeList.size();
        for (int i = 0; i < n; i++) {
            sb.append(nodeList.get(i));
            if(i!=n-1){
                sb.append(',');
            }
        }
        return sb.toString();
    }
}
