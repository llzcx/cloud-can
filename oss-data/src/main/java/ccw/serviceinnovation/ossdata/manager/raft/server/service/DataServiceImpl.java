/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ccw.serviceinnovation.ossdata.manager.raft.server.service;

import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.util.IpUtils;
import ccw.serviceinnovation.common.util.net.NetUtil;
import ccw.serviceinnovation.ossdata.constant.OssDataConstant;
import ccw.serviceinnovation.ossdata.manager.raft.server.DataClosure;
import ccw.serviceinnovation.ossdata.manager.raft.server.DataOperation;
import ccw.serviceinnovation.ossdata.manager.raft.server.DataServer;
import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.SerializerManager;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.closure.ReadIndexClosure;
import com.alipay.sofa.jraft.entity.Task;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.rhea.StoreEngineHelper;
import com.alipay.sofa.jraft.rhea.options.StoreEngineOptions;
import com.alipay.sofa.jraft.util.BytesUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

import static ccw.serviceinnovation.ossdata.constant.OssDataConstant.RPC_ADDR;

/**
 * @author likun (saimu.msm@antfin.com)
 */
public class DataServiceImpl implements DataService {
    private static final Logger LOG = LoggerFactory.getLogger(DataServiceImpl.class);


    private final DataServer dataServer;

    private final Executor      readIndexExecutor;

    public DataServiceImpl(DataServer neServer) {
        this.dataServer = neServer;
        this.readIndexExecutor = createReadIndexExecutor();
    }

    private Executor createReadIndexExecutor() {
        final StoreEngineOptions opts = new StoreEngineOptions();
        return StoreEngineHelper.createReadIndexExecutor(opts.getReadIndexCoreThreads());
    }

    private boolean isLeader() {
        return this.dataServer.getFsm().isLeader();
    }

    private String getRedirect() {
        return this.dataServer.redirect().getRedirect();
    }

    private void applyOperation(final DataOperation op, final DataClosure closure) {
        if (!isLeader()) {
            handlerNotLeaderError(closure);
            return;
        }

        try {
            closure.setDataOperation(op);
            final Task task = new Task();
            task.setData(ByteBuffer.wrap(SerializerManager.getSerializer(SerializerManager.Hessian2).serialize(op)));
            task.setDone(closure);
            this.dataServer.getNode().apply(task);
        } catch (CodecException e) {
            String errorMsg = "Fail to encode CounterOperation";
            LOG.error(errorMsg, e);
            closure.failure(StringUtils.EMPTY);
            closure.run(new Status(RaftError.EINTERNAL, errorMsg));
        }
    }

    private void handlerNotLeaderError(final DataClosure closure) {
        closure.failure(getRedirect());
        closure.run(new Status(RaftError.EPERM, "Not leader"));
    }

    private String getValue(String etag) {
        return this.dataServer.getFsm().getValue(etag);
    }


    public static void main(String[] args) {
        System.out.println(NetUtil.getLANAddressOnWindows().getHostAddress());
    }
    @Override
    public void get(boolean readOnlySafe, String etag, DataClosure closure) {
        if(!readOnlySafe){
            //非线性读直接读取结果
            closure.success(getValue(etag));
            closure.run(Status.OK());
            return;
        }
        //线性读取readIndex
        this.dataServer.getNode().readIndex(BytesUtil.EMPTY_BYTES, new ReadIndexClosure() {
            @Override
            public void run(Status status, long index, byte[] reqCtx) {
                if(status.isOk()){
                    String value = getValue(etag);
                    if(value!=null){
                        LOG.info("本地存在该文件:{},在{}",etag,value);
                        //NetUtil.getLANAddressOnWindows().getHostAddress()
                        closure.success(new LocationVo(IpUtils.getIp(RPC_ADDR),Integer.valueOf(OssDataConstant.PORT)));
                    }else{
                        LOG.info("本地不存在该文件:{}",etag);
                        closure.success(null);
                    }
                    closure.run(Status.OK());
                    return;
                }
                DataServiceImpl.this.readIndexExecutor.execute(() -> {
                    if(isLeader()){
                        LOG.debug("Fail to get value with 'ReadIndex': {}, try to applying to the state machine.", status);
                        applyOperation(DataOperation.createGet(readOnlySafe,etag), closure);
                    }else {
                        handlerNotLeaderError(closure);
                    }
                });
            }
        });
    }

    @Override
    public void save(String etag, LocationVo locationVo, DataClosure closure) {
        applyOperation(DataOperation.createSave(etag,locationVo), closure);
    }
    @Override
    public void del(String etag, DataClosure closure) {
        applyOperation(DataOperation.createDel(etag), closure);
    }
}
