package ccw.serviceinnovation.node.server.db;

import java.io.IOException;
import java.util.logging.Handler;

public abstract class DataEncoderHandler {
    private Handler nextHandler;
    public void setNextHandler(Handler nextHandler){
        this.nextHandler = nextHandler;
    }
    public byte[][] encoderToMulti(byte[] data) throws IOException {
        throw new IOException("no impl");
    }

    public byte[] encoder(byte[] data) throws IOException {
        throw new IOException("no impl");
    }
}
