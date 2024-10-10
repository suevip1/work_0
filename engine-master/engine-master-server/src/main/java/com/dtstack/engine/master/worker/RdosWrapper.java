package com.dtstack.engine.master.worker;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ScheduleDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author dazhi
 * date 2022/3/3 3:30 PM
 * company: www.dtstack.com
 */
@Component
public class RdosWrapper {

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ScheduleDictService scheduleDictService;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    public RdosWrapper() {
    }

    /**
     * 根据 engineType、集群 id、组建类型、组建版本获取对应的 datasource type
     *
     * @param engineType    engineType
     * @param clusterId     集群 id
     * @param componentType 组建类型
     * @param version       组建版本
     * @return datasource type
     */
    public Integer getDataSourceType(String engineType, Long clusterId, EComponentType componentType, String version) {
        if (Objects.isNull(componentType)) {
            componentType = Objects.requireNonNull(EngineTypeComponentType.getByEngineName(engineType)).getComponentType();
        }
        if (EComponentType.LIBRA_SQL.equals(componentType)) {
            return DataSourceType.LIBRA.getVal();
        } else if (EComponentType.IMPALA_SQL.equals(componentType)) {
            return DataSourceType.IMPALA.getVal();
        } else if (EComponentType.TIDB_SQL.equals(componentType)) {
            return DataSourceType.TiDB.getVal();
        } else if (EComponentType.ORACLE_SQL.equals(componentType)) {
            return DataSourceType.Oracle.getVal();
        } else if (EComponentType.GREENPLUM_SQL.equals(componentType)) {
            return DataSourceType.GREENPLUM6.getVal();
        } else if (EComponentType.PRESTO_SQL.equals(componentType)) {
            return DataSourceType.Presto.getVal();
        } else if (EComponentType.INCEPTOR_SQL.equals(componentType)) {
            return DataSourceType.INCEPTOR.getVal();
        } else if (EComponentType.ANALYTICDB_FOR_PG.equals(componentType)) {
            return DataSourceType.PostgreSQL.getVal();
        } else if (EComponentType.MYSQL.equals(componentType)) {
            return DataSourceType.MySQL.getVal();
        } else if (EComponentType.SQL_SERVER.equals(componentType)) {
            return DataSourceType.SQLServer.getVal();
        } else if (EComponentType.DB2.equals(componentType)) {
            return DataSourceType.DB2.getVal();
        } else if (EComponentType.OCEANBASE.equals(componentType)) {
            return DataSourceType.OceanBase.getVal();
        } else if (EComponentType.TRINO_SQL.equals(componentType)) {
            return DataSourceType.TRINO.getVal();
        } else if (EngineType.KingBase.name().equalsIgnoreCase(engineType)) {
            return DataSourceType.KINGBASE8.getVal();
        } else if (EngineType.Hana.name().equalsIgnoreCase(engineType)) {
            return DataSourceType.SAP_HANA1.getVal();
        } else if (EngineType.StarRocks.name().equalsIgnoreCase(engineType)) {
            return DataSourceType.STAR_ROCKS.getVal();
        } else if (EngineType.HashData.name().equalsIgnoreCase(engineType)) {
            return DataSourceType.HASH_DATA.getVal();
        } else if (EngineType.OushuDB.name().equalsIgnoreCase(engineType)) {
            return DataSourceType.OUSHUDB.getVal();
        } else if (EngineType.Kylin.name().equalsIgnoreCase(engineType)) {
            return DataSourceType.KylinRestful.getVal();
        } else if (EngineType.Maxcompute.name().equalsIgnoreCase(engineType)) {
            return DataSourceType.MAXCOMPUTE.getVal();
        } else if (EComponentType.KUBERNETES.equals(componentType)) {
            return DataSourceType.KUBERNETES.getVal();
        } else if (EComponentType.S3.equals(componentType)) {
            return DataSourceType.AWS_S3.getVal();
        } else if (EComponentType.RANGER.equals(componentType)) {
            return DataSourceType.RANGER.getVal();
        } else if (EComponentType.LDAP.equals(componentType)) {
            return DataSourceType.LDAP.getVal();
        } else if (EComponentType.HDFS.equals(componentType)) {
            Integer dataSourceCode = getDataSourceCodeByDiceName(engineType, EComponentType.HDFS);
            if (dataSourceCode != null) {
                return dataSourceCode;
            }
        } else if (EComponentType.YARN.equals(componentType)) {
            Integer dataSourceCode = getDataSourceCodeByDiceName(engineType, EComponentType.YARN);
            if (dataSourceCode != null) {
                return dataSourceCode;
            }
        } else if (EComponentType.HIVE_SERVER.equals(componentType)) {
            // hive任务需要判断版本
            if (StringUtils.isBlank(version)) {
                com.dtstack.engine.api.domain.Component component = componentService.getComponentByClusterId(clusterId, componentType.getTypeCode(), null);
                if (component != null) {
                    version = component.getVersionName();
                }
            }
            Integer dataSourceCode = getDataSourceCodeByDiceName(version, EComponentType.HIVE_SERVER);
            if (dataSourceCode != null) {
                return dataSourceCode;
            }
        }
        throw new RdosDefineException("not support task type:" + engineType);
    }

    public Integer getDataSourceType(String engineType, Long clusterId, EComponentType componentType) {
        return getDataSourceType(engineType, clusterId, componentType, null);
    }

    public Integer getDataSourceType(String pluginInfo, String engineType, EComponentType componentType, Long tenantId) {
        // 优先取 pluginInfo 中的 datasourceType
        if (StringUtils.isNotBlank(pluginInfo)) {
            JSONObject pluginInfoJson = JSONObject.parseObject(pluginInfo);
            if (pluginInfoJson.containsKey(ConfigConstant.DATASOURCE_TYPE)) {
                Integer datasourceType = pluginInfoJson.getInteger(ConfigConstant.DATASOURCE_TYPE);
                if (Objects.nonNull(datasourceType)) {
                    return datasourceType;
                }
            }
        }
        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(tenantId);
        return getDataSourceType(engineType, clusterId, componentType, null);
    }

    public Integer getDataSourceType(String engineType, EComponentType componentType, Long tenantId) {
        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(tenantId);
        return getDataSourceType(engineType, clusterId, componentType, null);
    }

    /**
     * 根据组建类型和 typeName 获取对应的 datasource type
     *
     * @param typeName      typeName
     * @param componentType 组建类型
     * @return datasource type
     */
    public Integer getDataSourceCodeByDiceName(String typeName, EComponentType componentType) {
        List<ScheduleDict> dictList = scheduleDictService.listByDictType(DictType.VERSION_DATASOURCE_X);
        Optional<ScheduleDict> dbTypeNames = dictList.stream()
                .filter(dict ->
                        dict.getDictName().equalsIgnoreCase(componentType.getName())
                )
                .findFirst();
        if (dbTypeNames.isPresent()) {
            JSONObject versionObj = JSONObject.parseObject(dbTypeNames.get().getDictValue());
            if (versionObj != null) {
                return versionObj.getInteger(typeName);
            }
        }
        return null;
    }
}
