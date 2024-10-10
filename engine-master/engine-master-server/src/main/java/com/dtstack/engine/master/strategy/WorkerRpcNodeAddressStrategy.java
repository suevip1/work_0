package com.dtstack.engine.master.strategy;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.rpc.enums.RpcRemoteType;
import com.dtstack.rpc.exception.ExecuteException;
import com.dtstack.rpc.manager.RpcNodeAddressStrategy;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2022/3/21 10:27 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class WorkerRpcNodeAddressStrategy implements RpcNodeAddressStrategy {

    @Autowired
    private EnvironmentContext environmentContext;


    @Override
    public RpcRemoteType getRpcRemoteType() {
        return RpcRemoteType.DAGSCHEDULEX_CLIENT;
    }

    @Override
    public Set<String> getAllNodes(Object[] publicParams) throws ExecuteException {
        String workerNodes = environmentContext.getWorkerNodes();
        List<String> splitToList = Splitter.on(",").omitEmptyStrings().splitToList(workerNodes);
        return Sets.newHashSet(splitToList);
    }
}
