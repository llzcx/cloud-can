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


import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.SerializerManager;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.closure.ReadIndexClosure;
import com.alipay.sofa.jraft.entity.Task;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.util.BytesUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.raft.request.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;


/**
 * @author 陈翔
 */
public class DataServiceImpl implements DataService {
    private static final Logger LOG = LoggerFactory.getLogger(DataServiceImpl.class);


    private final DataServer dataServer;


    public DataServiceImpl(DataServer neServer) {
        this.dataServer = neServer;
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

    public void readOnlySafe(JRaftRpcReq req, DataClosure closure) {
        this.dataServer.getNode().readIndex(BytesUtil.EMPTY_BYTES, new ReadIndexClosure() {
            @Override
            public void run(Status status, long index, byte[] reqCtx) {
                if (status.isOk()) {
                    Object res;
                    try {
                        res = ServiceHandler.invoke(req);
                    }catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    closure.success(res);
                    closure.run(Status.OK());
                } else {
                    // 特定情况下，比如发生选举，该读请求将失败
                    closure.failure(status.getErrorMsg());
                }
            }
        });
    }

    private void handlerNotLeaderError(final DataClosure closure) {
        closure.failure(getRedirect());
        closure.run(new Status(RaftError.EPERM, "Not leader"));
    }


    @Override
    public void readDelEvent(ReadDelEventRequest readDelEventRequest, DataClosure closure) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        applyOperation(new DataOperation(readDelEventRequest), closure);
    }

    @Override
    public void readEvent(ReadEventRequest readEventRequest, DataClosure closure) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        applyOperation(new DataOperation(readEventRequest), closure);
    }

    @Override
    public void readFragment(ReadFragmentRequest readFragmentRequest, DataClosure closure) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (readFragmentRequest.isReadOnSafe())
            readOnlySafe(readFragmentRequest, closure);
        else
            applyOperation(new DataOperation(readFragmentRequest), closure);
    }

    @Override
    public void read(ReadRequest readRequest, DataClosure closure) {
        if (readRequest.getReadOnlySafe())
            readOnlySafe(readRequest, closure);
        else
            applyOperation(new DataOperation(readRequest), closure);
    }


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
