package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.jobdealer.resource.CommonResource;
import com.dtstack.engine.master.jobdealer.resource.ComputeResourceType;
import com.dtstack.engine.master.mockcontainer.BaseMock;

/**
 * @Auther: dazhi
 * @Date: 2022/6/30 10:15 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobComputeResourcePlainMock extends BaseMock {

    @MockInvoke(targetClass = CommonResource.class)
    public ComputeResourceType newInstance(JobClient jobClient) {
        return ComputeResourceType.Hana;
    }



    @MockInvoke(targetClass = ClusterTenantDao.class)
    Long getClusterIdByDtUicTenantId(Long dtUicTenantId){
        return dtUicTenantId;
    }

    @MockInvoke(targetClass = ClusterDao.class)
    Cluster getOne(Long clusterId){
        Cluster cluster = new Cluster();
        cluster.setId(clusterId);
        cluster.setClusterName("123");
        return cluster;
    }

    @MockInvoke(targetClass = ClusterService.class)
    public String getNamespace(ParamAction action, Long tenantId, String engineName, ComputeType computeType) {
        return "";
    }

    @MockInvoke(targetClass = ClusterService.class)
    public Queue getQueue(Long dtUicTenantId, Long clusterId, Long resourceId) {
        return null;
    }

}
