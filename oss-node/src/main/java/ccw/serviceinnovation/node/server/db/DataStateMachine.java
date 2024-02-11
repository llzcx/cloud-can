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
import com.alipay.sofa.jraft.Iterator;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.core.StateMachineAdapter;
import com.alipay.sofa.jraft.error.RaftException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Counter state machine.
 *
 * @author boyan (boyan@alibaba-inc.com)
 * <p>
 * 2018-Apr-09 4:52:31 PM
 */
public class DataStateMachine extends StateMachineAdapter {


    private static final Logger LOG = LoggerFactory.getLogger(DataStateMachine.class);
    /**
     * Leader term
     */
    private final AtomicLong leaderTerm = new AtomicLong(-1);


    public boolean isLeader() {
        return this.leaderTerm.get() > 0;
    }


    @Override
    public void onApply(final Iterator iter) {

        while (iter.hasNext()) {
            Object returnData = null;
            DataOperation dataOperation = null;
            DataClosure closure = null;
            if (iter.done() != null) {
                // This task is applied by this node, get value from closure to avoid additional parsing.
                closure = (DataClosure) iter.done();
                dataOperation = closure.getDataOperation();
            } else {
                // Have to parse FetchAddRequest from this user log.
                final ByteBuffer data = iter.getData();
                try {
                    dataOperation = SerializerManager.getSerializer(SerializerManager.Hessian2).deserialize(data.array(),
                            DataOperation.class.getName());
                } catch (final CodecException e) {
                    LOG.error("Fail to decode DataRequest", e);
                }
                // follower ignore read operation
                if (dataOperation != null && dataOperation.isRead()) {
                    iter.next();
                    continue;
                }
            }
            if (dataOperation != null) {
                try {
                    returnData = ServiceHandler.invoke(dataOperation.request);
                    if (closure != null) {
                        closure.success(returnData);
                        closure.run(Status.OK());
                    }
                } catch (Exception e) {
                    if (closure != null) {
                        closure.failure("ERROR");
                    }
                    e.printStackTrace();
                }
            }
            iter.next();
        }
    }

    /**
     * 打印
     *
     * @param mp
     */
    public String printfState(Map<String, String> mp) {
        StringBuilder stringBuilder = new StringBuilder("\n");
        for (Map.Entry<String, String> unit : mp.entrySet()) {
            stringBuilder.append(unit.getKey()).append("=========>").append(unit.getValue()).append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public void onError(final RaftException e) {
        LOG.error("Raft error: {}", e, e);
    }

    @Override
    public void onLeaderStart(final long term) {
        this.leaderTerm.set(term);
        super.onLeaderStart(term);
    }

    @Override
    public void onLeaderStop(final Status status) {
        this.leaderTerm.set(-1);
        super.onLeaderStop(status);
    }

}
