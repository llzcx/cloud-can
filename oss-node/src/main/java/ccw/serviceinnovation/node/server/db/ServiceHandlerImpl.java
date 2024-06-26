package ccw.serviceinnovation.node.server.db;

import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.hash.directcalculator.EtagDirectCalculator;
import ccw.serviceinnovation.hash.directcalculator.MD5EtagDirectCalculatorAdapter;
import ccw.serviceinnovation.node.bo.ObjectMeta;
import ccw.serviceinnovation.node.server.db.concurrency.ObjectThreadBlockReadWriteLock;
import ccw.serviceinnovation.node.util.CustomizableBitmap;
import ccw.serviceinnovation.node.util.FNameUtil;
import lombok.extern.slf4j.Slf4j;
import service.raft.request.*;
import service.raft.response.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static ccw.serviceinnovation.node.server.constant.RegisterConstant.*;
import static ccw.serviceinnovation.node.server.db.StorageEngine.*;

@Slf4j
public class ServiceHandlerImpl extends ServiceHandler {

    ObjectThreadBlockReadWriteLock objectThreadBlockReadWriteLock = new ObjectThreadBlockReadWriteLock();


    ConcurrentHashMap<String, CustomizableBitmap> fragmentMap = new ConcurrentHashMap<>();

    EtagDirectCalculator etagDirectCalculator = new MD5EtagDirectCalculatorAdapter();

    public ServiceHandlerImpl() {

    }

    @Override
    public void initialize() throws IOException {

    }

    @Override
    public DelResponse del(DelRequest delRequest) throws IOException {
        String key = delRequest.getNodeObjectKey();
        objectThreadBlockReadWriteLock.writeLock(key);
        try {
            ObjectMeta objectMeta = index.get(key);
            if (objectMeta == null) return new DelResponse(true, ResultCode.KEY_IS_NULL);
            if (objectMeta.getCount() == 1) ossWriterRead.delete(objectMeta);
            index.decr(key);
            return new DelResponse(true, null);
        } catch (Exception e) {
            throw new IOException("del error.");
        } finally {
            objectThreadBlockReadWriteLock.writeRelease(key);
        }
    }

    @Override
    public ReadDelEventResponse readdelevent(ReadDelEventRequest readDelEventRequest) throws IOException {
        String eventId = readDelEventRequest.getEventId();
        Path tempFile = FNameUtil.getReadTempFile(TMP_LOG_DISK, eventId);
        syncDisk.del(tempFile);
        return new ReadDelEventResponse(true);
    }

    @Override
    public ReadEventResponse readevent(ReadEventRequest readEventRequest) throws IOException {
        String eventId = readEventRequest.getEventId();
        String key = readEventRequest.getObjectKey();
        objectThreadBlockReadWriteLock.readLock(key);
        try {
            ObjectMeta objectMeta = index.get(key);
            if (objectMeta == null) throw new IOException("object key is not exist.");
            byte[] bytes = ossWriterRead.read(objectMeta);
            Path tempFile = FNameUtil.getReadTempFile(TMP_LOG_DISK, eventId);
            syncDisk.save(tempFile, 0, bytes, 0, bytes.length);
            return new ReadEventResponse((long) bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("read error.");
        } finally {
            objectThreadBlockReadWriteLock.readRelease(key);
        }

    }

    @Override
    public ReadFragmentResponse readfragment(ReadFragmentRequest readFragmentRequest) throws IOException {
        String eventId = readFragmentRequest.getEventId();
        long off = readFragmentRequest.getOff();
        int size = readFragmentRequest.getSize();
        Path tempFile = FNameUtil.getReadTempFile(TMP_LOG_DISK, eventId);
        if (!tempFile.toFile().exists()) throw new IOException("the event has been deleted");
        byte[] bytes = new byte[size];
        return new ReadFragmentResponse(syncDisk.read(tempFile, off, size, bytes));
    }

    @Override
    public ReadResponse read(ReadRequest readRequest) throws IOException {
        String key = readRequest.getNodeObjectKey();
        objectThreadBlockReadWriteLock.readLock(key);
        try {
            ObjectMeta objectMeta = index.get(key);
            if (objectMeta == null) throw new IOException("object key is not exist.");
            return new ReadResponse(ossWriterRead.read(objectMeta));
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("read error.");
        } finally {
            objectThreadBlockReadWriteLock.readRelease(key);
        }
    }

    @Override
    public UploadResponse upload(UploadRequest uploadRequest) throws IOException {
        //TODO 小文件上层做校验
        byte[] data = uploadRequest.getData();
        String key = uploadRequest.getNodeObjectKey();
        objectThreadBlockReadWriteLock.writeLock(key);
        try {
            if (!index.incr(key)) {
                //TODO 自增失败，落盘
                index.add(key, ossWriterRead.write(key, data));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("write error.");
        } finally {
            objectThreadBlockReadWriteLock.writeRelease(key);
        }
        return new UploadResponse();
    }

    @Override
    public WriteDelEventResponse writedelevent(WriteDelEventRequest writeDelEventRequest) throws IOException {
        String eventId = writeDelEventRequest.getEventId();
        Path writeTempFile = FNameUtil.getWriteTempFile(TMP_LOG_DISK, eventId);
        syncDisk.del(writeTempFile);
        return new WriteDelEventResponse();
    }

    @Override
    public WriteEventResponse writeevent(WriteEventRequest writeEventRequest) {
        String eventId = writeEventRequest.getEventId();
        String key = writeEventRequest.getNodeObjectKey();
        //TODO 自增
        objectThreadBlockReadWriteLock.writeLock(key);
        boolean incr = index.incr(key);
        objectThreadBlockReadWriteLock.writeRelease(key);
        if (incr) {
            return new WriteEventResponse(true);
        } else {
            //TODO 自增失败，创建写事件
            fragmentMap.put(eventId, new CustomizableBitmap(writeEventRequest.getChunks()));
            Path writeTempFile = FNameUtil.getWriteTempFile(TMP_LOG_DISK, eventId);
            syncDisk.setSize(writeTempFile, writeEventRequest.getSize());
        }
        return new WriteEventResponse(false);
    }

    @Override
    public WriteFragmentResponse writefragment(WriteFragmentRequest writeFragmentRequest) throws IOException {
        Long off = writeFragmentRequest.getOff();
        byte[] fragment = writeFragmentRequest.getFragment();
        String eventId = writeFragmentRequest.getEventId();
        Integer chunk = writeFragmentRequest.getChunk();
        Path writeTempFile = FNameUtil.getWriteTempFile(TMP_LOG_DISK, eventId);
        //TODO 写偏移量
        CustomizableBitmap customizableBitmap = fragmentMap.get(eventId);
        customizableBitmap.setBit(chunk, 1);
        //TODO 写数据
        syncDisk.save(writeTempFile, off, fragment, 0, fragment.length);
        return new WriteFragmentResponse();
    }

    @Override
    public WriterMergeResponse writemerge(WriterMergeRequest writerMergeRequest) throws IOException {
        String eventId = writerMergeRequest.getEventId();
        String key = writerMergeRequest.getObjectKey();
        objectThreadBlockReadWriteLock.writeLock(key);
        try {
            //TODO 去重
            if (index.incr(key)) return new WriterMergeResponse(true, null);
            Path writeTempFile = FNameUtil.getWriteTempFile(TMP_LOG_DISK, eventId);
            //TODO 判读分块是否上传完毕
            if (!fragmentMap.get(eventId).isFULL())
                return new WriterMergeResponse(false, ResultCode.CHUNK_NOT_UP_FINISH);
            //TODO 文件校验
            String etag = etagDirectCalculator.get(writeTempFile);
            if (!Objects.equals(etag, key)) return new WriterMergeResponse(false, ResultCode.FILE_CHECK_ERROR);
            //TODO 落盘
            byte[] buffer = syncDisk.read(writeTempFile, 0, -1, null);
            index.add(key, ossWriterRead.write(key, buffer));
            return new WriterMergeResponse(true, null);
        }  catch (Exception e) {
            e.printStackTrace();
            throw new IOException("merge error.");
        } finally {
            objectThreadBlockReadWriteLock.writeRelease(key);
        }
    }
}
