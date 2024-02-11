package ccw.serviceinnovation.node.bo;

import ccw.serviceinnvation.encryption.consant.EncryptionEnum;
import lombok.Data;
import lombok.ToString;

import java.util.BitSet;

@Data
@ToString
public class FNameBo {
    private String key;
    private EncryptionEnum encryptionEnum;
    private Integer off;

    public FNameBo(String key, EncryptionEnum encryptionEnum,Integer off) {
        this.key = key;
        this.encryptionEnum = encryptionEnum;
        this.off = off;
    }

}
