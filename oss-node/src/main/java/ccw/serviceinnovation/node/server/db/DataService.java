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

import service.raft.request.*;

/**
 * @author 陈翔
 */
public interface DataService {

    /**
     * 获取某个对象
     */
    void get(GetRequest getRequest,DataClosure closure);

    /**
     * 删除某个对象
     */
    void del(DelRequest delRequest,DataClosure closure);

    /**
     * 上传对象
     */
    void upload(UploadRequest uploadRequest,DataClosure closure);

    /**
     *创建分片上传事件
     */
    void event(EventRequest eventRequest,DataClosure closure);

    /**
     * 上传某个分块
     */
    void fragment(FragmentRequest fragmentRequest,DataClosure closure);

    /**
     * 删除分片上传的事件
     */
    void delEvent(DelEventRequest delEventRequest,DataClosure closure);

    /**
     * 合并分块
     */
    void merge(MergeRequest mergeRequest,DataClosure closure);


}