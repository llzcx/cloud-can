package ccw.serviceinnovation.node.server.db;

import ccw.serviceinnovation.node.util.StringUtil;
import service.bo.ReadEventBo;
import service.raft.request.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 方法将被状态机反射调用
 */
public abstract class ServiceHandler {

    public abstract void initialize() throws IOException;
    public abstract void del(DelRequest delRequest) throws IOException;

    public abstract void readdelevent(ReadDelEventRequest readDelEventRequest) throws IOException;
    public abstract Long readevent(ReadEventRequest readEventRequest) throws IOException;
    public abstract byte[] readfragment(ReadFragmentRequest readFragmentRequest) throws IOException;

    public abstract void upload(UploadRequest uploadRequest) throws IOException;

    public abstract void writedelevent(WriteDelEventRequest writeDelEventRequest) throws IOException;
    public abstract void writeevent(WriteEventRequest writeEventRequest);
    public abstract void writefragment(WriteFragmentRequest writeFragmentRequest) throws IOException;
    public abstract void writemerge(WriterMergeRequest writerMergeRequest) throws IOException;

    public static Object invoke(JRaftRpcReq request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //反射调用
        Class<? extends JRaftRpcReq> req =request.getClass();
        String methodName = StringUtil.getBetweenLastTwoDots(req.getName());
        assert methodName != null;
        //截断后缀Request,变成小写
        methodName = methodName.substring(0, methodName.length() - 7).toLowerCase();
        System.out.println("invoke methodName:"+methodName);
        Method myMethod = StorageEngine.serviceHandler.getClass().getDeclaredMethod(methodName, req);
        myMethod.setAccessible(true);
        return myMethod.invoke(StorageEngine.serviceHandler,request);
    }
}
