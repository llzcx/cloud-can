import ccw.serviceinnovation.loadbalance.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 迁移率测试
 */
public class MigrationTest {

    public static LoadBalancer LoadBalancerFactory(){
        return new CrushLoadBalancer();
    }

    public static void main(String[] args) {
        List<Server> servers = LoadBalancerUtils.createServers(10,0.1,0.32);

        List<Invocation> invocations = LoadBalancerUtils.createInvocations(1000000);

        LoadBalancer loadBalancer = LoadBalancerFactory();
        loadBalancer.preheat(servers);

        testAdd(loadBalancer, servers, invocations,0.21);

        testRemove(loadBalancer, servers, invocations);
    }

    public static void testAdd(LoadBalancer loadBalancer, List<Server> servers, List<Invocation> invocations,double wight) {
        List<Server> newServers = new ArrayList<>(servers);
        newServers.add(new OssGroup("newGroup", LoadBalancerUtils.generateNodeList(10), wight));
        LoadBalancer newLoadBalancer = LoadBalancerFactory();
        newLoadBalancer.preheat(newServers);

        int count = 0;
        int total = invocations.size();

        for (Invocation invocation : invocations) {
            if (loadBalancer.select(invocation) != newLoadBalancer.select(invocation)) count++;
        }

        System.out.println("add node mobility:" + String.format("%.2f", count * 1.0 / total));
    }

    public static void testRemove(LoadBalancer loadBalancer, List<Server> servers, List<Invocation> invocations) {
        List<Server> newServers = new ArrayList<>(servers);
        newServers.remove(newServers.size() - 1);
        LoadBalancer newLoadBalancer = LoadBalancerFactory();
        newLoadBalancer.preheat(newServers);

        int count = 0;
        int total = invocations.size();

        for (Invocation invocation : invocations) {
            if (loadBalancer.select(invocation) != newLoadBalancer.select(invocation)) count++;
        }

        System.out.println("remove node mobility:" + String.format("%.2f", count * 1.0 / total));
    }
}
