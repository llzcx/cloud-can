package ccw.serviceinnovation.node.server.db;

import service.raft.request.*;

import java.io.IOException;

/**
 * 方法将被状态机反射调用
 */
public interface OnApply {

    void initialize() throws IOException;
    void get(GetRequest getRequest);

    /**
     * 删除某个对象
     */
    void del(DelRequest delRequest);

    /**
     * 上传对象
     */
    void upload(UploadRequest uploadRequest) throws IOException;

    /**
     *创建分片上传事件
     */
    void event(EventRequest eventRequest);

    /**
     * 上传某个分块
     */
    void fragment(FragmentRequest fragmentRequest);

    /**
     * 删除分片上传的事件
     */
    void delevent(DelEventRequest delEventRequest);

    /**
     * 合并分块
     */
    void merge(MergeRequest mergeRequest);
}
