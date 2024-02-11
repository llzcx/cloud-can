package ccw.serviceinnovation.node.util;

import ccw.serviceinnovation.node.bo.FNameBo;
import ccw.serviceinnvation.encryption.consant.EncryptionEnum;
import cn.hutool.core.text.StrBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;

import static ccw.serviceinnovation.node.server.constant.StaticConstant.*;

public class FNameUtil {

    public static Path toFileName(Path parent, String key, EncryptionEnum encryptionEnum, Integer off) {
        StrBuilder sb = new StrBuilder();
        sb.append(STANDARD)
                .append(key)
                .append(SPLIT)
                .append(encryptionEnum.getCode())
                .append(SPLIT)
                .append(off)

                .append(SUFFIX);
        return Paths.get(parent.toString(), sb.toString());
    }

    public static FNameBo getPropertyFromFName(Path name) {
        String path = name.getFileName().toString();
        //头尾
        path = path.substring(STANDARD.length(), path.length() - SUFFIX.length());
        String[] split = path.split(SPLIT);
        return new FNameBo(split[0], EncryptionEnum.getEnum(Integer.valueOf(split[1])), Integer.parseInt(split[2]));
    }

    public static Path getReadTempFile(String parent, String eventId) {
        return Paths.get(parent, READ_TEMPORARY + SPLIT + eventId);
    }

    public static Path getWriteTempFile(String parent, String eventId) {
        return Paths.get(parent, WRITE_TEMPORARY + SPLIT + eventId);
    }

    public static void main(String[] args) {
        Path path = Paths.get("D:\\oss");
        Path fileName = toFileName(path, "1231312", EncryptionEnum.NULL, 1);
        System.out.println(fileName.toString());
        FNameBo propertyFromFName = getPropertyFromFName(fileName);
        System.out.println(propertyFromFName);
    }

}
