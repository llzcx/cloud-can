package ccw.serviceinnovation.node.index;

import ccw.serviceinnovation.node.bo.ObjectMeta;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件索引
 */
public class EtagIndexHashMapImpl extends ConcurrentHashMap<String, ObjectMeta> implements Index{


    public EtagIndexHashMapImpl(){

    }

    @Override
    public ObjectMeta get(String etag) {
        return null;
    }

    @Override
    public void load() {

    }
}
