package ccw.serviceinnovation.node.index;

import ccw.serviceinnovation.node.bo.FNameBo;
import ccw.serviceinnovation.node.bo.ObjectMeta;
import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import ccw.serviceinnovation.node.util.Bitmap32;
import ccw.serviceinnovation.node.util.FNameUtil;
import ccw.serviceinnvation.encryption.consant.EncryptionEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件索引
 */
@Slf4j
public class EtagIndexHashMapImpl extends ConcurrentHashMap<String, ObjectMeta> implements Index{


    ConcurrentHashMap<String,ObjectMeta> map = new ConcurrentHashMap<>();
    public EtagIndexHashMapImpl(){


    }

    @Override
    public ObjectMeta get(String uniqueKey) {
        return map.get(uniqueKey);
    }

    @Override
    public void add(String uniqueKey, EncryptionEnum encryptionEnum) {
        ObjectMeta objectMeta = new ObjectMeta(uniqueKey,encryptionEnum);
        map.put(uniqueKey,objectMeta);
    }

    @Override
    public void load() throws IOException {
        for (String diskPath : RegisterConstant.PARTITION_DISK) {
            Path directory = Paths.get(diskPath);
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    FNameBo read = FNameUtil.getPropertyFromFName(file);
                    String key = read.getKey();
                    EncryptionEnum encryption = read.getEncryptionEnum();
                    ObjectMeta objectMeta = map.computeIfAbsent(key, k -> new ObjectMeta(key, encryption));
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        log.debug("index load success,total {}",map.size());
    }

    @Override
    public boolean incr(String uniqueKey) {
        return false;
    }

    @Override
    public boolean decr(String uniqueKey) {
        return false;
    }
}
