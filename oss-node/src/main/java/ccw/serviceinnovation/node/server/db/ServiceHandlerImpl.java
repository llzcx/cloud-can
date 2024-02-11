package ccw.serviceinnovation.node.server.db;

import ccw.serviceinnovation.node.bo.ObjectMeta;
import ccw.serviceinnovation.node.util.Bitmap;
import ccw.serviceinnovation.node.util.FNameUtil;
import lombok.extern.slf4j.Slf4j;
import service.raft.request.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ccw.serviceinnovation.node.server.constant.RegisterConstant.*;
import static ccw.serviceinnovation.node.server.db.StorageEngine.*;

@Slf4j
public class ServiceHandlerImpl extends ServiceHandler {


    public ServiceHandlerImpl() {

    }

    @Override
    public void initialize() throws IOException {

    }

    @Override
    public void del(DelRequest delRequest) throws IOException {
        String key = delRequest.getNodeObjectKey();
        ObjectMeta objectMeta = index.get(key);
        if(objectMeta == null) throw new IOException("object key is not exist.");
        Bitmap bitmap = objectMeta.getBitmap();
        for (int i = 0; i < TOTAL_SHARDS; i++) {
            if (bitmap.getBit(i) == 1) {
                Path parent = partitionSelector.get(key, i);
                Path fileName = FNameUtil.toFileName(parent, key, ENCRYPT, i);
                syncDisk.del(fileName);
            }
        }
    }

    @Override
    public void readdelevent(ReadDelEventRequest readDelEventRequest) throws IOException {
        String eventId = readDelEventRequest.getEventId();
        Path tempFile = FNameUtil.getReadTempFile(TMP_LOG_DISK, eventId);
        syncDisk.del(tempFile);
    }

    @Override
    public Long readevent(ReadEventRequest readEventRequest) throws IOException {
        String eventId = readEventRequest.getEventId();
        String key = readEventRequest.getObjectKey();
        ObjectMeta objectMeta = index.get(key);
        if(objectMeta == null) throw new IOException("object key is not exist.");
        Bitmap bitmap = objectMeta.getBitmap();
        byte[][] bytes = new byte[TOTAL_SHARDS][];
        for (int i = 0; i < TOTAL_SHARDS; i++) {
            if (bitmap.getBit(i) == 1) {
                Path parent = partitionSelector.get(key, i);
                Path fileName = FNameUtil.toFileName(parent, key, ENCRYPT, i);
                bytes[i] = syncDisk.read(fileName,0,-1,null);
            }else{
                bytes[i] = null;
            }
        }
        byte[] decoder = byteHandler.decoder(bytes);
        Path tempFile = FNameUtil.getReadTempFile(TMP_LOG_DISK, eventId);
        syncDisk.save(tempFile,0,decoder,0,decoder.length);
        return (long) decoder.length;
    }

    @Override
    public byte[] readfragment(ReadFragmentRequest readFragmentRequest) throws IOException {
        String eventId = readFragmentRequest.getEventId();
        long off = readFragmentRequest.getOff();
        int size = readFragmentRequest.getSize();
        Path tempFile = FNameUtil.getReadTempFile(TMP_LOG_DISK, eventId);
        if(!tempFile.toFile().exists()) throw new IOException("no data to read at this node");
        byte[] bytes = new byte[size];
        return syncDisk.read(tempFile,off,size,bytes);
    }

    @Override
    public void upload(UploadRequest uploadRequest) throws IOException {
        byte[] data = uploadRequest.getData();
        String key = uploadRequest.getNodeObjectKey();
        encoderAndDisk(key, data);
    }

    @Override
    public void writedelevent(WriteDelEventRequest writeDelEventRequest) throws IOException {
        String eventId = writeDelEventRequest.getEventId();
        Path writeTempFile = FNameUtil.getWriteTempFile(TMP_LOG_DISK, eventId);
        syncDisk.del(writeTempFile);
    }

    @Override
    public void writeevent(WriteEventRequest writeEventRequest) {
        String eventId = writeEventRequest.getEventId();
        Path writeTempFile = FNameUtil.getWriteTempFile(TMP_LOG_DISK, eventId);
        syncDisk.setSize(writeTempFile,writeEventRequest.getSize());

    }

    @Override
    public void writefragment(WriteFragmentRequest writeFragmentRequest) throws IOException {
        Long off = writeFragmentRequest.getOff();
        byte[] fragment = writeFragmentRequest.getFragment();
        String eventId = writeFragmentRequest.getEventId();
        Path writeTempFile = FNameUtil.getWriteTempFile(TMP_LOG_DISK, eventId);
        syncDisk.save(writeTempFile,off,fragment,0,fragment.length);
    }

    @Override
    public void writemerge(WriterMergeRequest writerMergeRequest) throws IOException {
        String eventId = writerMergeRequest.getEventId();
        String key = writerMergeRequest.getObjectKey();
        Path writeTempFile = FNameUtil.getWriteTempFile(TMP_LOG_DISK, eventId);
        byte[] buffer = syncDisk.read(writeTempFile, 0, -1, null);
        encoderAndDisk(key, buffer);
    }

    private void encoderAndDisk(String key, byte[] buffer) throws IOException {
        byte[][] encoder = byteHandler.encoder(buffer);
        int num = encoder.length;
        for (int i = 0; i < num; i++) {
            Path parent = partitionSelector.get(key, i);
            Path fileName = FNameUtil.toFileName(parent, key, ENCRYPT, i);
            syncDisk.save(fileName, 0,encoder[i], 0, encoder[i].length);
            index.add(key, ENCRYPT);
        }
    }

}
