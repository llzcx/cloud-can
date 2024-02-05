package ccw.serviceinnovation.node.secret;

import ccw.serviceinnovation.node.secret.impl.Sm4SecretImpl;

public class SecretFactory {
    public static Secret createSecret(SecretEnum secret){
        if(secret.equals(SecretEnum.SM4)){
            return new Sm4SecretImpl();
        }
        return null;
    }
}
