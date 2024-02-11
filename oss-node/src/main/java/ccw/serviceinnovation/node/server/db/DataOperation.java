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

import lombok.Data;
import service.raft.request.ReadDelEventRequest;
import service.raft.request.JRaftRpcReq;

import java.io.Serializable;

/**
 * The counter operation
 *
 * @author likun (saimu.msm@antfin.com)
 */

@Data
public class DataOperation implements Serializable {

    private static final long serialVersionUID = -6597003954824547294L;

    public Boolean onlyRead;
    public JRaftRpcReq request;

    public static DataOperation create(JRaftRpcReq request) {
        if(request instanceof ReadDelEventRequest){
            ReadDelEventRequest readDelEventRequest = (ReadDelEventRequest) request;
            return new DataOperation(readDelEventRequest, readDelEventRequest.isReadOnSafe());
        }else{
            return new DataOperation(request);
        }
    }


    public DataOperation(JRaftRpcReq request) {
        onlyRead = false;
        this.request = request;
    }

    public DataOperation(JRaftRpcReq request,Boolean onlyRead) {
        this.request = request;
        this.onlyRead = onlyRead;
    }

    public boolean isRead(){
        return onlyRead;
    }

}
