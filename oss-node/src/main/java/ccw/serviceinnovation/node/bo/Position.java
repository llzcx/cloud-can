package ccw.serviceinnovation.node.bo;

import lombok.Data;

@Data
public class Position {
    private long position;
    private long length;

    public Position(long position, long length) {
        this.position = position;
        this.length = length;
    }
}
