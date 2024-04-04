package ccw.serviceinnovation.node.rw;

import ccw.serviceinnovation.disk.Data;
import ccw.serviceinnovation.disk.DiskData;
import ccw.serviceinnovation.hash.select.HashCodeSelectorImpl;
import ccw.serviceinnovation.hash.select.ItemSelector;
import ccw.serviceinnovation.node.bo.ObjectMeta;
import ccw.serviceinnovation.node.bo.Position;
import ccw.serviceinnovation.node.calculate.EncryptByteHandlerImpl;
import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import ccw.serviceinnovation.node.server.db.ShutDownHook;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓冲区 + 顺序IO实现
 */
@Slf4j
public class SequentialIOWriteReadImpl extends OSSWriterRead<byte[]> {

    private final static String NAME = "data";

    ItemSelector<Data> diskSelector;

    private final static int BUFFER_SIZE = 256 * 1024 * 100;

    public SequentialIOWriteReadImpl() {
        List<Data> list = new ArrayList<>();
        for (String path : RegisterConstant.PARTITION_DISK) {
            try {
                list.add(new DiskData(Paths.get(path, NAME), BUFFER_SIZE, StandardOpenOption.READ, StandardOpenOption.WRITE,StandardOpenOption.CREATE));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("SequentialIOWriteReadImpl error");
            }
        }
        diskSelector = new HashCodeSelectorImpl<>(list);
        ShutDownHook.add(() -> {
            for (Data data : list) {
                try {
                    data.force();
                    data.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void setByteHandler() {
        byteHandler = new EncryptByteHandlerImpl();
    }

    @Override
    public ObjectMeta write(String key, byte[] bytes) throws IOException {
        bytes = byteHandler.encoder(bytes);
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        long position = diskSelector.select(key).write(wrap);
        return new ObjectMeta(key, RegisterConstant.ENCRYPT, new Position(position,bytes.length));
    }


    @Override
    public byte[] read(ObjectMeta objectMeta) throws IOException {
        String key = objectMeta.getKey();
        long position = objectMeta.getPosition().getPosition();
        int length = (int)objectMeta.getPosition().getLength();
        ByteBuffer allocate = ByteBuffer.allocate(length);
        allocate.clear();
        diskSelector.select(key).read(allocate,position,length);
        return byteHandler.decoder(allocate.array());
    }

    @Override
    public boolean delete(ObjectMeta objectMeta) throws IOException {
        return true;
    }
}
