package ccw.serviceinnovation.split;

import ccw.serviceinnovation.split.reedsolomon.ReedSolomon;

import java.io.IOException;
import java.nio.ByteBuffer;

import static ccw.serviceinnovation.split.reedsolomon.SampleEncoder.BYTES_IN_INT;

public class RsEncoderHandlerImpl implements SplitEncoderHandler {

    private final Integer dataShards;
    private final Integer parityShards;
    public RsEncoderHandlerImpl(Integer dataShards,Integer parityShards){
        this.dataShards = dataShards;
        this.parityShards = parityShards;
    }

    @Override
    public byte[][] split(byte[] data) throws IOException {
        final int fileSize = data.length;
        final int storedSize = fileSize + BYTES_IN_INT;
        final int shardSize = (storedSize + dataShards - 1) / dataShards;
        final int totalShards = dataShards + parityShards;
        final int bufferSize = shardSize * dataShards;
        final byte [] allBytes = new byte[bufferSize];
        ByteBuffer buffer = ByteBuffer.wrap(allBytes);
        buffer.putInt(fileSize);
        buffer.put(data,0,data.length);
        byte [] [] shards = new byte [totalShards] [shardSize];
        // Fill in the data shards
        for (int i = 0; i < dataShards; i++) {
            System.arraycopy(allBytes, i * shardSize, shards[i], 0, shardSize);
        }
        // Use Reed-Solomon to calculate the parity.
        ReedSolomon reedSolomon = ReedSolomon.create(dataShards, parityShards);
        reedSolomon.encodeParity(shards, 0, shardSize);
        return shards;
    }
}
