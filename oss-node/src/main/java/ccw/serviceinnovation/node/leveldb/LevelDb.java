package ccw.serviceinnovation.node.leveldb;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.util.json.FastJsonHandlerImpl;
import ccw.serviceinnovation.common.util.json.JacksonHandlerImpl;
import ccw.serviceinnovation.common.util.json.OssJsonHandler;
import ccw.serviceinnovation.node.bo.ObjectMeta;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevelDb {
    private DB db = null;
    private String dbFolder;
    private String charset = "utf-8";

    private OssJsonHandler jsonHandler = new FastJsonHandlerImpl();

    public LevelDb(String dbFolder) {
        this.dbFolder = dbFolder;
    }

    /**
     * 初始化LevelDB
     * 每次使用levelDB前都要调用此方法，无论db是否存在
     */
    public void initLevelDB() {
        DBFactory factory = new Iq80DBFactory();
        Options options = new Options();
        options.createIfMissing(true);
        try {
            this.db = factory.open(new File(dbFolder), options);
        } catch (IOException e) {
            System.out.println("levelDB启动异常");
            e.printStackTrace();
        }
    }

    /**
     * 基于fastjson的对象序列化
     *
     * @param obj
     * @return
     */
    private byte[] serializer(Object obj) {
       return jsonHandler.encodeByte(obj);

    }

    /**
     * 基于fastJson的对象反序列化
     *
     * @param bytes
     * @return
     */
    private <T> T deserializer(byte[] bytes,Class<T> cls) {
        return jsonHandler.decodeByte(bytes,cls);
    }

    /**
     * 存放数据
     *
     * @param key
     * @param val
     */
    public void put(String key, Object val) {
        try {
            this.db.put(key.getBytes(charset), this.serializer(val));
        } catch (UnsupportedEncodingException e) {
            System.out.println("编码转化异常");
            e.printStackTrace();
        }
    }

    /**
     * 根据key获取数据
     *
     * @param key
     * @return
     */
    public <T> T get(String key,Class<T> cls) {
        byte[] val = null;
        try {
            val = db.get(key.getBytes(charset));
        } catch (Exception e) {
            System.out.println("levelDB get error");
            e.printStackTrace();
            return null;
        }
        if (val == null) {
            return null;
        }
        return deserializer(val, cls);
    }

    /**
     * 根据key删除数据
     *
     * @param key
     */
    public void delete(String key) {
        try {
            db.delete(key.getBytes(charset));
        } catch (Exception e) {
            System.out.println("levelDB delete error");
            e.printStackTrace();
        }
    }


    /**
     * 关闭数据库连接
     * 每次只要调用了initDB方法，就要在最后调用此方法
     */
    public void closeDB() {
        if (db != null) {
            try {
                db.close();
            } catch (IOException e) {
                System.out.println("levelDB 关闭异常");
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取所有key
     *
     * @return
     */
    public List<String> getKeys() {

        List<String> list = new ArrayList<>();
        DBIterator iterator = null;
        try {
            iterator = db.iterator();
            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> item = iterator.next();
                String key = new String(item.getKey(), charset);
                list.add(key);
            }
        } catch (Exception e) {
            System.out.println("遍历发生异常");
            e.printStackTrace();
        } finally {
            if (iterator != null) {
                try {
                    iterator.close();
                } catch (IOException e) {
                    System.out.println("遍历发生异常");
                    e.printStackTrace();
                }

            }
        }
        return list;
    }

    public static void main(String[] args) {
        LevelDb levelDb =new LevelDb("./level");
        levelDb.initLevelDB();
        levelDb.put("name","keer");
        Bucket bucket = new Bucket();
        bucket.setName("bucket");
        levelDb.put("bucket",bucket);
        System.out.println("获得数据库中的所有key" + levelDb.getKeys().toString());
        System.out.println("数据库中key：name，value："+levelDb.get("name",String.class));
        Bucket bucket1 = levelDb.get("bucket",Bucket.class);
        System.out.println("数据库中key：userInfo，value："+ bucket1);
        levelDb.closeDB();
    }

}
