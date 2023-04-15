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
import ccw.serviceinnovation.common.util.IpUtils;
import ccw.serviceinnovation.common.util.http.FileUtil;
import ccw.serviceinnovation.ossdata.constant.OssDataConstant;
import com.alibaba.fastjson.JSONObject;
import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.SerializerManager;
import com.alipay.sofa.jraft.Iterator;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.core.StateMachineAdapter;
import com.alipay.sofa.jraft.error.RaftException;
import com.alipay.sofa.jraft.util.NamedThreadFactory;
import com.alipay.sofa.jraft.util.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

import static ccw.serviceinnovation.ossdata.constant.FilePrefixConstant.FILE_NOR;
import static ccw.serviceinnovation.ossdata.constant.OssDataConstant.RPC_ADDR;
import static ccw.serviceinnovation.ossdata.manager.raft.server.DataOperation.*;


/**
 * Counter state machine.
 * @author boyan (boyan@alibaba-inc.com)
 *
 * 2018-Apr-09 4:52:31 PM
 */
public class DataStateMachine extends StateMachineAdapter {

    public static final ConcurrentHashMap<String,String> dataMap = new ConcurrentHashMap<>();

    private static final Logger       LOG        = LoggerFactory.getLogger(DataStateMachine.class);

    private static ThreadPoolExecutor executor   = ThreadPoolUtil
                                                     .newBuilder()
                                                     .poolName("JRAFT_TEST_EXECUTOR")
                                                     .enableMetric(true)
                                                     .coreThreads(3)
                                                     .maximumThreads(5)
                                                     .keepAliveSeconds(60L)
                                                     .workQueue(new SynchronousQueue<>())
                                                     .threadFactory(
                                                         new NamedThreadFactory("JRaft-Test-Executor-", true)).build();
    /**
     * Leader term
     */
    private final AtomicLong          leaderTerm = new AtomicLong(-1);

    public String getValue(String etag) {
        return dataMap.get(etag);
    }

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

                String etag = dataOperation.getEtag();
                LOG.info("此次操作:{},etag:{}",dataOperation.getOp(),etag);
                String filePath = OssDataConstant.POSITION +"\\"+FILE_NOR+etag;
                switch (dataOperation.getOp()) {
                    case GET:
                        //返回HTTP接口位置
                        LocationVo locationVo1 = new LocationVo(IpUtils.getIp(RPC_ADDR),Integer.valueOf(OssDataConstant.PORT));
                        locationVo1.setGroup(OssDataConstant.GROUP);
                        if(dataMap.get(etag)!=null){
                            returnData = locationVo1;
                        }else{
                            returnData = locationVo1;
                        }
                        break;
                    case SAVE:
                        long start = System.currentTimeMillis();
                        try {
                            LocationVo locationVo = dataOperation.getLocationVo();
                            //从网络地址中保存文件
                            if(FileUtil.saveFile(locationVo.getPath(),filePath)){
                                LOG.info("同步完成!");
                                dataMap.put(etag,filePath);
                                returnData = "save ok from" +locationVo.getPath();
                            }else{
                                //缓存数据已经丢失,选择其他方式同步
                                LOG.info("缓存数据丢失!即将从其他节点复制");
                                //遍历group内所有节点
                                boolean flag = false;
                                String[] split = OssDataConstant.CLUSTER.split(",");
                                for (String url : split) {
                                    String urlPath = "http://"+url+ "/object/download/group1/"+etag;
                                    if(FileUtil.saveFile(urlPath,filePath)){
                                        flag = true;
                                        dataMap.put(etag,filePath);
                                        returnData = "save ok from " + urlPath;
                                        break;
                                    }
                                }
                                if(!flag){
                                    //如果没有从任何一个节点同步到数据,这部分日志就会丢失
                                    //其他任何一个节点都没有此etag 说明此etag
                                    LOG.error("同步任务失败!!!");
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case DEL:
                        //在map当中删除
                        dataMap.remove(etag);
                        //删除本地文件
                        File file = new File(filePath);
                        if(file.exists()){
                            file.delete();
                            returnData = "true";
                        }else{
                            returnData = "false";
                        }
                        LOG.info("本地map和文件删除结果:{}",returnData);
                    default:
                        break;
                }
//                LOG.info("状态机最终状态:"+JSONObject.toJSON(printfState(dataMap)));
                if (closure != null) {
                    closure.success(returnData);
                    closure.run(Status.OK());
                }
            }
            iter.next();
        }
    }

    /**
     * 打印
     * @param mp
     */
    public String printfState(Map<String,String> mp){
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
