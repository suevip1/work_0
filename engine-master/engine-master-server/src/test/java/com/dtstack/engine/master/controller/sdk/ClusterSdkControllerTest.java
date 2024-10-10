package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.vo.components.ClusterComponentVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.mapstruct.ComponentStruct;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;

@MockWith(ClusterSdkControllerMock.class)
public class ClusterSdkControllerTest {

    ClusterSdkController clusterSdkController = new ClusterSdkController();

    @Test
    public void testPluginInfoByTypeCode() throws Exception {
        clusterSdkController.pluginInfoByTypeCode(1L, 0);
    }

    @Test
    public void testComponentInfo() throws Exception {
        clusterSdkController.componentInfo(1L, 1L, 0, "componentVersion");
    }

    @Test
    public void testClusterComponent() throws Exception {
        clusterSdkController.clusterComponent(1L, true);
    }

    @Test
    public void testGetMetaComponent() throws Exception {
        clusterSdkController.getMetaComponent(1L);
    }

    @Test
    public void testComponentInfo2() throws Exception {
        clusterSdkController.componentInfo(1L);
    }
}

class ClusterSdkControllerMock {

    @MockInvoke(targetClass = ClusterService.class)
    public String componentInfo(Long dtUicTenantId, Long dtUicUserId, Integer componentTypCode, String componentVersion) {
        return "";

    }

    @MockInvoke(targetClass = ClusterService.class)
    public Long getClusterId(Long dtUicTenantId) {
        return 1L;
    }

    @MockInvoke(targetClass = ComponentService.class)
    public Component getMetadataComponent(Long clusterId) {
        return new Component();
    }

    @MockInvoke(targetClass = ClusterService.class)
    public List<Component> clusterComponent(Long uicTenantId, boolean multiVersion) {
        return null;
    }

    @MockInvoke(targetClass = ClusterService.class)
    public Queue getQueue(Long dtUicTenantId, Long clusterId, Long resourceId) {
        return new Queue();

    }

    @MockInvoke(targetClass = ComponentService.class)
    public List<Component> getComponents(Long uicTenantId, EComponentType componentType) {
        return Lists.newArrayList(new Component());
    }

    @MockInvoke(targetClass = ComponentStruct.class)
    public ClusterComponentVO toClusterComponentVO(Component component) {
        ClusterComponentVO clusterComponentVO = new ClusterComponentVO();
        clusterComponentVO.setComponentTypeCode(EComponentType.YARN.getTypeCode());
        clusterComponentVO.setClusterId(1L);
        clusterComponentVO.setId(1L);
        return clusterComponentVO;
    }

    @MockInvoke(targetClass = ClusterService.class)
    public String pluginInfoForType(Long dtUicTenantId, Boolean fullKerberos, Integer pluginType,
                                    boolean needHdfsConfig) {
        return "";
    }
}