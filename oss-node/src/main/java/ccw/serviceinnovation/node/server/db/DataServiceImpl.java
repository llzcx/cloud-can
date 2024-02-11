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
package ccw.serviceinnovation.node.server.db;

import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.node.index.IndexContext;
import ccw.serviceinnovation.node.server.constant.RegisterConstant;
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
import service.raft.request.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;


/**
 * @author likun (saimu.msm@antfin.com)
 */
public class DataServiceImpl implements DataService {
    private static final Logger LOG = LoggerFactory.getLogger(DataServiceImpl.class);


    private final DataServer dataServer;

    private final Executor readIndexExecutor;

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

    private void applyOperation(final DataOperation req, final DataClosure closure) {
        if (!isLeader()) {
            handlerNotLeaderError(closure);
            return;
        }
        try {
            closure.setDataOperation(req);
            final Task task = new Task();
            task.setData(ByteBuffer.wrap(SerializerManager.getSerializer(SerializerManager.Hessian2).serialize(req)));
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


    @Override
    public void readDelEvent(ReadDelEventRequest readDelEventRequest, DataClosure closure) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        boolean readOnlySafe = readDelEventRequest.isReadOnSafe();
        if (!readOnlySafe) {
            //非线性读直接读取结果
            ServiceHandler.invoke(readDelEventRequest);
            return;
        }
        indexReadOrApplyToStateMachine(readDelEventRequest,closure);
    }

    public void indexReadOrApplyToStateMachine(JRaftRpcReq req,DataClosure closure){
        //线性读取readIndex
        this.dataServer.getNode().readIndex(BytesUtil.EMPTY_BYTES, new ReadIndexClosure() {
            @Override
            public void run(Status status, long index, byte[] reqCtx) {
                if (status.isOk()) {
                    Object res;
                    try {
                        res = ServiceHandler.invoke(req);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    closure.success(res);
                    closure.run(Status.OK());
                    return;
                }
                DataServiceImpl.this.readIndexExecutor.execute(() -> {
                    if (isLeader()) {
                        LOG.debug("Fail to get value with 'ReadIndex': {}, try to applying to the state machine.", status);
                        applyOperation(DataOperation.create(req), closure);
                    } else {
                        handlerNotLeaderError(closure);
                    }
                });
            }
        });
    }

    @Override
    public void readEvent(ReadEventRequest readEventRequest, DataClosure closure) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        boolean readOnlySafe = readEventRequest.isReadOnSafe();
        if (!readOnlySafe) {
            //非线性读直接读取结果
            ServiceHandler.invoke(readEventRequest);
            return;
        }
        indexReadOrApplyToStateMachine(readEventRequest,closure);
    }

    @Override
    public void readFragment(ReadFragmentRequest readFragmentRequest, DataClosure closure) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        boolean readOnlySafe = readFragmentRequest.isReadOnSafe();
        if (!readOnlySafe) {
            //非线性读直接读取结果
            ServiceHandler.invoke(readFragmentRequest);
            return;
        }
        indexReadOrApplyToStateMachine(readFragmentRequest,closure);
    }


    //其他请求都需要应用到状态机

    @Override
    public void del(DelRequest delRequest, DataClosure closure) {
        applyOperation(new DataOperation(delRequest), closure);
    }

    @Override
    public void upload(UploadRequest uploadRequest, DataClosure closure) {
        applyOperation(new DataOperation(uploadRequest), closure);
    }

    @Override
    public void writeEvent(WriteEventRequest writeEventRequest, DataClosure closure) {
        applyOperation(new DataOperation(writeEventRequest), closure);
    }

    @Override
    public void writeFragment(WriteFragmentRequest writeFragmentRequest, DataClosure closure) {
        applyOperation(new DataOperation(writeFragmentRequest), closure);
    }

    @Override
    public void writeDelEvent(WriteDelEventRequest writeDelEventRequest, DataClosure closure) {
        applyOperation(new DataOperation(writeDelEventRequest), closure);
    }

    @Override
    public void writeMerge(WriterMergeRequest writerMergeRequest, DataClosure closure) {
        applyOperation(new DataOperation(writerMergeRequest), closure);
    }
}
