package ccw.serviceinnovation.node.server.db;

import ccw.serviceinnovation.disk.SyncDisk;
import ccw.serviceinnovation.disk.FileChannelSyncDiskImpl;
import ccw.serviceinnovation.node.calculate.ByteHandler;
import ccw.serviceinnovation.node.calculate.EncryptAndSplitByteHandlerImpl;
import ccw.serviceinnovation.node.index.EtagIndexHashMapImpl;
import ccw.serviceinnovation.node.index.Index;
import ccw.serviceinnovation.node.index.IndexContext;
import ccw.serviceinnovation.node.partition.PartitionSelector;
import ccw.serviceinnovation.node.partition.SurplusPartitionSelectorIMpl;
import ccw.serviceinnvation.encryption.consant.EncryptionEnum;
import service.raft.request.*;

import java.io.IOException;

import static ccw.serviceinnovation.node.server.constant.RegisterConstant.*;
import static ccw.serviceinnovation.node.server.constant.StaticConstant.PARTITION_SUFFIX;
import static ccw.serviceinnovation.node.server.constant.StaticConstant.STANDARD;

public class OnApplyImpl implements OnApply {
    /**
     * 索引实现
     */
    public static Index index;

    /**
     * 持久化
     */
    public SyncDisk syncDisk;

    /**
     * 字节流处理
     */
    public ByteHandler<byte[]> byteHandler;

    private PartitionSelector partitionSelector;


    public OnApplyImpl() {

    }

    @Override
    public void initialize() throws IOException {
        //磁盘
        syncDisk = new FileChannelSyncDiskImpl();
        syncDisk.initialize();

        //字节流处理
        byteHandler = new EncryptAndSplitByteHandlerImpl();
        byteHandler.initialize();

        //索引
        index = new EtagIndexHashMapImpl();
        IndexContext.index = index;
        index.load();

        //磁盘分区选择器
        partitionSelector = new SurplusPartitionSelectorIMpl(PARTITION_DISK, STANDARD,PARTITION_SUFFIX);
    }

    @Override
    public void get(GetRequest getRequest) {
        System.out.println("get");
    }

    @Override
    public void del(DelRequest delRequest) {
        System.out.println("del");
    }

    @Override
    public void upload(UploadRequest uploadRequest) throws IOException {
        byte[] data = uploadRequest.getData();
        String nodeObjectKey = uploadRequest.getNodeObjectKey();
        EncryptionEnum encryption = EncryptionEnum.getEnum(uploadRequest.getSecret());
        byte[][] encoder = byteHandler.encoder(data, encryption);
        int num = encoder.length;
        for (int i = 0; i < num; i++) {
            syncDisk.save(partitionSelector.get(nodeObjectKey,i),encoder[i],0,encoder[i].length);
        }
    }

    @Override
    public void event(EventRequest eventRequest) {
        System.out.println("event");
    }

    @Override
    public void fragment(FragmentRequest fragmentRequest) {
        System.out.println("fragment");
    }

    @Override
    public void delevent(DelEventRequest delEventRequest) {
        System.out.println("delEvent");
    }

    @Override
    public void merge(MergeRequest mergeRequest) {
        System.out.println("merge");
    }
}
