package ccw.serviceinnovation.node.bo;
import lombok.Data;

@Data
public class RsParam {
    public static RsParam instance = null;

    private final int data;
    private final int parity;

    public RsParam(int data, int parity) {
        this.data = data;
        this.parity = parity;
    }




}
