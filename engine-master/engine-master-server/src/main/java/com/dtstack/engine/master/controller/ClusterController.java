package com.dtstack.engine.master.controller;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.utils.AssertUtils;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.enums.DbType;
import com.dtstack.engine.api.enums.EComponentApiType;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ClusterEngineInfoVO;
import com.dtstack.engine.api.vo.ClusterEngineVO;
import com.dtstack.engine.api.vo.ClusterPageVO;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.api.vo.KerberosConfigVO;
import com.dtstack.engine.api.vo.TenantClusterCommonConfigVO;
import com.dtstack.engine.api.vo.TenantClusterInfoVO;
import com.dtstack.engine.api.vo.TenantNameVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.KerberosDao;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.model.cluster.system.Context;
import com.dtstack.engine.master.router.DtParamOrHeader;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.engine.master.router.permission.Authenticate;
import com.dtstack.pubsvc.sdk.usercenter.client.UIcUserTenantRelApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.LdapUserVO;
import dt.insight.plat.lang.base.Strings;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/node/cluster", "/node/component/cluster"})
@Api(value = "/node/cluster", tags = {"集群接口"})
public class ClusterController{

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private Context context;

    @Autowired
    private KerberosDao kerberosDao;

    @Autowired
    private SessionUtil sessionUtil;

    @Autowired
    private UIcUserTenantRelApiClient uIcUserTenantRelApiClient;

    @RequestMapping(value="/addCluster", method = {RequestMethod.POST})
    public ClusterVO addCluster(@RequestBody ClusterDTO clusterDTO) {
        return clusterService.addCluster(clusterDTO);
    }

    @RequestMapping(value = "/pageQuery", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "页数", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "条数", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "fuzzyClusterName", value = "模糊搜索集群名称", dataType = "String"),
            @ApiImplicitParam(name = "fuzzyTenantName", value = "模糊搜索租户名称", dataType = "String")
    })
    @Authenticate(all = "console_resource_enter_all",
            tenant = "console_resource_enter_tenant")
    public PageResult<List<ClusterPageVO>> pageQuery(@RequestParam("currentPage") int currentPage,
                                                     @RequestParam("pageSize") int pageSize,
                                                     @RequestParam("fuzzyClusterName") String fuzzyClusterName,
                                                     @RequestParam("fuzzyTenantName") String fuzzyTenantName,
                                                     @RequestParam("dtToken") String dtToken,
                                                     @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        return clusterService.pageQuery(currentPage, pageSize, fuzzyClusterName, fuzzyTenantName, isAllAuth, user);
    }

    /**
     * 获取集群绑定的租户id
     * @param clusterId 集群id
     * @param fuzzyTenantName 模糊搜索的租户名
     * @return vo
     */
    @RequestMapping(value = "/getClusterBindingTenants", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_bind_view_all",
            tenant = "console_resource_bind_view_tenant")
    public List<TenantNameVO> getClusterBindingTenants(@RequestParam("clusterId") Long clusterId,
                                                       @RequestParam("fuzzyTenantName") String fuzzyTenantName,
                                                       @RequestParam("dtToken") String dtToken,
                                                       @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        AssertUtils.notNull(clusterId, "参数错误, 集群 id 不能为空");
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        return clusterService.getClusterBindingTenants(clusterId, fuzzyTenantName,user,isAllAuth);
    }

    /**
     * 获取集群对应的引擎信息
     * @param clusterId 集群id
     * @return engineVO
     */
    @RequestMapping(value = "/getClusterEngineInfo", method = {RequestMethod.POST})
    public List<ClusterEngineInfoVO> getClusterEngineInfo(@RequestParam("clusterId") Long clusterId) {
        AssertUtils.notNull(clusterId, "参数错误, 集群 id 不能为空");
        return clusterService.getClusterEngineInfo(clusterId);
    }

    /**
     * 获取租户集群信息(主要是 Hadoop 引擎信息)
     * @param clusterId 集群id
     * @param tenantId  租户id
     * @return vo
     */
    @RequestMapping(value = "/getTenantClusterInfo", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_bind_view_all",
            tenant = "console_resource_bind_view_tenant")
    public TenantClusterInfoVO getTenantClusterInfo(@RequestParam("clusterId") Long clusterId,
                                                    @RequestParam("tenantId") Long tenantId,
                                                    @RequestParam("dtToken") String dtToken,
                                                    @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        AssertUtils.notNull(clusterId, "参数错误, 集群 id 不能为空");
        AssertUtils.notNull(tenantId, "参数错误, 租户 id 不能为空");
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        if (!isAllAuth) {
            // 如果是租户级别的，就只显示改租户下的数据
            List<Long> userTenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();

            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userTenantIds) && !userTenantIds.contains(tenantId)) {
                throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
            }
        }

        return clusterService.getTenantClusterInfo(clusterId, tenantId);
    }

    @RequestMapping(value = "/getTenantClusterCommonConfig", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_bind_currency_view_all",
            tenant = "console_resource_bind_currency_view_tenant")
    public TenantClusterCommonConfigVO getTenantClusterCommonConfig(@RequestParam("tenantId") Long tenantId,
                                                                    @RequestParam("dtToken") String dtToken,
                                                                    @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        AssertUtils.notNull(tenantId, "参数错误, 租户 id 不能为空");
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        if (!isAllAuth) {
            // 如果是租户级别的，就只显示改租户下的数据
            List<Long> userTenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();

            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userTenantIds) && !userTenantIds.contains(tenantId)) {
                throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
            }
        }
        return clusterService.getTenantClusterCommonConfig(tenantId);
    }

    @RequestMapping(value = "/updateTenantClusterCommonConfig", method = {RequestMethod.POST})
    @Authenticate(all = "console_resource_bind_currency_change_all",
            tenant = "console_resource_bind_currency_change_tenant")
    public void updateTenantClusterCommonConfig(@RequestParam("tenantId") Long tenantId,
                                                @RequestParam("tenantClusterCommonConfigVO") String tenantClusterCommonConfigVO,
                                                @RequestParam("dtToken") String dtToken,
                                                @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) {
        AssertUtils.notNull(tenantId, "参数错误, 租户 id 不能为空");
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        if (!isAllAuth) {
            // 如果是租户级别的，就只显示改租户下的数据
            List<Long> userTenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();

            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userTenantIds) && !userTenantIds.contains(tenantId)) {
                throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
            }
        }
        clusterService.updateTenantClusterCommonConfig(tenantId, tenantClusterCommonConfigVO);
    }

    @RequestMapping(value="/clusterInfo", method = {RequestMethod.POST})
    public String clusterInfo(@RequestParam("tenantId") Long tenantId) {
        return clusterService.clusterInfo(tenantId);
    }

    @RequestMapping(value="/clusterExtInfo", method = {RequestMethod.POST})
    public ClusterVO clusterExtInfo(@RequestParam("tenantId") Long uicTenantId) {
        return clusterService.clusterExtInfo(uicTenantId,false);
    }

    @RequestMapping(value="/pluginInfoJSON", method = {RequestMethod.POST})
    public JSONObject pluginInfoJSON(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("engineType") String engineTypeStr, @RequestParam("dtUicUserId")Long dtUicUserId, @RequestParam("deployMode")Integer deployMode) {
        return new JSONObject();
    }


    @RequestMapping(value="/pluginInfo", method = {RequestMethod.POST})
    public String pluginInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("engineType") String engineTypeStr,@RequestParam("userId") Long dtUicUserId,@RequestParam("deployMode")Integer deployMode) {
        return clusterService.pluginInfo(dtUicTenantId, engineTypeStr, dtUicUserId, deployMode);
    }

    @RequestMapping(value="/clusterSftpDir", method = {RequestMethod.POST})
    @ApiOperation(value = "获取集群在sftp上的路径")
    public String clusterSftpDir(@RequestParam("tenantId") Long tenantId, @RequestParam("componentType") Integer componentType) {
        return clusterService.clusterSftpDir(tenantId, componentType);
    }

    @ApiOperation(value = "获得插件信息")
    @RequestMapping(value="/pluginInfoForType", method = {RequestMethod.POST})
    public String pluginInfoForType(@RequestParam("tenantId") Long dtUicTenantId  , @RequestParam("fullKerberos") Boolean fullKerberos, @RequestParam("pluginType") EComponentApiType pluginType){
        return clusterService.pluginInfoForType(dtUicTenantId, fullKerberos,pluginType.getTypeCode(),true);
    }
    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    @RequestMapping(value="/hiveInfo", method = {RequestMethod.POST})
    @Deprecated
    public String hiveInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.hiveInfo(dtUicTenantId, fullKerberos,null);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    @RequestMapping(value="/hiveServerInfo", method = {RequestMethod.POST})
    @Deprecated
    public String hiveServerInfo(@RequestParam("tenantId") Long dtUicTenantId,@RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.hiveServerInfo(dtUicTenantId, fullKerberos,null);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    @RequestMapping(value="/hadoopInfo", method = {RequestMethod.POST})
    @Deprecated
    public String hadoopInfo(@RequestParam("tenantId") Long dtUicTenantId,@RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.hadoopInfo(dtUicTenantId, fullKerberos,null);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    @RequestMapping(value="/carbonInfo", method = {RequestMethod.POST})
    @Deprecated
    public String carbonInfo(@RequestParam("tenantId") Long dtUicTenantId,@RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.carbonInfo(dtUicTenantId, fullKerberos,null);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    @RequestMapping(value="/impalaInfo", method = {RequestMethod.POST})
    @Deprecated
    public String impalaInfo(@RequestParam("tenantId") Long dtUicTenantId,@RequestParam("fullKerberos") Boolean fullKerberos) {
        return clusterService.impalaInfo(dtUicTenantId, fullKerberos,null);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @return
     */
    @RequestMapping(value="/sftpInfo", method = {RequestMethod.POST})
    @Deprecated
    public String sftpInfo(@RequestParam("tenantId") Long dtUicTenantId) {
        return clusterService.sftpInfo(dtUicTenantId);
    }

    @RequestMapping(value="/getConfigByKey", method = {RequestMethod.POST})
    public String getConfigByKey(@RequestParam("dtUicTenantId")Long dtUicTenantId, @RequestParam("key") String key, @RequestParam("fullKerberos") Boolean fullKerberos, @RequestParam("componentVersionMap") Map<Integer,String > componentVersionMap) {
        return clusterService.getConfigByKey(dtUicTenantId, key, fullKerberos,componentVersionMap,false);
    }

    @RequestMapping(value="/clusters", method = {RequestMethod.POST})
    @ApiOperation(value = "集群下拉列表")
    public List<ClusterVO> clusters() {
        return clusterService.clusters();
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param dtUicUserId
     * @return
     */
    @RequestMapping(value="/tiDBInfo", method = {RequestMethod.POST})
    @Deprecated
    public String tiDBInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("userId") Long dtUicUserId) {
        return clusterService.tiDBInfo(dtUicTenantId, dtUicUserId,null, null);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param dtUicUserId
     * @return
     */
    @RequestMapping(value="/oracleInfo", method = {RequestMethod.POST})
    @Deprecated
    public String oracleInfo(@RequestParam("tenantId") Long dtUicTenantId,@RequestParam("userId") Long dtUicUserId) {
        return clusterService.oracleInfo(dtUicTenantId, dtUicUserId,null, null);
    }

    /**
     * 兼容其他应用保留
     *
     * @param dtUicTenantId
     * @param dtUicUserId
     * @return
     */
    @RequestMapping(value="/greenplumInfo", method = {RequestMethod.POST})
    @Deprecated
    public String greenplumInfo(@RequestParam("tenantId") Long dtUicTenantId,@RequestParam("userId") Long dtUicUserId) {
        return clusterService.greenplumInfo(dtUicTenantId, dtUicUserId,null, null);
    }

    @RequestMapping(value="/dbInfo", method = {RequestMethod.POST})
    public String dbInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("userId") Long dtUicUserId, @RequestParam("type") DbType type) {
        return clusterService.dbInfo(dtUicTenantId, dtUicUserId , type.getTypeCode(), null);
    }

    @RequestMapping(value="/deleteCluster", method = {RequestMethod.POST})
    @ApiOperation(value = "删除集群")
    @Authenticate(all = "console_resource_cluster_delete_all")
    public void deleteCluster(@RequestParam("clusterId")Long clusterId) {
        clusterService.deleteCluster(clusterId);
    }

    @RequestMapping(value="/getCluster", method = {RequestMethod.POST})
    @ApiOperation(value = "获取集群信息详情")
    public ClusterVO getCluster(@RequestParam("clusterId") Long clusterId, @RequestParam("kerberosConfig") Boolean kerberosConfig, @RequestParam("removeTypeName") Boolean removeTypeName) throws Exception{
        return clusterService.getCluster(clusterId, removeTypeName, true, true,true);
    }

    @RequestMapping(value="/getAllCluster", method = {RequestMethod.POST})
    public List<ClusterEngineVO> getAllCluster() {
        return clusterService.getAllCluster();
    }

    @RequestMapping(value="/prestoInfo", method = {RequestMethod.POST})
    public String prestoInfo(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("fullKerberos") Boolean fullKerberos) {
        return getConfigByKey(dtUicTenantId, EComponentType.PRESTO_SQL.getConfName(), fullKerberos,null);
    }


    @ApiOperation(value = "判断的租户和另一个租户是否在一个集群")
    @ApiImplicitParams({
            @ApiImplicitParam(name="tenantId",value="租户id",required=true, dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name="aimTenantIds",value="租户id集合",required=true, dataType = "Long", allowMultiple = true)
    })
    @RequestMapping(value="/isSameCluster", method = {RequestMethod.POST})
    public Boolean isSameCluster(@RequestParam("tenantId") Long dtUicTenantId,@RequestParam("aimTenantIds") List<Long> dtUicTenantIds){
        return clusterService.isSameCluster(dtUicTenantId,dtUicTenantIds);
    }


    @ApiOperation(value = "判断的租户对应集群是否有standalone组件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name = "componentType", value = "组件类型", required = true, dataType = "Integer", allowMultiple = true)
    })
    @RequestMapping(value = "/hasStandalone", method = {RequestMethod.POST, RequestMethod.GET})
    public Boolean hasStandalone(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("componentType") Integer componentType) {
        return clusterService.hasStandalone(dtUicTenantId, componentType);
    }

    @RequestMapping(value = "/showLdapUserName", method = {RequestMethod.POST, RequestMethod.GET})
    public String getLdapUserName(Long dtUicUserId, Long dtTenantId) {
        LdapUserVO ldapUserVO = clusterService.getLdapUserOnOpenRangerAndLdap(dtUicUserId, dtTenantId);
        return ldapUserVO != null ? ldapUserVO.getLdapUserName() : Strings.EMPTY_STRING;
    }

    @RequestMapping(value = "/getEncryptKeys", method = {RequestMethod.POST, RequestMethod.GET})
    public List<String> getEncryptKeys() {
        return context.listSm2EncryptKeys();
    }

    @PostMapping(value = "/getKerberos")
    public List<KerberosConfigVO> getKerberos(@RequestParam("clusterId") Long clusterId) {
        List<KerberosConfig> kerberosConfigs = kerberosDao.getByClusters(clusterId);
        if (CollectionUtils.isEmpty(kerberosConfigs)) {
            return new ArrayList<>();
        }
        return kerberosConfigs.stream().map(KerberosConfigVO::toVO).collect(Collectors.toList());
    }

    @RequestMapping(value="/hasComponent", method = {RequestMethod.POST})
    public boolean hasComponent(@RequestParam("clusterId") Long clusterId, @RequestParam("componentTypes") List<Integer> componentTypes) {
        return clusterService.hasComponent(clusterId, componentTypes);
    }
}
