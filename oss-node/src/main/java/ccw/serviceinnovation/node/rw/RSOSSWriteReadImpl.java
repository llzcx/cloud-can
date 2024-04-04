package ccw.serviceinnovation.node.rw;

import ccw.serviceinnovation.node.bo.ObjectMeta;
import ccw.serviceinnovation.node.calculate.EncryptAndSplitByteHandlerImpl;
import ccw.serviceinnovation.node.select.PartitionSelector;
import ccw.serviceinnovation.node.select.PartitionSelectorImpl;
import ccw.serviceinnovation.node.util.FNameUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ccw.serviceinnovation.node.server.constant.RegisterConstant.*;
import static ccw.serviceinnovation.node.server.db.StorageEngine.*;

public class RSOSSWriteReadImpl extends OSSWriterRead<byte[][]> {

    public static PartitionSelector partitionSelector;


    public RSOSSWriteReadImpl(){
        //磁盘分区选择器
        partitionSelector = new PartitionSelectorImpl(PARTITION_DISK);
    }
    @Override
    public void setByteHandler() {
        byteHandler = new EncryptAndSplitByteHandlerImpl();
    }

    @Override
    public ObjectMeta write(String key, byte[] bytes) throws IOException {
        byte[][] encoder = byteHandler.encoder(bytes);
        int num = encoder.length;
        for (int i = 0; i < num; i++) {
            Path parent = partitionSelector.get(key, i);
            Path fileName = FNameUtil.toFileName(parent, key);
            if (Files.exists(fileName)) continue;
            syncDisk.save(fileName, 0, encoder[i], 0, encoder[i].length);
        }
        return new ObjectMeta(key,ENCRYPT);
    }

    @Override
    public byte[] read(ObjectMeta objectMeta) throws IOException {
        String key = objectMeta.getKey();
        byte[][] bytes = new byte[TOTAL_SHARDS][];
        for (int i = 0; i < TOTAL_SHARDS; i++) {
            Path parent = partitionSelector.get(key, i);
            Path fileName = FNameUtil.toFileName(parent, key);
            bytes[i] = syncDisk.read(fileName, 0, -1, null);
        }
        return byteHandler.decoder(bytes);
    }

    @Override
    public boolean delete(ObjectMeta objectMeta) throws IOException {
        String key = objectMeta.getKey();
        for (int i = 0; i < TOTAL_SHARDS; i++) {
            Path parent = partitionSelector.get(key, i);
            Path fileName = FNameUtil.toFileName(parent, key);
            syncDisk.del(fileName);
        }
        return true;
    }
}
