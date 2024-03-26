package ccw.serviceinnovation.loadbalance;

import java.util.List;


/**
 * 陈翔
 */
public interface LoadBalancer {

    Server select(List<Server> servers, Invocation invocation);

    void preheat(List<Server> servers);

    Server select(Invocation invocation);
}
