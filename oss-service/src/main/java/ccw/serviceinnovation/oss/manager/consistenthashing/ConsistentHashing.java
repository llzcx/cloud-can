package ccw.serviceinnovation.oss.manager.consistenthashing;

import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.nacos.Host;
import ccw.serviceinnovation.common.nacos.TrackerService;
import ccw.serviceinnovation.oss.constant.OssApplicationConstant;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.cluster.specifyaddress.Address;
import org.apache.dubbo.rpc.cluster.specifyaddress.UserSpecifiedAddressUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.StorageObjectService;
import service.raft.client.RaftRpcRequest;

import java.util.*;

/**
 * 一致性hash
 * 陈翔
 **/
@Component
public class ConsistentHashing {

    // 物理节点
    public static Set<String> physicalNodes = new TreeSet<String>();

    /**
     * 虚拟节点
     */
    // 物理节点至虚拟节点的复制倍数
    private final int VIRTUAL_COPIES = 1048576;
    // 哈希值 => 物理节点
    private static TreeMap<Long, String> virtualNodes = new TreeMap<>();

    // 32位的 Fowler-Noll-Vo 哈希算法
    // https://en.wikipedia.org/wiki/Fowler–Noll–Vo_hash_function
    private static Long FNVHash(String key) {
        final int p = 16777619;
        Long hash = 2166136261L;
        for (int idx = 0, num = key.length(); idx < num; ++idx) {
            hash = (hash ^ key.charAt(idx)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }

    // 根据物理节点，构建虚拟节点映射表
    public ConsistentHashing() {
        for (String nodeIp : physicalNodes) {
            addPhysicalNode(nodeIp);
        }
    }

    // 添加物理节点
    public void addPhysicalNode(String nodeIp) {
        for (int idx = 0; idx < VIRTUAL_COPIES; ++idx) {
            long hash = FNVHash(nodeIp + "#" + idx);
            virtualNodes.put(hash, nodeIp);
        }
    }

    // 删除物理节点
    public void removePhysicalNode(String nodeIp) {
        for (int idx = 0; idx < VIRTUAL_COPIES; ++idx) {
            long hash = FNVHash(nodeIp + "#" + idx);
            virtualNodes.remove(hash);
        }
    }

    /**
     * 查找对象映射的节点(在哪个节点有存储)
     *
     * @param etag
     * @return 返回节点存储位置 不存在则返回null
     */
    public static LocationVo getObjectNode(String etag, String groupId) throws Exception {
        RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR,groupId);
        LocationVo locationVo = RaftRpcRequest.get(leader.getCliClientService(), leader.getPeerId(), etag);
        return locationVo;
    }

    /**
     * 查看ossdata是否存在一个etag
     * @param etag
     * @return
     * @throws Exception
     */
    public static LocationVo getObjectNode(String etag) throws Exception {
        Map<String, List<Host>> allJraftList = TrackerService.getAllJraftList(OssApplicationConstant.NACOS_SERVER_ADDR);
        for (Map.Entry<String, List<Host>> stringListEntry : allJraftList.entrySet()) {
            String group = stringListEntry.getKey();
            LocationVo objectNode = getObjectNode(etag, group);
            if(objectNode!=null){
                return objectNode;
            }
        }
        return null;
    }

    /**
     * 查找对象存储的节点
     *
     * @param object
     * @return
     */
    public static LocationVo getStorageObjectNode(String object) {
        long hash = FNVHash(object);
        SortedMap<Long, String> tailMap = virtualNodes.tailMap(hash); // 所有大于 hash 的节点
        Long key = tailMap.isEmpty() ? virtualNodes.firstKey() : tailMap.firstKey();
        String addr = virtualNodes.get(key);
        RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR,addr);
        //查找leader-group对应的端口信息
        Map<String, List<Host>> allJraftList = TrackerService.getAllJraftList(OssApplicationConstant.NACOS_SERVER_ADDR);
        List<Host> list = allJraftList.get(addr);
        Integer applicationPort = null;
        for (Host host : list) {
            if(host.getPort().equals(leader.getPeerId().getPort())){
                applicationPort = host.getMetadata().getPort();
            }
        }
        LocationVo locationVo = new LocationVo(leader.getPeerId().getIp(), applicationPort);
        locationVo.setGroup(addr);
        return locationVo;
    }

    /**
     * 统计对象与节点的映射关系
     *
     * @param label
     * @param objectMin
     * @param objectMax
     */
    public void dumpObjectNodeMap(String label, int objectMin, int objectMax) {
        // 统计  IP => COUNT
        Map<String, Integer> objectNodeMap = new TreeMap<>();
        for (int object = objectMin; object <= objectMax; ++object) {
            LocationVo storageObjectNode = getStorageObjectNode(Integer.toString(object));
            String nodeIp = storageObjectNode.getIp() + ":" + storageObjectNode.getPort();
            Integer count = objectNodeMap.get(nodeIp);
            objectNodeMap.put(nodeIp, (count == null ? 0 : count + 1));
        }
        // 打印
        double totalCount = objectMax - objectMin + 1;
        System.out.println("======== " + label + " ========");
        for (Map.Entry<String, Integer> entry : objectNodeMap.entrySet()) {
            long percent = (int) (100 * entry.getValue() / totalCount);
            System.out.println("IP=" + entry.getKey() + ": RATE=" + percent + "%");
        }
    }

    public static void main(String[] args) {
        ConsistentHashing ch = new ConsistentHashing();

        // 初始情况
        ch.dumpObjectNodeMap("初始情况", 0, 65536);

        // 删除物理节点
        ch.removePhysicalNode("192.168.1.103");
        ch.dumpObjectNodeMap("删除物理节点", 0, 65536);

        // 添加物理节点
        ch.addPhysicalNode("192.168.1.108");
        ch.dumpObjectNodeMap("添加物理节点", 0, 65536);
    }


}