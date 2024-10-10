package com.dtstack.engine.api.service;

import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.ComponentInfoParam;
import com.dtstack.engine.api.vo.ClusterEngineVO;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.api.vo.TenantClusterCommonConfigVO;
import com.dtstack.engine.api.vo.components.ClusterComponentVO;
import com.dtstack.engine.api.vo.components.SparkThriftServerVO;
import com.dtstack.engine.api.vo.components.ClusterFilePathVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;
import java.util.Map;

public interface ClusterService extends DtInsightServer {

    @RequestLine("POST /node/cluster/addCluster")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<ClusterVO> addCluster(ClusterDTO clusterDTO);

    @RequestLine("POST /node/cluster/pageQuery")
    ApiResponse<PageResult<List<ClusterVO>>> pageQuery(@Param("currentPage") int currentPage, @Param("pageSize") int pageSize);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/clusterInfo")
    ApiResponse<String> clusterInfo(@Param("tenantId") Long tenantId);

    /**
     * 对外接口
     */
    @RequestLine("POST /node/cluster/clusterExtInfo")
    ApiResponse<ClusterVO> clusterExtInfo(@Param("tenantId") Long uicTenantId);

    /**
     * 对外接口
     * 已废弃，因为原接口为空实现
     */
    // @RequestLine("POST /node/cluster/pluginInfoJSON")
    // @Deprecated
    // ApiResponse<JSONObject> pluginInfoJSON(@Param("tenantId") Long dtUicTenantId, @Param("engineType") String engineTypeStr, @Param("dtUicUserId") Long dtUicUserId, @Param("deployMode") Integer deployMode);

    /**
     * 已废弃，因为原接口为空实现
     */
    // @RequestLine("POST /node/cluster/pluginInfo")
    // @Deprecated
    // ApiResponse<String> pluginInfo(@Param("tenantId") Long dtUicTenantId, @Param("engineType") String engineTypeStr, @Param("userId") Long dtUicUserId, @Param("deployMode") Integer deployMode);

    /**
     * 获取集群在sftp上的路径
     * 开启kerberos 带上kerberos路径
     * 多版本组件获取默认
     * @param tenantId
     * @return
     */
    @RequestLine("POST /node/cluster/clusterSftpDir")
    ApiResponse<String> clusterSftpDir(@Param("tenantId") Long tenantId, @Param("componentType") Integer componentType);

    /**
     * 获得插件信息
     * 注释： 用于取代 /node/cluster/hiveInfo /node/cluster/hiveServerInfo、/node/cluster/hadoopInfo、/node/cluster/carbonInfo、/node/cluster/impalaInfo、/node/cluster/sftpInfo等接口
     * @param dtUicTenantId 租户id
     * @param fullKerberos 历史无用参数
     * @param pluginType 插件类型 插入code即可
     *                   HDFS(4, "HDFS", "hadoopConf"), -> /node/cluster/hadoopInfo
     *                   SPARK_THRIFT(6, "SparkThrift", "hiveConf"), -> /node/cluster/hiveInfo
     *                   CARBON_DATA(7, "CarbonData ThriftServer", "carbonConf"), -> /node/cluster/carbonInfo
     *                   HIVE_SERVER(9, "HiveServer", "hiveServerConf"), ->  /node/cluster/hiveServerInfo
     *                   IMPALA_SQL(11, "Impala SQL", "impalaSqlConf"), -> /node/cluster/impalaInfo
     *                   SFTP(10, "SFTP", "sftpConf"), -> /node/cluster/sftpInfo
     *                   PRESTO(16, "Presto")
     * 使用新接口
     * @see ClusterService#pluginInfoByTypeCode(Long, Integer, String)
     * @return
     */
    // @RequestLine("POST /node/cluster/pluginInfoForType")
    // @Deprecated
    // ApiResponse<String> pluginInfoForType(@Param("tenantId") Long dtUicTenantId  ,@Param("fullKerberos") Boolean fullKerberos, @Param("pluginType") EComponentApiType pluginType );

    /**
     * 通过枚举获得配置
     *
     * @param dtUicTenantId 用户id
     * @param key 枚举类型 例如 FLINK
     * @param fullKerberos 是否将sftp中keytab配置转换为本地路径
     *                     如果不传或者false,不转换;
     *                     如果是true,转换;
     * @return
     */
    @RequestLine("POST /node/cluster/getConfigByKey")
    ApiResponse<String> getConfigByKey(@Param("dtUicTenantId") Long dtUicTenantId, @Param("key") String key, @Param("fullKerberos") Boolean fullKerberos,@Param("componentVersionMap") Map<Integer,String > componentVersionMap);


    /**
     * 集群下拉列表
     */
    @RequestLine("POST /node/cluster/clusters")
    ApiResponse<List<ClusterVO>> clusters();

    /**
     * 获得tiDBInfo,oracleInfo,greenplumInfo组件信息
     * 注释： 用于取代 /node/cluster/tiDBInfo、/node/cluster/oracleInfo 、/node/cluster/greenplumInfo 等接口
     * @param dtUicTenantId 组户id
     * @param dtUicUserId 用户id
     * @param type 组件类型
     *             `Oracle(2);` /node/cluster/oracleInfo
     *             `TiDB(31);` /node/cluster/tiDBInfo
     *             `GREENPLUM6(36);` /node/cluster/greenplumInfo
     *
     * 使用新接口
     * @see ClusterService#componentInfo(Long, Long, Integer, String)
     * @return
     */
    // @RequestLine("POST /node/cluster/dbInfo")
    // @Deprecated
    // ApiResponse<String> dbInfo(@Param("tenantId") Long dtUicTenantId, @Param("userId") Long dtUicUserId, @Param("type") DbType type);


    /**
     * 删除集群
     * 判断该集群下是否有租户
     * @param clusterId
     */
    // @RequestLine("POST /node/cluster/deleteCluster")
    // @Deprecated
    // ApiResponse<Void> deleteCluster( @Param("clusterId") Long clusterId);


    /**
     * 获取集群信息详情 需要根据组件分组
     * @param clusterId
     * @return
     */
    @RequestLine("POST /node/cluster/getCluster")
    ApiResponse<ClusterVO> getCluster(@Param("clusterId") Long clusterId, @Param("kerberosConfig") Boolean kerberosConfig, @Param("removeTypeName") Boolean removeTypeName);

    /**
     * 获得所有Cluster
     *
     * @return
     */
    @RequestLine("POST /node/cluster/getAllCluster")
    ApiResponse<List<ClusterEngineVO>> getAllCluster();

    /**
     * 对外接口
     */
    // @RequestLine("POST /node/cluster/prestoInfo")
    // @Deprecated
    // ApiResponse<String> prestoInfo(@Param("tenantId") Long dtUicTenantId, @Param("fullKerberos") Boolean fullKerberos);

    /**
     * 判断的租户和另一个租户是否在一个集群
     *
     * @param dtUicTenantId
     * @param dtUicTenantIds
     * @return
     */
    @RequestLine("POST /node/cluster/isSameCluster")
    ApiResponse<Boolean> isSameCluster(@Param("tenantId") Long dtUicTenantId,@Param("aimTenantIds") List<Long> dtUicTenantIds);

    /**
     * 判断的租户对应组件下是否有standalone配置
     *
     * @param dtUicTenantId
     * @param componentType
     * @return
     */
    @RequestLine("POST /node/cluster/hasStandalone")
    ApiResponse<Boolean> hasStandalone(@Param("tenantId") Long dtUicTenantId, @Param("componentType") Integer componentType);



    /**
     * 获取集群下所有组件
     * @param dtUicTenantId 租户id
     * @param multiVersion 是否返回多版本 true: 组件配置多个版本全部返回 false:返回默认版本
     */
    @RequestLine("POST /node/sdk/cluster/clusterComponent")
    ApiResponse<List<ClusterComponentVO>> clusterComponent(@Param("dtUicTenantId") Long dtUicTenantId, @Param("multiVersion") boolean multiVersion);

    /**
     * 获取集群下metadata组件
     * @param dtUicTenantId 租户id
     */
    @RequestLine("POST /node/sdk/cluster/getMetaComponent")
    ApiResponse<ClusterComponentVO> getMetaComponent(@Param("dtUicTenantId") Long dtUicTenantId);


    /**
     * 获取组件的配置信息
     *
     * @param dtUicTenantId     租户id
     * @param componentTypeCode 组件code
     * @param componentVersion  组件版本 不传使用该组件默认版本
     * @return
     */
    @RequestLine("POST /node/sdk/cluster/pluginInfoByTypeCode")
    ApiResponse<String> pluginInfoByTypeCode(@Param("dtUicTenantId") Long dtUicTenantId, @Param("componentTypeCode") Integer componentTypeCode, @Param("componentVersion") String componentVersion);


    /**
     * 获取控制台组件的配置信息
     * 迁移到 {@link com.dtstack.engine.api.service.ClusterService#componentInfoV2}
     *
     * @param dtUicTenantId 租户id
     * @param dtUicUserId 用户id (如果控制台账号绑定中有对应id 会替换sql组件的用户密码）
     * @param componentTypCode 组件code
     * @param componentVersion 组件版本 为空 返回默认版本
     * @return
     */
    @RequestLine("POST /node/sdk/cluster/componentInfo")
    @Deprecated
    ApiResponse<String> componentInfo(@Param("dtUicTenantId") Long dtUicTenantId, @Param("dtUicUserId") Long dtUicUserId, @Param("componentTypCode") Integer componentTypCode,@Param("componentVersion")String componentVersion);

    /**
     * 获取控制台组件的配置信息
     * @param componentInfoParam
     * @return
     */
    @RequestLine("POST /node/sdk/cluster/v2/componentInfo")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<String> componentInfoV2(ComponentInfoParam componentInfoParam);

    /**
     * 获取k8s集群下的namespace
     * @param dtUicTenantId
     * @return
     */
    @RequestLine("POST /node/sdk/cluster/getNamespace")
    ApiResponse<String> getNamespace(@Param("dtUicTenantId") Long dtUicTenantId);

    /**
     * 获取spark thrift的url信息
     * @param dtUicTenantId
     * @return
     */
    @RequestLine("POST /node/sdk/cluster/getSparkThrift")
    ApiResponse<SparkThriftServerVO> getSparkThrift(@Param("dtUicTenantId") Long dtUicTenantId);

    /**
     * 获取租户对应的集群配置文件路径
     *
     * @param dtUicTenantId
     * @return
     */
    @RequestLine("POST /node/sdk/cluster/getConfigFilePath")
    ApiResponse<ClusterFilePathVO> getConfigFilePath(@Param("dtUicTenantId") Long dtUicTenantId);

    /**
     * 获取租户对应集群的通用配置
     * @param tenantId
     * @return
     */
    @RequestLine("POST /node/sdk/cluster/getTenantClusterCommonConfig")
    ApiResponse<TenantClusterCommonConfigVO> getTenantClusterCommonConfig(@Param("tenantId") Long tenantId);
}
