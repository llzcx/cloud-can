package ccw.serviceinnovation.node.rw;

import ccw.serviceinnovation.node.bo.ObjectMeta;
import ccw.serviceinnovation.node.calculate.EncryptByteHandlerImpl;
import ccw.serviceinnovation.node.select.PartitionSelector;
import ccw.serviceinnovation.node.select.PartitionSelectorImpl;
import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import ccw.serviceinnovation.node.util.FNameUtil;

import java.io.IOException;
import java.nio.file.Path;

import static ccw.serviceinnovation.node.server.constant.RegisterConstant.PARTITION_DISK;
import static ccw.serviceinnovation.node.server.db.StorageEngine.syncDisk;

public class DirectWriteReadImpl extends OSSWriterRead<byte[]> {

    /**
     * 磁盘分区选择器
     */
    public static PartitionSelector partitionSelector;

    private final static int INDEX = 1;

    public DirectWriteReadImpl(){
        partitionSelector = new PartitionSelectorImpl(PARTITION_DISK);
    }

    @Override
    public void setByteHandler() {
        byteHandler = new EncryptByteHandlerImpl();
    }
    @Override
    public ObjectMeta write(String key, byte[] bytes) throws IOException {
        byte[] encoder = byteHandler.encoder(bytes);
        Path parent = partitionSelector.get(key, INDEX);
        Path fileName = FNameUtil.toFileName(parent, key);
        syncDisk.save(fileName, 0,encoder, 0, encoder.length);
        return new ObjectMeta(key, RegisterConstant.ENCRYPT);
    }

    @Override
    public byte[] read(ObjectMeta objectMeta) throws IOException {
        String key = objectMeta.getKey();
        Path fileName = FNameUtil.toFileName(partitionSelector.get(key, INDEX), key);
        return byteHandler.decoder(syncDisk.read(fileName,0,-1,null));
    }

    @Override
    public boolean delete(ObjectMeta objectMeta) throws IOException {
        String key = objectMeta.getKey();
        syncDisk.del(FNameUtil.toFileName(partitionSelector.get(key, INDEX), key));
        return true;
    }
}
