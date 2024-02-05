package ccw.serviceinnovation.node.index;

import ccw.serviceinnovation.node.bo.ObjectMeta;

public interface Index {
    /**
     * 获取数据
     * @param etag
     * @return
     */
    ObjectMeta get(String etag);

    /**
     * 索引加载
     */
    void load();
}
