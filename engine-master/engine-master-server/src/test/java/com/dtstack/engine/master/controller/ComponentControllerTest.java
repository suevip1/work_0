package com.dtstack.engine.master.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.pojo.lineage.ComponentMultiTestResult;
import com.dtstack.engine.api.vo.ComponentUserVO;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.engine.api.vo.task.TaskGetSupportJobTypesResultVO;
import com.dtstack.engine.common.Resource;
import com.dtstack.engine.common.client.bean.DtScriptAgentLabelDTO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.vo.JdbcUrlTipVO;
import com.dtstack.engine.po.ComponentUser;
import com.dtstack.engine.po.ConsoleSSL;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MockWith(ComponentControllerTest.Mock.class)
public class ComponentControllerTest {

    private ComponentController controller = new ComponentController();

    public static class Mock  {

        @MockInvoke(targetClass=ComponentService.class)
        public List<ComponentsConfigOfComponentsVO> listConfigOfComponents(Long dtUicTenantId, Integer engineType, Map<Integer, String> componentVersionMap) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public Component getOne(Long id) {
            return new Component();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public void updateCache(Long clusterId, Integer componentCode) {

        }

        @MockInvoke(targetClass=ComponentService.class)
        public KerberosConfig getKerberosConfig(Long clusterId, Integer componentType, String componentVersion) {
            return new KerberosConfig();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public String uploadKerberos(List<Resource> resources, Long clusterId, Integer componentCode, String componentVersion) {
            return "";
        }

        @MockInvoke(targetClass=ComponentService.class)
        public String mergeKrb5() {
            return "";
        }

        @MockInvoke(targetClass=ComponentService.class)
        public void uploadSSL(Resource resource, Long clusterId, Integer componentCode, String componentVersion) {

        }

        @MockInvoke(targetClass=ComponentService.class)
        public String buildComponentPath(Long clusterId, Integer componentCode) {
            return "";
        }

        @MockInvoke(targetClass=ComponentService.class)
        public String formatVersion(Integer componentCode, String componentVersion) {
            return "";
        }

        @MockInvoke(targetClass=ComponentService.class)
        public ConsoleSSL getSSLConfig(Long clusterId, Integer componentCode, String componentVersion) {
            return new ConsoleSSL();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public void updateKrb5Conf(String krb5Content) {

        }

        @MockInvoke(targetClass=ComponentService.class)
        public ComponentVO addOrUpdateComponent(Long clusterId, String componentConfig, List<Resource> resources, String versionName, String kerberosFileName, String componentTemplate, Integer componentCode, Integer storeType, String principals, String principal, boolean isMetadata, Boolean isDefault, Integer deployType, String sslFileName) {
            return new ComponentVO();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public void changeTaskVersion(Long clusterId, EComponentType componentType, Component updateComponent, List<String> reserveComponentVersion) {
        }

        @MockInvoke(targetClass=ComponentService.class)
        public void refreshVersion(EComponentType componentType, Long clusterId, Component addComponent, String dbHadoopVersion, Component dbComponent) {
        }

        @MockInvoke(targetClass=ComponentService.class)
        public SftpConfig getSFTPConfig(String sftpConfigStr, Integer componentCode, String componentTemplate) {
            return new SftpConfig();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public String buildConfRemoteDir(Long clusterId) {
           return "";
        }

        @MockInvoke(targetClass=ComponentService.class)
        public void closeSSL(Long componentId) {
        }

        @MockInvoke(targetClass=ComponentService.class)
        public void closeKerberos(Long componentId) {

        }

        @MockInvoke(targetClass=ComponentService.class)
        public ComponentsResultVO addOrCheckClusterWithName(String clusterName) {
            return new ComponentsResultVO();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public List<Object> config(List<Resource> resources, Integer componentType, Boolean autoDelete, String version) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public String buildSftpPath(Long clusterId, Integer componentCode) {
            return "";
        }

        @MockInvoke(targetClass=ComponentService.class)
        public ComponentTestResult testConnect(Integer componentType, String componentConfig, String clusterName, String hadoopVersion, Long clusterId, KerberosConfig kerberosConfig, Map<String, String> sftpConfig, Integer storeType, String versionName, Integer deployType, Long dtuicTenantId, ConsoleSSL sslConfig) {
            return new ComponentTestResult();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public JSONObject wrapperConfig(int componentType, String componentConfig, Map<String, String> sftpConfig, KerberosConfig kerberosConfig, String clusterName, ConsoleSSL sslConfig) {
            return new JSONObject();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public String getLocalKerberosPath(Long clusterId, Integer componentCode) {
           return "";
        }

        @MockInvoke(targetClass=ComponentService.class)
        public File downloadFile(Long componentId, Integer downloadType, Integer componentType, String versionName, Long clusterId, Integer deployType) {
            return new File("sd");
        }

        @MockInvoke(targetClass=ComponentService.class)
        public List<ClientTemplate> loadTemplate(Long clusterId, EComponentType componentType, String versionName, EComponentType storeType, Integer deployType) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public String convertComponentTypeToClient(String clusterName, Integer componentType, String versionName, Integer storeType, Integer deployType) {
            return "";
        }

        @MockInvoke(targetClass=ComponentService.class)
        public void delete(List<Integer> componentIds) {

        }

        @MockInvoke(targetClass=ComponentService.class)
        public Map getComponentVersion(Long clusterId) {
            return new HashMap();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public Component getComponentByClusterId(Long clusterId, Integer componentType, String componentVersion) {
            return new Component();
        }


        @MockInvoke(targetClass=ComponentService.class)
        public List<ComponentTestResult> refresh(String clusterName) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public ComponentTestResult testConnect(String clusterName, Integer componentType, String versionName, Long dtuicTenantId) {
            return new ComponentTestResult();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public List<ComponentMultiTestResult> testConnects(String clusterName, Long dtuicTenantId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public List<Component> getComponentStore(String clusterName, Integer componentType) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public Long addOrUpdateNamespaces(Long clusterId, String namespace, Long queueId, Long dtUicTenantId) {
           return 1L;
        }

        @MockInvoke(targetClass=ComponentService.class)
        public Boolean isYarnSupportGpus(String clusterName) {
            return true;
        }

        @MockInvoke(targetClass=ComponentService.class)
        public List<String> parseKerberos(List<Resource> resourcesFromFiles) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public boolean changeMetadata(Integer componentType, boolean isMetadata, Long clusterId, Integer oldMetadata) {
           return true;
        }

        @MockInvoke(targetClass=ComponentService.class)
        public List<TaskGetSupportJobTypesResultVO> getSupportJobTypes(Integer appType, Long projectId, Long dtuicTenantId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public List<DtScriptAgentLabelDTO> getDtScriptAgentLabel(String agentAddress) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public List<Component> getComponentVersionByEngineType(Long uicTenantId, String engineType) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public List<Component> getComponents(Long uicTenantId, EComponentType componentType) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public Component getMetadataComponent(Long clusterId) {
            return new Component();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public void addOrUpdateComponentUser(List<ComponentUserVO> componentUserList) {

        }

        @MockInvoke(targetClass=ComponentService.class)
        public List<ComponentUserVO> getClusterComponentUser(Long clusterId, Integer componentTypeCode, Boolean needRefresh, String agentAddress, boolean uic) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public ComponentUser getComponentUser(Long dtUicId, Integer componentTypeCode, String label, String userName) {
            return new ComponentUser();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public List<Component> listComponents(Long dtUicTenantId, Integer engineType) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public List<Component> listCommonComponents(Long clusterId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public void clearConfigCache() {

        }

        @MockInvoke(targetClass=ComponentService.class)
        public List<JdbcUrlTipVO> listJdbcUrlTips() {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass=ComponentService.class)
        public String buildHdfsTypeName(Long dtUicTenantId, Long clusterId) {
           return "";
        }

        @MockInvoke(targetClass=ComponentService.class)
        public String getComponentByTenantId(Long tenantId, Integer componentType) {
            return "";
        }

        @MockInvoke(targetClass = ClusterService.class)
        public boolean hasRangerAndLdap(Long clusterId) {
            return true;
        }

        @MockInvoke(targetClass = ComponentService.class)
        public boolean syncDataSecurity(Long componentId) {
            return true;
        }

        @MockInvoke(targetClass = ComponentService.class)
        public boolean hasSecurityPolicy(Long componentId) {
            return true;
        }
        }

    @Test
    public void listJdbcUrlTips() {
        controller.listJdbcUrlTips();
    }

    @Test
    public void listConfigOfComponents() {
        controller.listConfigOfComponents(1L,1);
    }

    @Test
    public void listComponents() {
        controller.listComponents(1L,1);
    }

    @Test
    public void listCommonComponents() {
        controller.listCommonComponents(1L);
    }

    @Test
    public void hasRanger() {
        controller.hasRanger(1L);
    }

    @Test
    public void hasRangerAndLdap() {
        controller.hasRangerAndLdap(1L);
    }

    @Test
    public void getOne() {
        controller.getOne(1L);
    }

    @Test
    public void getKerberosConfig() {
        controller.getKerberosConfig(1L,1,"1.x");
    }

    @Test
    public void updateKrb5Conf() {
        controller.updateKrb5Conf("");
    }

    @Test
    public void closeKerberos() {
        controller.closeKerberos(1L);
    }

    @Test
    public void closeSSL() {
        controller.closeSSL(1L);
    }

    @Test
    public void addOrCheckClusterWithName() {
        controller.addOrCheckClusterWithName("");
    }

    @Test
    public void loadTemplate() {
        controller.loadTemplate(1,1L,"1.x",1,1);
    }

    @Test
    public void delete() {
        controller.delete(Lists.newArrayList(1));
    }

    @Test
    public void getComponentVersion() {
        controller.delete(Lists.newArrayList(1));
    }

    @Test
    public void getComponentStore() {
        controller.getComponentStore("default",1);
    }

    @Test
    public void testConnects() {
        controller.testConnects("default",1L);
    }

    @Test
    public void testConnect() {
        controller.testConnect("default",1,"1.x",1L);
    }

    @Test
    public void refresh() {
        controller.refresh("default");
    }

    @Test
    public void isYarnSupportGpus() {
        controller.isYarnSupportGpus("default");
    }

    @Test
    public void getSupportJobTypes() {
        controller.getSupportJobTypes(1,1L,1L);
    }

    @Test
    public void getDtScriptAgentLabel() {
        controller.getDtScriptAgentLabel("");
    }

    @Test
    public void getComponentVersionByEngineType() {
        controller.getComponentVersionByEngineType(1L,"1");
    }

    @Test
    public void addOrUpdateComponentUser() {
        controller.addOrUpdateComponentUser(new ArrayList<>());
    }

    @Test
    public void getClusterComponentUser() {
        controller.getClusterComponentUser(1L,1,true,"");
    }

    @Test
    public void getComponentUserByUic() {
        controller.getComponentUserByUic(1L,1,true,"");
    }

    @Test
    public void testSyncDataSecurity() {
        controller.syncDataSecurity(1L);
    }

    @Test
    public void testHasSecurityPolicy() {
        controller.hasSecurityPolicy(1L);
    }
}