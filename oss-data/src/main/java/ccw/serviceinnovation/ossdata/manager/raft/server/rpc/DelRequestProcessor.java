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
package ccw.serviceinnovation.ossdata.manager.raft.server.rpc;

import ccw.serviceinnovation.ossdata.manager.raft.server.DataClosure;
import ccw.serviceinnovation.ossdata.manager.raft.server.service.DataService;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import service.raft.request.DelRequest;

/**
 * @author 陈翔
 */
public class DelRequestProcessor implements RpcProcessor<DelRequest> {
    private final DataService dataService;

    public DelRequestProcessor(DataService neService) {
        super();
        this.dataService = neService;
    }

    @Override
    public void handleRequest(final RpcContext rpcCtx, final DelRequest request) {
        final DataClosure closure = new DataClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getResponse());
            }
        };
        this.dataService.del(request.getEtag(), closure);
    }

    @Override
    public String interest() {
        return DelRequest.class.getName();
    }
}
