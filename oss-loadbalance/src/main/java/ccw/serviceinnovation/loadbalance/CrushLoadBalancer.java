package ccw.serviceinnovation.loadbalance;

import ccw.serviceinnovation.hash.HashStrategy;
import ccw.serviceinnovation.hash.MurmurHashStrategy;

import java.util.ArrayList;
import java.util.List;

public class CrushLoadBalancer implements LoadBalancer {

    private List<Server> list;

    int TRIAL = 1; // Assuming trial is defined elsewhere;

    double BASE = 10; // base value Being added to sever wight.

    @Override
    public Server select(List<Server> servers, Invocation invocation) {
        Server highServer = null;
        double highDraw = 0;
        long draw;
        for (Server server : servers) {
            draw = crushHash(server.getId(), invocation.getHashKey(), TRIAL);
            draw &= 0xffff;
            double wight = server.getWight() == 0 ? 0 : BASE + server.getWight();
            draw *= wight;
            if (highServer == null || draw > highDraw) {
                highServer = server;
                highDraw = draw;
            }
        }

        return highServer;
    }

    @Override
    public void preheat(List<Server> servers) {
        this.list = servers;
    }

    @Override
    public Server select(Invocation invocation) {
        return select(this.list, invocation);
    }

    // Assuming crushHash method is defined elsewhere
    public static int crushHash(String serverId, String hashKey, int trial) {
        HashStrategy hashStrategy = new MurmurHashStrategy();
        return hashStrategy.getHashCode(serverId + hashKey) * trial;
    }
}
