package com.dtstack.engine.master.jobdealer.resource;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/11
 */
@Component
public class CommonResource {

    private static Map<EngineType, CommonResource> resources = new ConcurrentHashMap<>();

    @Autowired
    protected ClusterDao clusterDao;

    @Autowired
    protected ClusterService clusterService;

    @Autowired
    protected ComponentService componentService;

    @Autowired
    protected ClusterTenantDao clusterTenantDao;


    public ComputeResourceType newInstance(JobClient jobClient) {
        EngineType engineType = EngineType.getEngineType(jobClient.getEngineType());
        CommonResource resource = resources.computeIfAbsent(engineType, k -> {
            CommonResource commonResource = null;
            switch (engineType) {
                case Flink:
                    commonResource = new FlinkResource();
                    commonResource.setClusterDao(clusterDao);
                    commonResource.setClusterService(clusterService);
                    commonResource.setComponentService(componentService);
                    commonResource.setClusterTenantDao(clusterTenantDao);
                    break;
                case Spark:
                case Learning:
                case DtScript:
                case Hadoop:
                case Hive:
                case Mysql:
                case Oracle:
                case Sqlserver:
                case Trino:
                case Maxcompute:
                case PostgreSQL:
                case Kylin:
                case Impala:
                case TiDB:
                case Presto:
                case GreenPlum:
                case Dummy:
                case KingBase:
                case InceptorSQL:
                case DtScriptAgent:
                case AnalyticdbForPg:
                case tony:
                case Volcano:
                case Hana:
                case HashData:
                case StarRocks:
                case OushuDB:
                case DB2:
                case OCEANBASE:
                    commonResource = this;
                    break;
                default:
                    throw new RdosDefineException("engineType:" + engineType + " is not support.");
            }
            return commonResource;
        });

        return resource.getComputeResourceType(jobClient);
    }

    public ComputeResourceType getComputeResourceType(JobClient jobClient) {

        EngineType engineType = EngineType.getEngineType(jobClient.getEngineType());
        switch (engineType) {
            case Spark:
            case Learning:
            case DtScript:
            case Hadoop:
                return ComputeResourceType.Yarn;
            case Hive:
                return ComputeResourceType.Hive;
            case Mysql:
                return ComputeResourceType.Mysql;
            case Oracle:
                return ComputeResourceType.Oracle;
            case Sqlserver:
                return ComputeResourceType.Sqlserver;
            case Maxcompute:
                return ComputeResourceType.Maxcompute;
            case PostgreSQL:
                return ComputeResourceType.PostgreSQL;
            case Kylin:
                return ComputeResourceType.Kylin;
            case Impala:
                return ComputeResourceType.Impala;
            case TiDB:
                return ComputeResourceType.TiDB;
            case GreenPlum:
                return ComputeResourceType.GreenPlum;
            case Dummy:
                return ComputeResourceType.Dummy;
            case Presto:
                return ComputeResourceType.Presto;
            case KingBase:
                return ComputeResourceType.KingBase;
            case InceptorSQL:
                return ComputeResourceType.InceptorSQL;
            case DtScriptAgent:
                return ComputeResourceType.DtScriptAgent;
            case AnalyticdbForPg:
                return ComputeResourceType.AnalyticdbForPg;
            case Trino:
                return ComputeResourceType.Trino;
            case tony:
                return ComputeResourceType.tony;
            case Hana:
                return ComputeResourceType.Hana;
            case HashData:
                return ComputeResourceType.HashData;
            case StarRocks:
                return ComputeResourceType.StarRocks;
            case OushuDB:
                return ComputeResourceType.OushuDB;
            case DB2:
                return ComputeResourceType.DB2;
            case OCEANBASE:
                return ComputeResourceType.OCEANBASE;
            case Volcano:
                return ComputeResourceType.Volcano;
            default:
                throw new RdosDefineException("engineType:" + engineType + " is not support.");
        }
    }

    public ClusterDao getClusterDao() {
        return clusterDao;
    }

    public void setClusterDao(ClusterDao clusterDao) {
        this.clusterDao = clusterDao;
    }

    public ClusterService getClusterService() {
        return clusterService;
    }

    public void setClusterService(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    public ComponentService getComponentService() {
        return componentService;
    }

    public void setComponentService(ComponentService componentService) {
        this.componentService = componentService;
    }

    public ClusterTenantDao getClusterTenantDao() {
        return clusterTenantDao;
    }

    public void setClusterTenantDao(ClusterTenantDao clusterTenantDao) {
        this.clusterTenantDao = clusterTenantDao;
    }
}
