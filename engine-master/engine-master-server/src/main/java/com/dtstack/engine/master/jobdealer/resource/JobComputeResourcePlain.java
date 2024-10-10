package com.dtstack.engine.master.jobdealer.resource;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.utils.JobClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.dtstack.engine.common.constrant.ConfigConstant.RESOURCE_NAMESPACE_OR_QUEUE_DEFAULT;
import static com.dtstack.engine.common.constrant.ConfigConstant.SPLIT;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
@Component
public class JobComputeResourcePlain {

    @Autowired
    private CommonResource commonResource;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private ClusterService clusterService;


    public String getJobResource(JobClient jobClient) {
        this.buildJobClientGroupName(jobClient);
        ComputeResourceType computeResourceType = commonResource.newInstance(jobClient);
        String plainType = environmentContext.getComputeResourcePlain();
        String jobResource = null;
        if (ComputeResourcePlain.EngineTypeClusterQueue.name().equalsIgnoreCase(plainType)) {
            jobResource = jobClient.getEngineType() + SPLIT + jobClient.getGroupName();
        } else {
            jobResource = jobClient.getEngineType() + SPLIT + jobClient.getGroupName() + SPLIT + jobClient.getComputeType().name().toLowerCase();
        }

        String type = EScheduleType.TEMP_JOB.getType().equals(jobClient.getType())?jobClient.getType()+"":"";
        return jobResource + SPLIT + computeResourceType.name() + type;
    }


    private void buildJobClientGroupName(JobClient jobClient) {
        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(jobClient.getTenantId());
        if (null == clusterId) {
            return;
        }
        Cluster cluster = clusterDao.getOne(clusterId);
        if (null == cluster) {
            return;
        }
        String clusterName = cluster.getClusterName();
        //%s_default
        String groupName = clusterName + SPLIT + RESOURCE_NAMESPACE_OR_QUEUE_DEFAULT;

        if (ScheduleEngineType.KUBERNETES.getEngineName().equals(jobClient.getEngineType())) {
            String namespace = clusterService.getNamespace(JobClientUtil.getParamAction(jobClient),
                    jobClient.getTenantId(), jobClient.getEngineType(), jobClient.getComputeType());
            groupName = clusterName + SPLIT + namespace;
        } else {
            ParamAction paramAction = JobClientUtil.getParamAction(jobClient);
            if (paramAction != null) {
                Queue queue = clusterService.getQueue(jobClient.getTenantId(), clusterId, paramAction.getResourceId());
                if (null != queue) {
                    groupName = clusterName + SPLIT + queue.getQueueName();
                }
            }
        }
        jobClient.setGroupName(groupName);
    }
}
