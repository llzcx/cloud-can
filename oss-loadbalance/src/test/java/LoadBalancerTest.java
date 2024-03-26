import ccw.serviceinnovation.loadbalance.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 负载均衡测试
 */
public class LoadBalancerTest {
    public static LoadBalancer LoadBalancerFactory() {
        return new ConsistentHashLoadBalancer();
    }

    public static void main(String[] args) {
        int count = 10;
        double base = 1;
        double speed = 0.001;
        loadBalancer(count, base, speed);
    }

    public static void loadBalancer(int count, double base, double speed) {
        String title = "count:" + count + ",base:" + base + "speed:" + speed;
        // 创建服务器列表
        List<Server> servers = LoadBalancerUtils.createServers(20, 0.1, 0.03);
        long time = System.currentTimeMillis();

        // 创建 CrushLoadBalancer 对象
        LoadBalancer loadBalancer = LoadBalancerFactory();

        // 创建并输出 100W 个 Invocation
        List<Invocation> invocations = LoadBalancerUtils.createInvocations(1000000);

        // 对每个 Invocation 进行分类，并输出结果
        System.out.println("\nClassification Result:");
        classifyInvocations(title, loadBalancer, servers, invocations);
        System.out.println("need time:" + (System.currentTimeMillis() - time));
    }


    // 对 Invocation 进行分类，并输出结果
    private static void classifyInvocations(String title, LoadBalancer loadBalancer, List<Server> servers, List<Invocation> invocations) {
        HashMap<String, Integer> res = new HashMap<>();
        int total = invocations.size();
        loadBalancer.preheat(servers);
        for (Invocation invocation : invocations) {
            Server selectedServer = loadBalancer.select(invocation);
            int count = res.computeIfAbsent(selectedServer.getId(), k -> 0);
            res.put(selectedServer.getId(), count + 1);
        }
        List<Double> xData = new ArrayList<>();
        for (Server server : servers) xData.add(server.getWight());
        List<Double> yData = new ArrayList<>();
        for (Server server : servers) {
            Integer count = res.computeIfAbsent(server.getId(), k -> 0);
            double rate = count * 100.0 / total;
            yData.add(rate);
            System.out.println("serverName: " + server.getId() + ",wight is " + String.format("%.2f",server.getWight()) + ", count is " + count + ", rate is " + String.format("%.2f", rate) + "%.");
        }
        DrawUtil.draw("负载均衡测试:" + title, "权重", xData, "分配结果", yData);
    }
}
