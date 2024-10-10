package com.dtstack.engine.master.controller.sdk;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.utils.AssertUtils;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.param.ComponentInfoParam;
import com.dtstack.engine.api.vo.TenantClusterCommonConfigVO;
import com.dtstack.engine.api.vo.components.ClusterComponentVO;
import com.dtstack.engine.api.vo.components.ClusterFilePathVO;
import com.dtstack.engine.api.vo.components.SparkThriftServerVO;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.cron.SparkThriftServerMonitor;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.dtstack.engine.master.mapstruct.ComponentStruct;
import com.dtstack.lang.Langs;
import jodd.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2021-08-26
 * sdk接口
 */
@RestController
@RequestMapping({"/node/sdk/cluster"})
public class ClusterSdkController {

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentStruct componentStruct;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SparkThriftServerMonitor sparkThriftServerMonitor;

    @Autowired
    private ScheduleDictService dictService;

    @RequestMapping(value = "/pluginInfoByTypeCode", method = {RequestMethod.POST})
    public String pluginInfoByTypeCode(@RequestParam("dtUicTenantId") Long dtUicTenantId, @RequestParam("componentTypeCode") Integer componentTypeCode) {
        if (Objects.isNull(dtUicTenantId)) {
            throw new RdosDefineException(ErrorCode.TENANT_ID_NOT_NULL);
        }
        if (Objects.isNull(componentTypeCode)) {
            throw new RdosDefineException(ErrorCode.COMPONENT_TYPE_CODE_NOT_NULL);
        }
        return clusterService.pluginInfoForType(dtUicTenantId, null, componentTypeCode, false);
    }


    /**
     * 获取组件信息接口，迁移到 {@link com.dtstack.engine.api.service.ClusterService#componentInfoV2}
     * @param dtUicTenantId
     * @param dtUicUserId
     * @param componentTypCode
     * @param componentVersion
     * @return
     */
    @RequestMapping(value = "/componentInfo", method = {RequestMethod.POST})
    public String componentInfo(@RequestParam("dtUicTenantId") Long dtUicTenantId, @RequestParam("dtUicUserId") Long dtUicUserId, @RequestParam("componentTypCode") Integer componentTypCode, @RequestParam("componentVersion") String componentVersion) {
        if (Objects.isNull(dtUicTenantId)) {
            throw new RdosDefineException(ErrorCode.TENANT_ID_NOT_NULL);
        }
        if (Objects.isNull(componentTypCode)) {
            throw new RdosDefineException(ErrorCode.COMPONENT_TYPE_CODE_NOT_NULL);
        }
        return clusterService.componentInfo(dtUicTenantId, dtUicUserId, componentTypCode, componentVersion, null);
    }

    @RequestMapping(value = "/v2/componentInfo", method = {RequestMethod.POST})
    public String componentInfo(@RequestBody ComponentInfoParam componentInfoParam) {
        if (Objects.isNull(componentInfoParam.getDtUicTenantId())) {
            throw new RdosDefineException(ErrorCode.TENANT_ID_NOT_NULL);
        }
        if (Objects.isNull(componentInfoParam.getComponentTypCode())) {
            throw new RdosDefineException(ErrorCode.COMPONENT_TYPE_CODE_NOT_NULL);
        }
        return clusterService.componentInfo(componentInfoParam.getDtUicTenantId(), componentInfoParam.getDtUicUserId(), componentInfoParam.getComponentTypCode(),
                componentInfoParam.getComponentVersion(), componentInfoParam.getProjectVO());
    }

    @RequestMapping(value = "/clusterComponent", method = {RequestMethod.POST})
    public List<ClusterComponentVO> clusterComponent(@RequestParam("dtUicTenantId") Long dtUicTenantId, @RequestParam("multiVersion") boolean multiVersion) {
        if (Objects.isNull(dtUicTenantId)) {
            throw new RdosDefineException(ErrorCode.TENANT_ID_NOT_NULL);
        }
        List<Component> components = clusterService.clusterComponent(dtUicTenantId, multiVersion);
        if (CollectionUtils.isEmpty(components)) {
            return new ArrayList<>(0);
        }
        return components.stream().map(componentStruct::toClusterComponentVO).collect(Collectors.toList());
    }

    @RequestMapping(value = "/getMetaComponent", method = {RequestMethod.POST})
    public ClusterComponentVO getMetaComponent(@RequestParam("dtUicTenantId") Long dtUicTenantId) {
        if (Objects.isNull(dtUicTenantId)) {
            throw new RdosDefineException(ErrorCode.TENANT_ID_NOT_NULL);
        }
        Long clusterId = clusterService.getClusterId(dtUicTenantId);
        if (Objects.isNull(clusterId)) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        Component metadataComponent = componentService.getMetadataComponent(clusterId);
        return componentStruct.toClusterComponentVO(metadataComponent);
    }


    @PostMapping(value = "/getNamespace")
    public String componentInfo(@RequestParam("dtUicTenantId") Long dtUicTenantId) {
        if (Objects.isNull(dtUicTenantId)) {
            throw new RdosDefineException(ErrorCode.TENANT_ID_NOT_NULL);
        }
        Long clusterId = clusterService.getClusterId(dtUicTenantId);
        if (null == clusterId) {
            throw new RdosDefineException(ErrorCode.TENANT_NOT_BIND);
        }
        List<Component> components = componentService.getComponents(dtUicTenantId, EComponentType.KUBERNETES);
        if (CollectionUtils.isEmpty(components)) {
            throw new RdosDefineException("k8s component not found");
        }
        Queue queue = clusterService.getQueue(dtUicTenantId, clusterId, null);
        return queue == null ? "" : queue.getQueueName();
    }

    @PostMapping(value = "/getSparkThrift")
    public SparkThriftServerVO getSparkThrift(@RequestParam("dtUicTenantId") Long dtUicTenantId) {
        if (Langs.isNull(dtUicTenantId)) {
            throw new RdosDefineException(ErrorCode.TENANT_ID_NOT_NULL);
        }
        Cluster cluster = clusterService.getCluster(dtUicTenantId);
        if (null == cluster) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        //获取spark 地址信息
        String monitorUrl = redisTemplate.opsForValue().get(GlobalConst.SPARK_THRIFT_SERVER_MONITOR_KEY + cluster.getClusterName());
        if (StringUtil.isBlank(monitorUrl)) {
            try {
                sparkThriftServerMonitor.monitorTrigger(cluster);
                monitorUrl = redisTemplate.opsForValue().get(GlobalConst.SPARK_THRIFT_SERVER_MONITOR_KEY + cluster.getClusterName());
            } catch (Exception e) {
                throw new RdosDefineException(e);
            }
        }
        SparkThriftServerVO sparkThriftServerVO = new SparkThriftServerVO();
        sparkThriftServerVO.setJdbcUrl(monitorUrl);
        return sparkThriftServerVO;
    }

    @PostMapping(value = "/getConfigFilePath")
    public ClusterFilePathVO getConfigFilePath(@RequestParam("dtUicTenantId") Long dtUicTenantId) {
        if (Langs.isNull(dtUicTenantId)) {
            throw new RdosDefineException(ErrorCode.TENANT_ID_NOT_NULL);
        }
        Long clusterId = clusterService.getClusterId(dtUicTenantId);
        if (null == clusterId) {
            throw new RdosDefineException(ErrorCode.TENANT_NOT_BIND);
        }
        JSONObject sftpConf = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, JSONObject.class, new HashMap<>());
        if (sftpConf == null) {
            return null;
        }
        List<Component> components = componentService.getComponentsByClusterId(clusterId, EComponentType.HDFS);
        if (CollectionUtils.isEmpty(components)) {
            throw new RdosDefineException("hdfs component not found");
        }
        Component component = components.get(0);
        ClusterFilePathVO filePathVO = new ClusterFilePathVO();
        filePathVO.setSftpConf(sftpConf.toJSONString());
        filePathVO.setFileModifiedTime(component.getGmtModified());
        String filePrefixPath = componentService.buildConfRemoteDir(clusterId);
        filePathVO.setFilePath(sftpConf.getString(GlobalConst.PATH) + File.separator + filePrefixPath);
        return filePathVO;
    }


    @RequestMapping(value = "/getTenantClusterCommonConfig", method = {RequestMethod.POST})
    public TenantClusterCommonConfigVO getTenantClusterCommonConfig(@RequestParam("tenantId") Long tenantId) {
        AssertUtils.notNull(tenantId, "参数错误, 租户 id 不能为空");
        return clusterService.getTenantClusterCommonConfig(tenantId);
    }
}
