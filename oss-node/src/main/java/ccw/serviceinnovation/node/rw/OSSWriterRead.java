package ccw.serviceinnovation.node.rw;

import ccw.serviceinnovation.node.bo.ObjectMeta;
import ccw.serviceinnovation.node.calculate.ByteHandler;

import java.io.IOException;

public abstract class OSSWriterRead<T> {
    ByteHandler<T> byteHandler;


    public OSSWriterRead(){
        setByteHandler();
        byteHandler.initialize();
    }

    public abstract void setByteHandler();

    public abstract ObjectMeta write(String key, byte[] bytes) throws IOException;

    public abstract byte[] read(ObjectMeta objectMeta) throws IOException;

    public abstract boolean delete(ObjectMeta objectMeta) throws IOException;
}
