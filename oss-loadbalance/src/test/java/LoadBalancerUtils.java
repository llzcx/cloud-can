import ccw.serviceinnovation.loadbalance.Invocation;
import ccw.serviceinnovation.loadbalance.OssGroup;
import ccw.serviceinnovation.loadbalance.Server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoadBalancerUtils {
    // 生成随机的 MD5 值
    public static String generateRandomMD5() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] randomBytes = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(randomBytes);
            md.update(randomBytes);
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            StringBuilder md5Value = new StringBuilder(bigInt.toString(16));
            while (md5Value.length() < 32) {
                md5Value.insert(0, "0");
            }
            return md5Value.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 生成随机的 IPv4 地址和端口号的组合
    public static String generateRandomEndpoint() {
        Random random = new Random();

        // 生成随机的 IPv4 地址
        String ipAddress = random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256);

        // 生成随机的端口号
        int port = random.nextInt(65536);

        return ipAddress + ":" + port;
    }

    // 生成指定数量的节点列表
    public static List<String> generateNodeList(int numNodes) {
        List<String> nodeList = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            nodeList.add(generateRandomEndpoint());
        }
        return nodeList;
    }

    // 创建指定数量的 Invocation
    public static List<Invocation> createInvocations(int numInvocations) {
        List<Invocation> invocations = new ArrayList<>();
        for (int i = 0; i < numInvocations; i++) {
            invocations.add(new Invocation(generateRandomMD5()));
        }
        return invocations;
    }

    // 创建服务器列表
    public static List<Server> createServers(int count,double base,double speed) {
        List<Server> servers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            servers.add(new OssGroup("group" + (i+1), LoadBalancerUtils.generateNodeList(10), base + i*speed));
        }
        return servers;
    }

}
