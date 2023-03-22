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
package ccw.serviceinnovation.ossdata.manager.raft.server;

import ccw.serviceinnovation.common.entity.LocationVo;
import lombok.Data;

import java.io.Serializable;

/**
 * The counter operation
 *
 * @author likun (saimu.msm@antfin.com)
 */

@Data
public class DataOperation implements Serializable {

    private static final long serialVersionUID = -6597003954824547294L;

    /** Get value */
    public static final byte SAVE = 0x01;
    public static final byte DEL = 0x02;
    public static final byte GET = 0x03;
    private byte op;
    private String etag;
    private LocationVo locationVo;

    public static DataOperation createSave(String etag,LocationVo locationVo) {
        return new DataOperation(SAVE,etag,locationVo);
    }
    public static DataOperation createDel(String etag) {
        return new DataOperation(DEL, etag);
    }
    public static DataOperation createGet(boolean readOnSafe,String etag) {
        return new DataOperation(GET, etag);
    }

    public DataOperation(byte op, String etag,LocationVo locationVo) {
        this.op = op;
        this.etag = etag;
        this.locationVo = locationVo;
    }
    public DataOperation(byte op, String etag) {
        this.op = op;
        this.etag = etag;
    }
    public boolean isRead(){
        return op==GET;
    }

}
