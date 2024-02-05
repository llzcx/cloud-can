package ccw.serviceinnovation.node.secret;

public interface Secret {
    byte[] encode();
    byte[] decode();
}
