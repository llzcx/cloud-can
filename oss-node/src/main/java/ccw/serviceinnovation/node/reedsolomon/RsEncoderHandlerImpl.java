package ccw.serviceinnovation.node.reedsolomon;

import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import ccw.serviceinnovation.node.server.db.DataEncoderHandler;

import java.io.IOException;

public class RsEncoderHandlerImpl extends DataEncoderHandler {
    ReedSolomon reedSolomon;
    public RsEncoderHandlerImpl(){
        ReedSolomon.create(RegisterConstant.DATA_SHARDS, RegisterConstant.PARITY_SHARDS);
    }
    @Override
    public byte[][] encoderToMulti(byte[] data) throws IOException {
        return super.encoderToMulti(data);
    }

    @Override
    public byte[] encoder(byte[] data) throws IOException {
        return super.encoder(data);
    }
}
