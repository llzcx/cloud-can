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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author 陈翔
 */
public interface DataService {
    void del(DelRequest delRequest,DataClosure closure);
    void readDelEvent(ReadDelEventRequest readDelEventRequest, DataClosure closure) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException;
    void readEvent(ReadEventRequest readEventRequest, DataClosure closure) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;
    void readFragment(ReadFragmentRequest readFragmentRequest, DataClosure closure) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;
    void read(ReadRequest readRequest,DataClosure closure) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    void upload(UploadRequest uploadRequest,DataClosure closure);

    void writeDelEvent(WriteDelEventRequest writeDelEventRequest, DataClosure closure);
    void writeEvent(WriteEventRequest writeEventRequest, DataClosure closure);
    void writeFragment(WriteFragmentRequest writeFragmentRequest, DataClosure closure);
    void writeMerge(WriterMergeRequest writerMergeRequest, DataClosure closure);


}