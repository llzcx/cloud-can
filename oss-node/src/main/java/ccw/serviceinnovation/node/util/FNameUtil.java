package ccw.serviceinnovation.node.util;

import ccw.serviceinnovation.node.bo.FNameBo;
import ccw.serviceinnvation.encryption.consant.EncryptionEnum;
import cn.hutool.core.text.StrBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;

import static ccw.serviceinnovation.node.server.constant.StaticConstant.*;

public class FNameUtil {

    public static Path toFileName(Path parent, String key) {
        return Paths.get(parent.toString(), key);
    }

    public static Path getReadTempFile(String parent, String eventId) {
        return Paths.get(parent, READ_TEMPORARY + SPLIT + eventId);
    }

    public static Path getWriteTempFile(String parent, String eventId) {
        return Paths.get(parent, WRITE_TEMPORARY + SPLIT + eventId);
    }

}
