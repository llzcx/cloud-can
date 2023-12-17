package ccw.serviceinnovation.oss.manager.mq;

import ccw.serviceinnovation.common.constant.MessageQueueConstant;
import ccw.serviceinnovation.common.constant.StorageTypeEnum;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.entity.bo.ColdMqMessage;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.nacos.Host;
import ccw.serviceinnovation.common.nacos.TrackerService;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.util.http.HttpUtils;
import ccw.serviceinnovation.oss.common.InitApplication;
import ccw.serviceinnovation.oss.constant.OssApplicationConstant;
import ccw.serviceinnovation.oss.manager.consistenthashing.ColdConsistHashing;
import ccw.serviceinnovation.oss.manager.consistenthashing.ConsistentHashing;
import ccw.serviceinnovation.oss.manager.redis.ColdDuplicateRemovalService;
import ccw.serviceinnovation.oss.manager.redis.NorDuplicateRemovalService;
import ccw.serviceinnovation.oss.manager.redis.ObjectStateRedisService;
import ccw.serviceinnovation.oss.mapper.BucketMapper;
import ccw.serviceinnovation.oss.mapper.ColdStorageMapper;
import ccw.serviceinnovation.oss.mapper.OssObjectMapper;
import ccw.serviceinnovation.oss.pojo.bo.MqColdDelTmpBo;
import ccw.serviceinnovation.oss.pojo.bo.MqDelTmpBo;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.cluster.specifyaddress.Address;
import org.apache.dubbo.rpc.cluster.specifyaddress.UserSpecifiedAddressUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.StorageObjectService;
import service.StorageTempObjectService;
import service.raft.client.RaftRpcRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ccw.serviceinnovation.oss.constant.OssApplicationConstant.MQ_ADDR;
import static ccw.serviceinnovation.oss.constant.OssApplicationConstant.NACOS_SERVER_ADDR;

/**
 * @author 陈翔
 */
@Component
@Slf4j
public class ColdConsumer {

    public static final String MQ_FREEZE_GROUP = "oss-freeze-group";
    public static final String MQ_UNFREEZE_GROUP = "oss-unfreeze-group";
    public static final String MQ_DELETE_TMP_GROUP = "oss-delete-tmp-group";
    public static final String MQ_COLD_DELETE_TMP_GROUP = "oss-cold-delete-tmp-group";
    public static final String UNFREEZE_CONSUMER = "unfreeze-consumer";
    public static final String FREEZE_CONSUMER = "freeze-consumer";
    public static final String DELETE_CONSUMER = "delete-consumer";
    public static final String COLD_DELETE_CONSUMER = "cold-delete-consumer";

    @Autowired
    OssObjectMapper ossObjectMapper;

    @Autowired
    BucketMapper bucketMapper;

    @Autowired
    ColdStorageMapper coldStorageMapper;

    @Autowired
    NorDuplicateRemovalService norDuplicateRemovalService;

    @Autowired
    ColdDuplicateRemovalService coldDuplicateRemovalService;

    @Autowired
    ObjectStateRedisService objectStateRedisService;

    public void submitColdDelTask(MqColdDelTmpBo mqColdDelTmpBo) throws Exception{
        String json = JSONObject.toJSONString(mqColdDelTmpBo);
        Message msg = new Message(MessageQueueConstant.TOPIC_COLD_DELETE_TMP,
                json.getBytes(StandardCharsets.UTF_8));
        InitApplication.producer.send(msg);
        log.info("提交cold del任务:{}",json);
    }
    public void initMqUnfreeze() throws Exception {
        //消费解冻:oss-data拿到oss-old-data
        DefaultMQPushConsumer consumer1 = new DefaultMQPushConsumer(MQ_UNFREEZE_GROUP);
        consumer1.setNamesrvAddr(MQ_ADDR);
        consumer1.setInstanceName(UNFREEZE_CONSUMER);
        //订阅某个主题，然后使用tag过滤消息，不过滤可以用*代表
        consumer1.subscribe(MessageQueueConstant.TOPIC_UNFREEZE, "*");
        consumer1.setConsumeMessageBatchMaxSize(1);
        //设置广播消费模式
        consumer1.setMessageModel(MessageModel.CLUSTERING);
        //注册监听回调实现类来处理broker推送过来的消息,MessageListenerConcurrently是并发消费
        consumer1.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {
            for (MessageExt message : messages) {
                try {
                    log.info("unfreeze 开始消费");
                    ColdMqMessage coldMqMessage = JSONObject.parseObject(new String(message.getBody()), ColdMqMessage.class);
                    Long objectId = coldMqMessage.getObjectId();
                    String etag = coldMqMessage.getEtag();
                    OssObject ossObject = ossObjectMapper.selectById(objectId);
                    Bucket bucket = bucketMapper.selectById(ossObject.getBucketId());
                    //检查oss-data是否已经存在这个etag
                    String group = norDuplicateRemovalService.getGroup(etag);
                    //检查oss-cold是否存在这个文件
                    String coldServerName = coldDuplicateRemovalService.getName(etag);
                    if (group == null && coldServerName == null) {
                        //2边度没有数据,无法完成解冻
                        throw new OssException(ResultCode.SERVER_EXCEPTION);
                    } else if (group == null && coldServerName != null) {
                        //oss-data没有 oss-cold有 属于正常情况 走正常流程
                        LocationVo objectNode = ConsistentHashing.getStorageObjectNode(etag);
                        RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR, objectNode.getGroup());
                        //拿到oss-cold的ip+port
                        Host cold = TrackerService.getCold(OssApplicationConstant.NACOS_SERVER_ADDR, coldServerName);
                        //先发送解压请求
                        String unfreezeUrl = "http://" + cold.getIp() + ":" + cold.getPort() + "/cold/unfreeze/" + etag;
                        String token = HttpUtils.requestTo(unfreezeUrl, "GET");
                        //准备好下载路径
                        String url = "http://" + cold.getIp() + ":" + cold.getPort() + "/cold/download/" + token;
                        LocationVo locationVo = new LocationVo(url);
                        //集群内进行同步
                        RaftRpcRequest.save(leader.getCliClientService(), leader.getPeerId(), etag, locationVo);
                        //归档数据-1
                        if (coldDuplicateRemovalService.del(etag) == 0) {
                            //提交mq任务 延迟删除
                            submitColdDelTask(new MqColdDelTmpBo(cold.getIp(),cold.getPort(),token));
                        }
                        //正常数据+1
                        norDuplicateRemovalService.save(etag, objectNode.getGroup());
                        log.info("完成解冻 case:1");
                    } else if (group != null && coldServerName == null) {
                        //oss-data有 oss-cold没有
                        //一种情况是没有冷冻过,一种情况是已经完成了解冻
                        //这就是最终结果
                        log.info("重复消费~");
                        continue;
                    } else if (group != null && coldServerName != null) {
                        //2边都有数据
                        //归档数据-1
                        if (coldDuplicateRemovalService.del(etag) == 0) {
                            Host cold = TrackerService.getCold(OssApplicationConstant.NACOS_SERVER_ADDR, coldServerName);
                            HttpUtils.requestTo("http://" + cold.getIp() + ":" + cold.getPort() + "/cold/delete/" + etag, "DELETE");
                        }
                        //正常数据+1
                        norDuplicateRemovalService.save(etag, group);
                        log.info("完成解冻 case:2");
                    }
                    ossObject.setStorageLevel(StorageTypeEnum.STANDARD.getCode());
                    ossObjectMapper.updateById(ossObject);
                    objectStateRedisService.delState(bucket.getName(),ossObject.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer1.start();//消费者启动完成
        System.out.println("Consumer:unfreeze Started.");
    }
    private Host getServiceInstance(String nacosPath,String group,String etag) throws Exception{
        //先拿到etag所在的leader
        RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(nacosPath, group);
        LocationVo locationVo = RaftRpcRequest.get(leader.getCliClientService(), leader.getPeerId(), etag);
        log.info("{}的group:{}(leader:{})的定位:{}",etag, group,leader.getPeerId(),JSONObject.toJSONString(locationVo));
        if(locationVo==null){
            throw new OssException(ResultCode.OBJECT_IS_DEFECT);
        }
        Map<String, List<Host>> allJraftList = TrackerService.getAllJraftList(nacosPath);
        //某个group的服务列表(rpc-jraft)
        List<Host> hosts = allJraftList.get(group);
        if(hosts!=null){
            for (Host host : hosts) {
                if(host.getIp().equals(locationVo.getIp()) && host.getMetadata().getPort().equals(locationVo.getPort())){
                    return new Host(host.getIp(),host.getMetadata().getPort());
                }
            }
            throw new OssException(ResultCode.SERVER_EXCEPTION);
        }else{
            //数据服务器异常
            throw new OssException(ResultCode.SERVER_EXCEPTION);
        }
    }

    @Autowired
    ColdConsistHashing coldConsistHashing;


    public void initMqFreeze() throws Exception {
        DefaultMQPushConsumer consumer1 = new DefaultMQPushConsumer(MQ_FREEZE_GROUP);
        consumer1.setNamesrvAddr(MQ_ADDR);
        consumer1.setInstanceName(FREEZE_CONSUMER);
        //订阅某个主题，然后使用tag过滤消息，不过滤可以用*代表
        consumer1.subscribe(MessageQueueConstant.TOPIC_FREEZE, "*");
        consumer1.setConsumeMessageBatchMaxSize(1);
        //设置集群模式
        consumer1.setMessageModel(MessageModel.CLUSTERING);
        //注册监听回调实现类来处理broker推送过来的消息,MessageListenerConcurrently是并发消费
        consumer1.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
                for (MessageExt message : messages) {
                    try {
                        log.info("freeze 开始消费");
                        ColdMqMessage coldMqMessage = JSONObject.parseObject(new String(message.getBody()), ColdMqMessage.class);
                        Long objectId = coldMqMessage.getObjectId();
                        String etag = coldMqMessage.getEtag();
                        OssObject ossObject = ossObjectMapper.selectById(objectId);
                        Bucket bucket = bucketMapper.selectById(ossObject.getBucketId());
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
                            log.info("重复消费~");
                            continue;
                        } else if (group != null && coldServerName == null) {
                            //oss-data有 oss-cold没有 正常情况
                            //根据一致性hash寻找存储节点
                            LocationVo cold = ColdConsistHashing.getStorageObjectNode(etag);
                            //获取oss-data的http ip
                            Host ossDataHttpHost = getServiceInstance(NACOS_SERVER_ADDR, group, etag);
                            //给oss-cold发送请求
                            String url = "http://"+cold.getIp()+":"+cold.getPort()+"/cold/freeze/"+ossDataHttpHost.getIp()+"/"+ossDataHttpHost.getPort()+"/"+etag;
                            log.info(url);
                            String res = HttpUtils.requestTo(url, "GET");
                            log.info("res:"+res);
                            coldDuplicateRemovalService.save(etag, cold.getColdStorageName());
                            long del = norDuplicateRemovalService.del(etag);
                            if (del == 0) {
                                RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR, group);
                                RaftRpcRequest.del(leader.getCliClientService(), leader.getPeerId(), etag);
                            }
                            log.info("完成归档 case:1");
                        } else if (group != null && coldServerName != null) {
                            coldDuplicateRemovalService.save(etag, coldServerName);
                            long del = norDuplicateRemovalService.del(etag);
                            if (del == 0) {
                                RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR, group);
                                RaftRpcRequest.del(leader.getCliClientService(), leader.getPeerId(), etag);
                            }
                            log.info("完成归档 case:2");
                        }
                        ossObject.setStorageLevel(StorageTypeEnum.ARCHIVAL.getCode());
                        ossObjectMapper.updateById(ossObject);
                        objectStateRedisService.delState(bucket.getName(),ossObject.getName());
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

    @DubboReference(version = "1.0.0", group = "temp")
    private StorageTempObjectService storageTempObjectService;

    public void initDeleteTmp() throws Exception{
        DefaultMQPushConsumer consumer1 = new DefaultMQPushConsumer(MQ_DELETE_TMP_GROUP);
        consumer1.setNamesrvAddr(MQ_ADDR);
        consumer1.setInstanceName(DELETE_CONSUMER);
        //订阅某个主题，然后使用tag过滤消息，不过滤可以用*代表
        consumer1.subscribe(MessageQueueConstant.TOPIC_DELETE_TMP, "*");
        consumer1.setConsumeMessageBatchMaxSize(1);
        //设置拉取时间间隔
        consumer1.setPullInterval(5000);
        //设置消费次数
        consumer1.setMaxReconsumeTimes(1000);
        //设置集群消费模式
        consumer1.setMessageModel(MessageModel.CLUSTERING);
        //注册监听回调实现类来处理broker推送过来的消息,MessageListenerConcurrently是并发消费
        consumer1.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
                for (MessageExt message : messages) {
//                    log.info("del tmp 开始消费");
                    MqDelTmpBo mqDelTmpBo = JSONObject.parseObject(new String(message.getBody()),MqDelTmpBo.class);
                    String blockToken = mqDelTmpBo.getBlockToken();
                    String ip = mqDelTmpBo.getIp();
                    Integer port = mqDelTmpBo.getPort();
                    try {
                        UserSpecifiedAddressUtil.setAddress(new Address(ip,port, true));
                        Boolean flag = storageTempObjectService.deleteBlockObject(blockToken);
                        log.info("此次消费结果:{}",flag);
                        if (flag){
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }else {
                            if(message.getReconsumeTimes() < consumer1.getMaxReconsumeTimes()){
                                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                            }else{
                                System.out.println("max retry times reached, put message into DLQ:" + new String(message.getBody(), StandardCharsets.UTF_8));
                                // 将重试失败的消息放入死信队列
                                // 此处省略放入死信队列的代码
                                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer1.start();//消费者启动完成
        System.out.println("Consumer:delete tmp Started.");
    }

    public void initColdDeleteTmp() throws Exception{
        DefaultMQPushConsumer consumer1 = new DefaultMQPushConsumer(MQ_COLD_DELETE_TMP_GROUP);
        consumer1.setNamesrvAddr(MQ_ADDR);
        consumer1.setInstanceName(COLD_DELETE_CONSUMER);
        //订阅某个主题，然后使用tag过滤消息，不过滤可以用*代表
        consumer1.subscribe(MessageQueueConstant.TOPIC_COLD_DELETE_TMP, "*");
        consumer1.setConsumeMessageBatchMaxSize(2);
        //设置拉取时间间隔
        consumer1.setPullInterval(5000);
        //设置消费次数
        consumer1.setMaxReconsumeTimes(1000);
        //设置集群消费模式
        consumer1.setMessageModel(MessageModel.CLUSTERING);
        //注册监听回调实现类来处理broker推送过来的消息,MessageListenerConcurrently是并发消费
        consumer1.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
                for (MessageExt message : messages) {
                    log.info("cold del tmp 开始消费");
                    MqColdDelTmpBo mqDelTmpBo = JSONObject.parseObject(new String(message.getBody()),MqColdDelTmpBo.class);
                    String token = mqDelTmpBo.getToken();
                    String ip = mqDelTmpBo.getIp();
                    Integer port = mqDelTmpBo.getPort();
                    try {
                        String delete = HttpUtils.requestTo("http://" + ip + ":" + port + "/cold/delete/" + token, "DELETE");
                        log.info("此次消费结果:{}",delete);
                        if ("true".equals(delete)){
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }else {
                            if(message.getReconsumeTimes() < consumer1.getMaxReconsumeTimes()){
                                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                            }else{
                                System.out.println("max retry times reached, put message into DLQ:" + new String(message.getBody(), StandardCharsets.UTF_8));
                                // 将重试失败的消息放入死信队列
                                // 此处省略放入死信队列的代码
                                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer1.start();//消费者启动完成
        System.out.println("Consumer:cold delete tmp Started.");
    }
}
