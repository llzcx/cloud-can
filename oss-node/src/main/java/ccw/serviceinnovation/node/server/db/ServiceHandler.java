package ccw.serviceinnovation.node.server.db;

import ccw.serviceinnovation.node.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import service.raft.request.*;
import service.raft.response.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 方法将被状态机反射调用
 */
@Slf4j
public abstract class ServiceHandler {

    //初始化
    public abstract void initialize() throws IOException;
    //删除对象【写】
    public abstract DelResponse del(DelRequest delRequest) throws IOException;
    //删除读事件【写】
    public abstract ReadDelEventResponse readdelevent(ReadDelEventRequest readDelEventRequest) throws IOException;
    //开启读事件【写】
    public abstract ReadEventResponse readevent(ReadEventRequest readEventRequest) throws IOException;
    //读分片【读】
    public abstract ReadFragmentResponse readfragment(ReadFragmentRequest readFragmentRequest) throws IOException;
    //读分片【读】
    public abstract ReadResponse read(ReadRequest readRequest) throws IOException;

    //写对象【写】
    public abstract UploadResponse upload(UploadRequest uploadRequest) throws IOException;
    //删除写事件【写】
    public abstract WriteDelEventResponse writedelevent(WriteDelEventRequest writeDelEventRequest) throws IOException;
    //开启写事件【写】
    public abstract WriteEventResponse writeevent(WriteEventRequest writeEventRequest);
    //写分片【写】
    public abstract WriteFragmentResponse writefragment(WriteFragmentRequest writeFragmentRequest) throws IOException;
    //合并【写】
    public abstract WriterMergeResponse writemerge(WriterMergeRequest writerMergeRequest) throws IOException;

    public static Object invoke(JRaftRpcReq request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //反射调用
        Class<? extends JRaftRpcReq> req =request.getClass();
        String methodName = StringUtil.getBetweenLastTwoDots(req.getName());
        assert methodName != null;
        //截断后缀Request,变成小写
        methodName = methodName.substring(0, methodName.length() - 7).toLowerCase();
        log.info("invoke RPC:{}",methodName);
        Method myMethod = StorageEngine.serviceHandler.getClass().getDeclaredMethod(methodName, req);
        myMethod.setAccessible(true);
        return myMethod.invoke(StorageEngine.serviceHandler,request);
    }
}
