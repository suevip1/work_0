package com.dtstack.engine.master.model.cluster;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.model.cluster.datasource.ImmediatelyLoadDataSource;
import com.dtstack.engine.master.model.cluster.system.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClusterFactory {

    @Autowired
    private Context context;

    @Autowired
    private ComponentFacade facade;

    public PartCluster newImmediatelyLoadCluster(Long clusterId) {
        if (null == clusterId) {
            throw new RdosDefineException(ErrorCode.CLUSTER_ID_EMPTY);
        }
        return new PartCluster(clusterId, context, new ImmediatelyLoadDataSource(clusterId, facade));
    }


}
