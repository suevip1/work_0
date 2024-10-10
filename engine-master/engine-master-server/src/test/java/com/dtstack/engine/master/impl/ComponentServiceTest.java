package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.alibaba.testable.processor.annotation.EnablePrivateAccess;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.ComponentUserVO;
import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.engine.api.vo.task.TaskGetSupportJobTypesResultVO;
import com.dtstack.engine.common.Resource;
import com.dtstack.engine.common.client.bean.DtScriptAgentLabelDTO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.dtstack.engine.master.enums.DownloadType;
import com.dtstack.engine.master.mockcontainer.impl.ComponentConfigServiceMock;
import com.dtstack.engine.master.mockcontainer.impl.ComponentServiceMock;
import com.dtstack.engine.master.vo.JdbcUrlTipVO;
import com.dtstack.engine.po.ComponentUser;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-11 15:05
 */
@MockWith(value = ComponentServiceMock.class)
@EnablePrivateAccess(srcClass = ComponentService.class)
public class ComponentServiceTest {
    ComponentService componentService = new ComponentService();

    @Before
    public void before() {
        PrivateAccessor.set(componentService, "sftpFileManageBean", SftpFileManage.getInstance());
    }

    @Test
    public void init() {
        componentService.init();
    }

    @Test
    public void listConfigOfComponents() {
        assertTrue(CollectionUtils.isNotEmpty(componentService.listConfigOfComponents(-1L, null, null)));
    }

    @Test
    public void getOne() {
        assertTrue(componentService.getOne(ComponentConfigServiceMock.SPARK_COMPONENT_ID) != null);
    }

    @Test
    public void updateCache() {
        componentService.updateCache(-1L, EComponentType.SPARK.getTypeCode());
    }

    @Test
    public void getKerberosConfig() {
        assertTrue(componentService.getKerberosConfig(-1L, EComponentType.SPARK.getTypeCode(), "2.4") != null);
    }

    @Test
    public void uploadKerberos() {
        Resource resource = new Resource();
        resource.setFileName("kerberos_file_name.zip");
        resource.setUploadedFileName("kerberos_file_name.zip");
        assertTrue(StringUtils.isNotEmpty(componentService.uploadKerberos(Lists.newArrayList(resource), -1L, EComponentType.SPARK.getTypeCode(), "2.4")));
    }

    @Test
    public void mergeKrb5() {
        String mergeKrb5 = componentService.mergeKrb5();
        assertTrue(StringUtils.isNotEmpty(mergeKrb5));
    }

    @Test
    public void uploadSSL() {
        Resource resource = new Resource();
        resource.setUploadedFileName("ssl.zip");
        componentService.uploadSSL(resource, -1L, EComponentType.YARN.getTypeCode(), "");
    }

    @Test
    public void buildComponentPath() {
        String componentPath = componentService.buildComponentPath(-1L, EComponentType.SPARK.getTypeCode());
        assertTrue(StringUtils.isNotEmpty(componentPath));
    }

    @Test
    public void formatVersion() {
        String version = "210";
        assertTrue(version.equals(componentService.formatVersion(EComponentType.SPARK.getTypeCode(), version)));
    }

    @Test
    public void getSSLConfig() {
        assertNotNull(componentService.getSSLConfig(-1L, EComponentType.YARN.getTypeCode(), null));
    }

    @Test
    public void updateKrb5Conf() {
        String krb5Content = "[libdefaults]\n" +
                "default_realm = DTSTACK.COM\n" +
                "dns_lookup_kdc = false\n" +
                "dns_lookup_realm = false\n" +
                "ticket_lifetime = 600\n" +
                "renew_lifetime = 3600 \n" +
                "forwardable = true\n" +
                "default_tgs_enctypes = rc4-hmac aes256-cts\n" +
                "default_tkt_enctypes = rc4-hmac aes256-cts\n" +
                "permitted_enctypes = rc4-hmac aes256-cts\n" +
                "udp_preference_limit = 1\n" +
                "kdc_timeout = 3000\n" +
                "\n" +
                "[realms]\n" +
                "DTSTACK.COM = {\n" +
                "kdc = eng-cdh1\n" +
                "admin_server = eng-cdh1\n" +
                "default_domain = DTSTACK.COM\n" +
                "}\n" +
                "\n" +
                "[domain_realm]\n" +
                " .k.com = K.COM\n" +
                " k.com = K.COM\n" +
                " krb01.k.com = K.COM\n" +
                " eng-cdh1 = DTSTACK.COM\n" +
                " eng-cdh2 = DTSTACK.COM\n" +
                " eng-cdh3 = DTSTACK.COM\n" +
                " \n" +
                "\n";
        componentService.updateKrb5Conf(krb5Content);
    }

    @Test
    public void addOrUpdateComponent() {
        String componentConfig = "{\"jdbcUrl\":\"jdbc:mysql://172.16.100.186:3306/%s\",\"username\":\"drpeco1\",\"password\":\"048E82D17166186B69EE93C10C011C15D2C9D8542583E93B585F87668ED73AB917C1A437855950F93DA3053E9C190561DDDB7553D305D9A11EA7FD1287698EFB6110E5CADD70972C8469DB149BCDC3465A291EB1143D40CFEC01A75386CD450DB6F995A521FD771134C8A7FCF2\",\"maxJobPoolSize\":\"\",\"minJobPoolSize\":\"\"}";
        String componentTemplate = "[{\"dependName\":\"\",\"key\":\"jdbcUrl\",\"keyDescribe\":\"jdbc url地址\",\"required\":true,\"sort\":1,\"type\":\"INPUT\",\"value\":\"jdbc:mysql://172.16.100.186:3306/%s\"},{\"dependName\":\"\",\"key\":\"username\",\"keyDescribe\":\"jdbc连接用户名\",\"required\":false,\"sort\":2,\"type\":\"INPUT\",\"value\":\"drpeco1\"},{\"dependName\":\"\",\"key\":\"password\",\"keyDescribe\":\"jdbc连接密码\",\"required\":false,\"sort\":3,\"type\":\"PASSWORD\",\"value\":\"048E82D17166186B69EE93C10C011C15D2C9D8542583E93B585F87668ED73AB917C1A437855950F93DA3053E9C190561DDDB7553D305D9A11EA7FD1287698EFB6110E5CADD70972C8469DB149BCDC3465A291EB1143D40CFEC01A75386CD450DB6F995A521FD771134C8A7FCF2\"},{\"dependName\":\"\",\"key\":\"maxJobPoolSize\",\"keyDescribe\":\"任务最大线程数\",\"required\":false,\"sort\":4,\"type\":\"INPUT\",\"value\":\"\"},{\"dependName\":\"\",\"key\":\"minJobPoolSize\",\"keyDescribe\":\"任务最小线程数\",\"required\":false,\"sort\":5,\"type\":\"INPUT\",\"value\":\"\"}]";

        componentService.addOrUpdateComponent(877L, componentConfig, null, null, null,
                componentTemplate, EComponentType.MYSQL.getTypeCode(), null, null, null,
                false, true, null, null);
    }

    @Test
    public void changeTaskVersion() {
        Component updateComponent = new Component();
        updateComponent.setIsDefault(Boolean.TRUE);
        componentService.changeTaskVersion(-1L, EComponentType.SPARK, updateComponent, Lists.newArrayList("210"));
    }

    @Test
    public void refreshVersion() {
        Component addComponent = new Component();
        addComponent.setVersionName("Apache Hadoop 2.x");
        addComponent.setHadoopVersion("2.7.6");

        String dbHadoopVersion = null;
        Component dbComponent = new Component();
        BeanUtils.copyProperties(addComponent, dbComponent);
        addComponent.setVersionName("Apache Hadoop 3.x");
        addComponent.setHadoopVersion("3.0.0");

        componentService.refreshVersion(EComponentType.YARN, -1L, addComponent, dbHadoopVersion, dbComponent);
        componentService.refreshVersion(EComponentType.HDFS, -1L, addComponent, dbHadoopVersion, dbComponent);
    }

    @Test
    public void getSFTPConfig() {
        String sftpComponent = componentService.getComponentByClusterId(-1L, EComponentType.SFTP.getTypeCode(), false, String.class, null);
        SftpConfig sftpConfig = componentService.getSFTPConfig(sftpComponent, EComponentType.SPARK.getTypeCode(), "");
        assertNotNull(sftpConfig);
    }

    @Test
    public void buildConfRemoteDir() {
        assertTrue(StringUtils.isNotEmpty(componentService.buildConfRemoteDir(-1L)));
    }

    @Test
    public void closeSSL() {
        componentService.closeSSL(ComponentConfigServiceMock.SPARK_COMPONENT_ID);
    }

    @Test
    public void closeKerberos() {
        componentService.closeKerberos(ComponentConfigServiceMock.SPARK_COMPONENT_ID);
    }

    @Test
    public void addOrCheckClusterWithName() {
        ComponentsResultVO aDefault = componentService.addOrCheckClusterWithName("default-1");
        assertNotNull(aDefault);
    }

    @Test
    public void config() {
        Resource resource = new Resource();
        resource.setUploadedFileName("ssl.zip");
        List<Resource> resources = Lists.newArrayList(resource);
        List<Object> config = componentService.config(resources, EComponentType.SPARK.getTypeCode(), true, null);
    }

    @Test
    public void buildSftpPath() {
        String s = componentService.buildSftpPath(-1L, EComponentType.SPARK.getTypeCode(),null);
        assertTrue(StringUtils.isNotEmpty(s));
    }


    @Test
    public void testConnect1() {
        ComponentTestResult testResult = componentService.testConnect("default", EComponentType.YARN.getTypeCode(), "Apache Hadoop 2.x", -1L);
        assertNotNull(testResult);
    }

    @Test
    public void testConnects() {
        // List<ComponentMultiTestResult> results = componentService.testConnects("default", -1L);
    }

//    @Test
//    public void wrapperConfig() {
//        JSONObject jsonObject1 = new JSONObject();
//        jsonObject1.put("perjob",new JSONObject());
//
//        componentService.wrapperConfig(EComponentType.getByCode(EComponentType.SPARK.getTypeCode()),
//                jsonObject1.toJSONString(),
//                null,
//                null,
//                null,
//                null,
//                "");
//        assertTrue(Objects.nonNull(jsonObject));
//    }

    @Test
    public void getLocalKerberosPath() {
        String localKerberosPath = componentService.getLocalKerberosPath(-1L, EComponentType.SPARK.getTypeCode());
        assertTrue(StringUtils.isNotBlank(localKerberosPath));
    }

    @Test
    public void downloadFile() {
        File file = null;
        try {
            file = componentService.downloadFile(null, DownloadType.SSL_TEMPLATE.getCode(), EComponentType.SPARK.getTypeCode(),
                    null, -1L, null);
        } catch (Exception e) {
            if (e instanceof RdosDefineException) {
                assertTrue(((RdosDefineException) e).getErrorMessage().equals("file not found"));
            }
        }
    }

    @Test
    public void loadTemplate() {
        List<ClientTemplate> clientTemplates = componentService.loadTemplate(-1L, EComponentType.SFTP, null, null, null);
        assertTrue(CollectionUtils.isNotEmpty(clientTemplates));
    }

    @Test
    public void convertComponentTypeToClient() {
        String aDefault = componentService.convertComponentTypeToClient("default", EComponentType.SPARK.getTypeCode(), null, null, null);
    }

    @Test
    public void delete() {
        List<Integer> deletedComponentIds = Lists.newArrayList(ComponentConfigServiceMock.DELETE_SPARK_COMPONENT_ID)
                .stream().map(p -> p.intValue()).collect(Collectors.toList());
        componentService.delete(deletedComponentIds);
    }

    @Test
    public void getComponentVersion() {
        Map map = componentService.getComponentVersion(9999L);
        assertTrue(map.isEmpty());
    }

    @Test
    public void getComponentByClusterId() {
        Long clusterId = -1L;
        Integer componentType = EComponentType.SPARK.getTypeCode();
        String componentVersion = "2.1";
        Component component = componentService.getComponentByClusterId(clusterId, componentType, componentVersion);
        assertNotNull(component);

        Map<String, Object> map = componentService.getComponentByClusterId(-1L, componentType, true, Map.class, null, ComponentConfigServiceMock.SPARK_COMPONENT_ID);
        assertTrue(MapUtils.isNotEmpty(map));

        map = componentService.getComponentByClusterId(-1L, componentType, true, Map.class, null);
        assertTrue(MapUtils.isNotEmpty(map));

        map = componentService.getComponentByClusterId(ComponentConfigServiceMock.SPARK_COMPONENT_ID, true, Map.class);
        assertTrue(MapUtils.isNotEmpty(map));
    }

    @Test
    public void refresh() {
        componentService.refresh("default");
    }

    @Test
    public void getComponentStore() {
        List<Component> componentStore = componentService.getComponentStore("default", EComponentType.SPARK.getTypeCode());
        assertTrue(!componentStore.isEmpty());
    }

    @Test(expected = Exception.class)
    public void addOrUpdateNamespaces() {
        Long result = componentService.addOrUpdateNamespaces(-1L, "namespace", -1L, -1L);
        assertNotNull(result);
    }

    @Test
    public void isYarnSupportGpus() {
        Boolean result = componentService.isYarnSupportGpus("default");
        assertFalse(result);
    }

    @Test
    public void parseKerberos() {
        Resource resource = new Resource();
        resource.setFileName("kerberos_file_name.zip");
        resource.setUploadedFileName("kerberos_file_name.zip");
        List<Resource> resources = Lists.newArrayList(resource);
        List<String> result = componentService.parseKerberos(resources);
        assertTrue(CollectionUtils.isNotEmpty(result));
    }

    @Test
    public void changeMetadata() {
        try {
            boolean result = componentService.changeMetadata(EComponentType.HIVE_SERVER.getTypeCode(), true, -1L, 0);
        } catch (Exception e) {
            if (e instanceof RdosDefineException) {
                boolean equals = ErrorCode.CHANGE_META_NOT_PERMIT_WHEN_BIND_CLUSTER.getMsg()
                        .equals(((RdosDefineException) e).getErrorMessage());
                assertTrue(equals);
                return;
            }
        }
        fail();
    }

    @Test
    public void getSupportJobTypes() {
        List<TaskGetSupportJobTypesResultVO> supportJobTypes = componentService.getSupportJobTypes(AppType.RDOS.getType(), -1L, -1L);
        System.out.println(supportJobTypes);
    }

    @Test
    public void getDtScriptAgentLabel() {
        List<DtScriptAgentLabelDTO> dtScriptAgentLabel = componentService.getDtScriptAgentLabel("192.168.0.1");
        System.out.println(dtScriptAgentLabel);
    }

    @Test
    public void getComponentVersionByEngineType() {
        List<Component> components = componentService.getComponentVersionByEngineType(-1L, "spark");
        assertTrue(!components.isEmpty());
    }

    @Test
    public void getComponents() {
        List<Component> components = componentService.getComponents(-1L, EComponentType.SPARK);
        assertTrue(!components.isEmpty());
    }

    @Test
    public void getMetadataComponent() {
        Component metadataComponent = componentService.getMetadataComponent(-1L);
        assertTrue(metadataComponent != null);
    }

    @Test
    public void addOrUpdateComponentUser() {
        List<ComponentUserVO> componentUserList = new ArrayList<>();
        ComponentUserVO vo = new ComponentUserVO();
        vo.setComponentTypeCode(EComponentType.DT_SCRIPT.getTypeCode());
        vo.setIsDefault(true);
        vo.setLabelIp("127.0.0.1");
        vo.setLabel("default");
        componentUserList.add(vo);
        componentService.addOrUpdateComponentUser(componentUserList);
    }

    @Test
    public void getClusterComponentUser() {
        List<ComponentUserVO> clusterComponentUser = componentService.getClusterComponentUser(-1L, EComponentType.DTSCRIPT_AGENT.getTypeCode(), true, "127.0.0.1", false);
        System.out.println(clusterComponentUser);
    }

    @Test
    public void getComponentUser() {
        ComponentUser componentUser = componentService.getComponentUser(-1L, EComponentType.DTSCRIPT_AGENT.getTypeCode(), null, null);
        assertNotNull(componentUser);
    }

    @Test
    public void listComponents() {
        List<Component> components = componentService.listComponents(-1L, null);
        assertTrue(!components.isEmpty());
    }

    @Test
    public void listCommonComponents() {
        List<Component> components = componentService.listCommonComponents(-1L);
        assertTrue(!components.isEmpty());
    }

    @Test
    public void clearConfigCache() {
        componentService.clearConfigCache();
    }

    @Test
    public void listJdbcUrlTips() {
        List<JdbcUrlTipVO> jdbcUrlTipVOS = componentService.listJdbcUrlTips();
        assertTrue(!jdbcUrlTipVOS.isEmpty());
    }

    @Test
    public void buildHdfsTypeName() {
        System.out.println(componentService.buildHdfsTypeName(-1L, -1L));
    }

    @Test
    public void getComponentByTenantId() {
        String componentByTenantId = componentService.getComponentByTenantId(-1L, EComponentType.SPARK.getTypeCode());
        assertTrue(StringUtils.isNotEmpty(componentByTenantId));
    }
}