package ccw.serviceinnovation.oss.manager.mq;

import ccw.serviceinnovation.common.constant.StorageTypeEnum;
import ccw.serviceinnovation.common.entity.ColdStorage;
import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.entity.bo.ColdMqMessage;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.nacos.Host;
import ccw.serviceinnovation.common.nacos.TrackerService;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.util.http.HttpUtils;
import ccw.serviceinnovation.oss.constant.OssApplicationConstant;
import ccw.serviceinnovation.oss.manager.consistenthashing.ConsistentHashing;
import ccw.serviceinnovation.oss.manager.redis.ColdDuplicateRemovalService;
import ccw.serviceinnovation.oss.manager.redis.NorDuplicateRemovalService;
import ccw.serviceinnovation.oss.mapper.ColdStorageMapper;
import ccw.serviceinnovation.oss.mapper.OssObjectMapper;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.error.RemotingException;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.raft.client.RaftRpcRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author 陈翔
 */
@Component
public class ColdConsumer {

    @Autowired
    OssObjectMapper ossObjectMapper;

    @Autowired
    ColdStorageMapper coldStorageMapper;

    @Autowired
    NorDuplicateRemovalService norDuplicateRemovalService;

    @Autowired
    ColdDuplicateRemovalService coldDuplicateRemovalService;

    public void initMqUnfreeze() throws Exception {
        //消费解冻:oss-data拿到oss-old-data
        DefaultMQPushConsumer consumer1 = new DefaultMQPushConsumer(
                "oss-group");
        consumer1.setNamesrvAddr("127.0.0.1:9876");
        consumer1.setInstanceName("unfreeze-consumer");
        //订阅某个主题，然后使用tag过滤消息，不过滤可以用*代表
        consumer1.subscribe("Topic-unfreeze", "*");
        consumer1.setConsumeMessageBatchMaxSize(1);
        //设置广播消费模式
        consumer1.setMessageModel(MessageModel.BROADCASTING);
        //注册监听回调实现类来处理broker推送过来的消息,MessageListenerConcurrently是并发消费
        consumer1.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
                for (MessageExt message : messages) {
                    try {
                        ColdMqMessage coldMqMessage = JSONObject.parseObject(new String(message.getBody()), ColdMqMessage.class);
                        Long objectId = coldMqMessage.getObjectId();
                        String etag = coldMqMessage.getEtag();
                        OssObject ossObject = ossObjectMapper.selectById(objectId);
                        //检查oss-data是否已经存在这个etag
                        String group = norDuplicateRemovalService.getGroup(etag);
                        //检查oss-cold是否存在这个文件
                        String coldServerName = coldDuplicateRemovalService.getName(etag);
                        if (group == null && coldServerName == null) {
                            //2边度没有数据,无法完成解冻
                            throw new OssException(ResultCode.SERVER_EXCEPTION);
                        } else if (group == null && coldServerName != null) {
                            //oss-data没有 oss-cold有 属于正常情况 走正常流程
                            RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR, group);
                            //拿到oss-cold的ip+port
                            Host cold = TrackerService.getCold(OssApplicationConstant.NACOS_SERVER_ADDR, coldServerName);
                            String url = "http://" + cold.getIp() + ":" + cold.getPort() + "/cold/unfreeze/" + etag;
                            LocationVo locationVo = new LocationVo(url);
                            RaftRpcRequest.save(leader.getCliClientService(), leader.getPeerId(), etag, locationVo);
                            //归档数据-1
                            if (coldDuplicateRemovalService.del(etag) == 0) {
                                HttpUtils.requestTo("http://" + cold.getIp() + ":" + cold.getPort() + "/cold/delete/" + etag, "delete");
                            }
                            //正常数据+1
                            norDuplicateRemovalService.save(etag, group);
                        } else if (group != null && coldServerName == null) {
                            //oss-data有 oss-cold没有
                            //一种情况是没有冷冻过,一种情况是已经完成了解冻
                            //这就是最终结果
                        } else if (group != null && coldServerName != null) {
                            //2边都有数据
                            //归档数据-1
                            if (coldDuplicateRemovalService.del(etag) == 0) {
                                Host cold = TrackerService.getCold(OssApplicationConstant.NACOS_SERVER_ADDR, coldServerName);
                                HttpUtils.requestTo("http://" + cold.getIp() + ":" + cold.getPort() + "/cold/delete/" + etag, "delete");
                            }
                            //正常数据+1
                            norDuplicateRemovalService.save(etag, group);
                        }
                        ossObject.setStorageLevel(StorageTypeEnum.STANDARD.getCode());
                        ossObjectMapper.updateById(ossObject);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer1.start();//消费者启动完成
        System.out.println("Consumer:freeze Started.");
    }

    public void initMqFreeze() throws Exception {
        DefaultMQPushConsumer consumer1 = new DefaultMQPushConsumer(
                "oss-group");
        consumer1.setNamesrvAddr("127.0.0.1:9876");
        consumer1.setInstanceName("freeze-consumer");
        //订阅某个主题，然后使用tag过滤消息，不过滤可以用*代表
        consumer1.subscribe("Topic-freeze", "*");
        consumer1.setConsumeMessageBatchMaxSize(1);
        //设置广播消费模式
        consumer1.setMessageModel(MessageModel.BROADCASTING);
        //注册监听回调实现类来处理broker推送过来的消息,MessageListenerConcurrently是并发消费
        consumer1.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
                for (MessageExt message : messages) {
                    try {
                        ColdMqMessage coldMqMessage = JSONObject.parseObject(new String(message.getBody()), ColdMqMessage.class);
                        Long objectId = coldMqMessage.getObjectId();
                        String etag = coldMqMessage.getEtag();
                        OssObject ossObject = ossObjectMapper.selectById(objectId);
                        //检查oss-data是否已经存在这个etag
                        String group = norDuplicateRemovalService.getGroup(etag);
                        //检查oss-cold是否存在这个文件
                        String coldServerName = coldDuplicateRemovalService.getName(etag);
                        if (group == null && coldServerName == null) {
                            //2边度没有数据,无法完成归档
                            throw new OssException(ResultCode.SERVER_EXCEPTION);
                        } else if (group == null && coldServerName != null) {
                            //oss-data有 oss-cold没有
                            //一种情况是没有归档过,一种情况是已经完成了归档
                            //这就是最终结果
                        } else if (group != null && coldServerName == null) {
                            //oss-data有 oss-cold没有
                            //获取oss-cold的ip
                            Host cold = TrackerService.getCold(OssApplicationConstant.NACOS_SERVER_ADDR, coldServerName);
                            //获取oss-data的ip
                            RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR, group);
                            PeerId peerId = leader.getPeerId();
                            //给oss-cold发送请求
                            HttpUtils.requestTo("http://"+cold.getIp()+":"+cold.getPort()+"/cold/freeze/"+peerId.getIp()+"/"+peerId.getPort()+"/"+etag,"get");
                            coldDuplicateRemovalService.save(etag, group);
                            long del = norDuplicateRemovalService.del(etag);
                            if (del == 0) {
                                RaftRpcRequest.del(leader.getCliClientService(), leader.getPeerId(), etag);
                            }
                        } else if (group != null && coldServerName != null) {
                            coldDuplicateRemovalService.save(etag, group);
                            long del = norDuplicateRemovalService.del(etag);
                            if (del == 0) {
                                RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR, group);
                                RaftRpcRequest.del(leader.getCliClientService(), leader.getPeerId(), etag);
                            }
                        }
                        ossObject.setStorageLevel(StorageTypeEnum.ARCHIVAL.getCode());
                        ossObjectMapper.updateById(ossObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer1.start();//消费者启动完成
        System.out.println("Consumer:freeze Started.");
    }
}
