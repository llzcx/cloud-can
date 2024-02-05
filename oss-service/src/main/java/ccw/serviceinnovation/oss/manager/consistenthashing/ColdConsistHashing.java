package ccw.serviceinnovation.oss.manager.consistenthashing;

import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.nacos.Host;
import ccw.serviceinnovation.common.nacos.TrackerService;
import ccw.serviceinnovation.oss.constant.OssApplicationConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import service.raft.client.RaftRpcRequest;

import java.util.*;

import static ccw.serviceinnovation.oss.constant.OssApplicationConstant.NACOS_SERVER_ADDR;

/**
 * @author 陈翔
 */
@Component
@Slf4j
public class ColdConsistHashing {
    // 物理节点
    public static Set<String> physicalNodes = new TreeSet<>();

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
    public ColdConsistHashing() {
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
     * @param etag
     * @return 返回节点存储位置 不存在则返回null
     */
    public static LocationVo getObjectNode(String etag, String groupId) throws Exception {
        RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(NACOS_SERVER_ADDR,groupId);
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
        Map<String, List<Host>> allJraftList = TrackerService.getAllJraftList(NACOS_SERVER_ADDR);
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
     * 根据etag进行hash落到某个cold-name上 在group内寻找rpc-raft服务的leader 根据leader的port推出上传服务的位置
     * @param etag
     * @return
     */
    public static LocationVo getStorageObjectNode(String etag) {
        long hash = FNVHash(etag);
        // 所有大于 hash 的节点
        SortedMap<Long, String> tailMap = virtualNodes.tailMap(hash);
        Long key = tailMap.isEmpty() ? virtualNodes.firstKey() : tailMap.firstKey();
        String coldStorageName = virtualNodes.get(key);
        log.info("找到的:{}",coldStorageName);
        //通过cold_storage_name在 nacos 上发现服务的ip
        List<Host> coldList = TrackerService.getColdList(NACOS_SERVER_ADDR);
        for (Host host : coldList) {
            String name = host.getMetadata().getCold_storage_name();
            if(name.equals(coldStorageName)){
                LocationVo locationVo = new LocationVo(host.getIp(), host.getPort());
                locationVo.setColdStorageName(coldStorageName);
                return locationVo;
            }
        }
        return null;
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
