package ccw.serviceinnovation.common.util.hash;

import java.util.List;

/**
 * @author 陈翔
 */
public interface LoadBalancer {

    Server select(List<Server> servers, Invocation invocation);
}
