package ccw.serviceinnovation.split;

import ccw.serviceinnovation.split.reedsolomon.ReedSolomon;

import java.io.*;
import java.nio.ByteBuffer;

import static ccw.serviceinnovation.split.reedsolomon.SampleDecoder.BYTES_IN_INT;

public class RsDecoderHandlerImpl implements SplitDecoderHandler {
    private final Integer dataShards;
    private final Integer parityShards;
    public RsDecoderHandlerImpl(Integer dataShards,Integer parityShards){
        this.dataShards = dataShards;
        this.parityShards = parityShards;
    }
    @Override
    public byte[] merge(byte[][] shards) throws IOException {
        // Read in any of the shards that are present.
        // (There should be checking here to make sure the input
        // shards are the same size, but there isn't.)
        int total_shards = dataShards + parityShards;
        final boolean [] shardPresent = new boolean [total_shards];
        int shardSize = 0;
        int shardCount = 0;
        for (int i = 0; i < total_shards; i++) {
            if (shards[i] != null) {
                shardSize = shards[i].length;
                shardPresent[i] = true;
                shardCount += 1;
            }
        }

        // We need at least DATA_SHARDS to be able to reconstruct the file.
        if (shardCount < dataShards) {
            throw new IOException("Not enough shards present");
        }

        // Make empty buffers for the missing shards.
        for (int i = 0; i < total_shards; i++) {
            if (!shardPresent[i]) {
                shards[i] = new byte [shardSize];
            }
        }

        // Use Reed-Solomon to fill in the missing shards
        ReedSolomon reedSolomon = ReedSolomon.create(dataShards, parityShards);
        reedSolomon.decodeMissing(shards, shardPresent, 0, shardSize);

        // Combine the data shards into one buffer for convenience.
        // (This is not efficient, but it is convenient.)
        byte [] allBytes = new byte [shardSize * dataShards];
        for (int i = 0; i < dataShards; i++) {
            System.arraycopy(shards[i], 0, allBytes, shardSize * i, shardSize);
        }

        // Extract the file length
        int fileSize = ByteBuffer.wrap(allBytes).getInt();

        // Write the decoded file
        ByteBuffer buffer = ByteBuffer.allocate(fileSize);
        buffer.put(allBytes,BYTES_IN_INT,fileSize);
        return buffer.array();
    }
}
