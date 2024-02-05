package ccw.serviceinnovation.node.server.db;

import java.io.IOException;
import java.util.logging.Handler;

public abstract class DataDecoderHandler {
    private Handler nextHandler;

    public void setNextHandler(Handler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public byte[] decoder(byte[] data) throws IOException {
        throw new IOException("no impl");
    }

    public byte[] decoderMulti(byte[][] data) throws IOException {
        throw new IOException("no impl");
    }
}
