package com.dtstack.engine.common.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComponentConfigUtilsTest {

    private static final String componentConfigJson = "[{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"公共参数\",\"gmtCreate\":1621308540000,\"gmtModified\":1621308540000,\"id\":44547,\"isDeleted\":0,\"key\":\"jobmanager.rpc.address\",\"keyDescribe\":\"ha 模式下 jobmanager 远程调用地址\",\"required\":1,\"sort\":3,\"type\":\"INPUT\",\"value\":\"\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"公共参数\",\"gmtCreate\":1621308540000,\"gmtModified\":1621308540000,\"id\":44549,\"isDeleted\":0,\"key\":\"jobmanager.rpc.port\",\"keyDescribe\":\"ha 模式下 jobmanager 远程调用端口\",\"required\":1,\"sort\":4,\"type\":\"INPUT\",\"value\":\"\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"metric监控\",\"gmtCreate\":1621308540000,\"gmtModified\":1621308540000,\"id\":44553,\"isDeleted\":0,\"key\":\"prometheusHost\",\"keyDescribe\":\"prometheus地址，平台端使用\",\"required\":0,\"sort\":1,\"type\":\"INPUT\",\"value\":\"\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"metric监控\",\"gmtCreate\":1621308540000,\"gmtModified\":1621308540000,\"id\":44555,\"isDeleted\":0,\"key\":\"prometheusPort\",\"keyDescribe\":\"prometheus，平台端使用\",\"required\":0,\"sort\":2,\"type\":\"INPUT\",\"value\":\"\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"metric监控\",\"gmtCreate\":1621308540000,\"gmtModified\":1621308540000,\"id\":44557,\"isDeleted\":0,\"key\":\"metrics.reporter.promgateway.class\",\"keyDescribe\":\"用来推送指标类\",\"required\":1,\"sort\":3,\"type\":\"INPUT\",\"value\":\"\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"metric监控\",\"gmtCreate\":1621308540000,\"gmtModified\":1621308540000,\"id\":44561,\"isDeleted\":0,\"key\":\"metrics.reporter.promgateway.deleteOnShutdown\",\"keyDescribe\":\"任务结束后是否删除指标\",\"required\":1,\"sort\":6,\"type\":\"SELECT\",\"value\":\"true\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"metric监控\",\"gmtCreate\":1621308540000,\"gmtModified\":1621308540000,\"id\":44563,\"isDeleted\":0,\"key\":\"metrics.reporter.promgateway.randomJobNameSuffix\",\"keyDescribe\":\"是否在任务名上添加随机值\",\"required\":1,\"sort\":8,\"type\":\"SELECT\",\"value\":\"true\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"metric监控\",\"gmtCreate\":1621308540000,\"gmtModified\":1621308540000,\"id\":44565,\"isDeleted\":0,\"key\":\"metrics.reporter.promgateway.host\",\"keyDescribe\":\"promgateway地址\",\"required\":1,\"sort\":4,\"type\":\"INPUT\",\"value\":\"\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"metric监控\",\"gmtCreate\":1621308540000,\"gmtModified\":1621308540000,\"id\":44567,\"isDeleted\":0,\"key\":\"metrics.reporter.promgateway.jobName\",\"keyDescribe\":\"指标任务名\",\"required\":1,\"sort\":7,\"type\":\"INPUT\",\"value\":\"\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"metric监控\",\"gmtCreate\":1621308540000,\"gmtModified\":1621308540000,\"id\":44569,\"isDeleted\":0,\"key\":\"metrics.reporter.promgateway.port\",\"keyDescribe\":\"promgateway端口\",\"required\":1,\"sort\":5,\"type\":\"INPUT\",\"value\":\"\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"数栈平台参数\",\"gmtCreate\":1621308540000,\"gmtModified\":1621308540000,\"id\":44571,\"isDeleted\":0,\"key\":\"flinkJarPath\",\"keyDescribe\":\"flink lib path\",\"required\":1,\"sort\":2,\"type\":\"INPUT\",\"value\":\"/data/insight_plugin/flink110_lib\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"数栈平台参数\",\"gmtCreate\":1621308540000,\"gmtModified\":1621308540000,\"id\":44573,\"isDeleted\":0,\"key\":\"flinkPluginRoot\",\"keyDescribe\":\"flinkStreamSql和flinkx plugins父级本地目录\",\"required\":1,\"sort\":4,\"type\":\"INPUT\",\"value\":\"/data/insight_plugin\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"数栈平台参数\",\"gmtCreate\":1621308540000,\"gmtModified\":1621308540000,\"id\":44579,\"isDeleted\":0,\"key\":\"clusterMode\",\"keyDescribe\":\"任务执行模式：perjob,session,standalone\",\"required\":1,\"sort\":1,\"type\":\"INPUT\",\"value\":\"standalone\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependencyKey\":\"metrics.reporter.promgateway.deleteOnShutdown\",\"gmtCreate\":1621396105000,\"gmtModified\":1621396105000,\"id\":45167,\"isDeleted\":0,\"key\":\"false\",\"required\":0,\"type\":\"\",\"value\":\"false\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependencyKey\":\"metrics.reporter.promgateway.deleteOnShutdown\",\"gmtCreate\":1621396105000,\"gmtModified\":1621396105000,\"id\":45169,\"isDeleted\":0,\"key\":\"true\",\"required\":0,\"type\":\"\",\"value\":\"true\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependencyKey\":\"metrics.reporter.promgateway.randomJobNameSuffix\",\"gmtCreate\":1621396105000,\"gmtModified\":1621396105000,\"id\":45171,\"isDeleted\":0,\"key\":\"false\",\"required\":0,\"type\":\"\",\"value\":\"false\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependencyKey\":\"metrics.reporter.promgateway.randomJobNameSuffix\",\"gmtCreate\":1621396105000,\"gmtModified\":1621396105000,\"id\":45173,\"isDeleted\":0,\"key\":\"true\",\"required\":0,\"type\":\"\",\"value\":\"true\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"公共参数\",\"gmtCreate\":1653285720000,\"gmtModified\":1653285720000,\"id\":1294381,\"isDeleted\":0,\"key\":\"rest.port\",\"keyDescribe\":\"客户端连接到jobmanger的端口。如果rest.bind-port未指定，则REST服务器将绑定到此端口。注意:只有在高可用性配置为NONE时才会考虑此选项。\",\"required\":0,\"sort\":10,\"type\":\"INPUT\",\"value\":\"8081\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"高可用\",\"gmtCreate\":1653285720000,\"gmtModified\":1653285720000,\"id\":1294383,\"isDeleted\":0,\"key\":\"high-availability\",\"keyDescribe\":\"flink ha类型\",\"required\":0,\"sort\":1,\"type\":\"INPUT\",\"value\":\"ZOOKEEPER\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"高可用\",\"gmtCreate\":1653285720000,\"gmtModified\":1653285720000,\"id\":1294385,\"isDeleted\":0,\"key\":\"high-availability.cluster-id\",\"keyDescribe\":\"Flink集群ID，用于多个Flink集群之间的隔离。\",\"required\":1,\"sort\":5,\"type\":\"INPUT\",\"value\":\"/default\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"高可用\",\"gmtCreate\":1653285720000,\"gmtModified\":1653285720000,\"id\":1294387,\"isDeleted\":0,\"key\":\"high-availability.storageDir\",\"keyDescribe\":\"ha元数据存储路径\",\"required\":1,\"sort\":4,\"type\":\"INPUT\",\"value\":\"hdfs://ns1/dtInsight/flink110/ha\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"高可用\",\"gmtCreate\":1653285720000,\"gmtModified\":1653285720000,\"id\":1294389,\"isDeleted\":0,\"key\":\"high-availability.zookeeper.path.root\",\"keyDescribe\":\"ha节点路径\",\"required\":1,\"sort\":3,\"type\":\"INPUT\",\"value\":\"/flink110\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"高可用\",\"gmtCreate\":1653285720000,\"gmtModified\":1653285720000,\"id\":1294391,\"isDeleted\":0,\"key\":\"high-availability.zookeeper.quorum\",\"keyDescribe\":\"zookeeper地址，当ha选择是zookeeper时必填\",\"required\":1,\"sort\":2,\"type\":\"INPUT\",\"value\":\"\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"容错和checkpointing\",\"gmtCreate\":1653285720000,\"gmtModified\":1653285720000,\"id\":1294393,\"isDeleted\":0,\"key\":\"state.backend\",\"keyDescribe\":\"状态后端\",\"required\":1,\"sort\":5,\"type\":\"INPUT\",\"value\":\"\"},{\"clusterId\":-2,\"componentId\":-113,\"componentTypeCode\":20,\"dependName\":\"数栈平台参数\",\"gmtCreate\":1653285720000,\"gmtModified\":1653285720000,\"id\":1294395,\"isDeleted\":0,\"key\":\"pluginLoadMode\",\"keyDescribe\":\"插件加载类型\",\"required\":0,\"sort\":6,\"type\":\"INPUT\",\"value\":\"classpath\"}]";

    private static final String xmlConfigJson = "  {\n" +
            "    \"id\": 7561,\n" +
            "    \"cluster_id\": 61,\n" +
            "    \"component_id\": 303,\n" +
            "    \"component_type_code\": 5,\n" +
            "    \"type\": \"XML\",\n" +
            "    \"required\": 1,\n" +
            "    \"key\": \"dfs.block.access.token.enable\",\n" +
            "    \"value\": \"true\",\n" +
            "    \"values\": null,\n" +
            "    \"dependencyKey\": null,\n" +
            "    \"dependencyValue\": null,\n" +
            "    \"desc\": null,\n" +
            "    \"gmt_create\": \"2021-03-25 17:01:23\",\n" +
            "    \"gmt_modified\": \"2021-03-25 17:01:23\",\n" +
            "    \"is_deleted\": 0\n" +
            "  }";


    private static final String tipGroups = "{\"kubernetes\":{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"tips\",\"dictDesc\":\"0\",\"dictName\":\"kubernetes\",\"gmtCreate\":1654007652000,\"gmtModified\":1654007652000,\"id\":12427,\"isDeleted\":0,\"sort\":8,\"type\":25},\"数栈平台参数\":{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"tips\",\"dictDesc\":\"0\",\"dictName\":\"数栈平台参数\",\"gmtCreate\":1654007652000,\"gmtModified\":1654007652000,\"id\":12429,\"isDeleted\":0,\"sort\":9,\"type\":25},\"容错和checkpointing\":{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"tips\",\"dictDesc\":\"0\",\"dictName\":\"容错和checkpointing\",\"gmtCreate\":1654007652000,\"gmtModified\":1654007652000,\"id\":12419,\"isDeleted\":0,\"sort\":4,\"type\":25},\"其它\":{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"tips\",\"dictDesc\":\"0\",\"dictName\":\"其它\",\"gmtCreate\":1654007652000,\"gmtModified\":1654007652000,\"id\":12431,\"isDeleted\":0,\"sort\":10,\"type\":25},\"metric监控\":{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"tips\",\"dictDesc\":\"0\",\"dictName\":\"metric监控\",\"gmtCreate\":1654007652000,\"gmtModified\":1654007652000,\"id\":12417,\"isDeleted\":0,\"sort\":3,\"type\":25},\"自定义参数\":{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"tips\",\"dictDesc\":\"0\",\"dictName\":\"自定义参数\",\"gmtCreate\":1654007652000,\"gmtModified\":1654007652000,\"id\":12433,\"isDeleted\":0,\"sort\":11,\"type\":25},\"JVM参数\":{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"tips\",\"dictDesc\":\"0\",\"dictName\":\"JVM参数\",\"gmtCreate\":1654007652000,\"gmtModified\":1654007652000,\"id\":12423,\"isDeleted\":0,\"sort\":6,\"type\":25},\"高级\":{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"tips\",\"dictDesc\":\"0\",\"dictName\":\"高级\",\"gmtCreate\":1654007652000,\"gmtModified\":1654007652000,\"id\":12421,\"isDeleted\":0,\"sort\":5,\"type\":25},\"公共参数\":{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"tips\",\"dictDesc\":\"0\",\"dictName\":\"公共参数\",\"gmtCreate\":1654007652000,\"gmtModified\":1654007652000,\"id\":12405,\"isDeleted\":0,\"sort\":1,\"type\":25},\"Yarn\":{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"tips\",\"dictDesc\":\"0\",\"dictName\":\"Yarn\",\"gmtCreate\":1654007652000,\"gmtModified\":1654007652000,\"id\":12425,\"isDeleted\":0,\"sort\":7,\"type\":25},\"高可用\":{\"dataType\":\"STRING\",\"dependName\":\"\",\"dictCode\":\"tips\",\"dictDesc\":\"0\",\"dictName\":\"高可用\",\"gmtCreate\":1654007652000,\"gmtModified\":1654007652000,\"id\":12415,\"isDeleted\":0,\"sort\":2,\"type\":25}}";

    private static final List<ComponentConfig> componentConfigs;

    private static final Map<String, ScheduleDict> tipGroup;

    static {
        componentConfigs = JSONObject.parseObject(componentConfigJson,new TypeReference<List<ComponentConfig>>(){});
        tipGroup = JSONObject.parseObject(tipGroups,new TypeReference<Map<String, ScheduleDict>>(){});
    }

    @Test
    public void buildDBDataToClientTemplate() {
        List<ClientTemplate> clientTemplates = ComponentConfigUtils.buildDBDataToClientTemplate(componentConfigs);
        Assert.assertNotNull(clientTemplates);
        List<ClientTemplate> clientTemplates1 = ComponentConfigUtils.buildDBDataToClientTemplate(new ArrayList<>());
        Assert.assertTrue(clientTemplates1.isEmpty());
    }

    @Test
    public void buildDBDataToTipsClientTemplate() {
        List<ClientTemplate> clientTemplates = ComponentConfigUtils.buildDBDataToTipsClientTemplate(componentConfigs, 0, 0, tipGroup);
        Assert.assertNotNull(clientTemplates);
    }

    @Test
    public void convertComponentConfigToMap() {
        Map<String, Object> stringObjectMap = ComponentConfigUtils.convertComponentConfigToMap(componentConfigs);
        Assert.assertNotNull(stringObjectMap);
    }

    @Test
    public void convertClientTemplateToMap() {
        Map<String, Object> stringObjectMap = ComponentConfigUtils.convertClientTemplateToMap(ComponentConfigUtils.buildDBDataToTipsClientTemplate(componentConfigs, 0, 0, tipGroup));
        Assert.assertNotNull(stringObjectMap);
    }



    @Test
    public void convertXMLConfigToComponentConfig() {
       Assert.assertNotNull(ComponentConfigUtils.convertXMLConfigToComponentConfig(xmlConfigJson));
    }


    @Test
    public void fillClientTemplate() {
        String passwordComponentConfig = "  {\n" +
                "    \"id\": 7,\n" +
                "    \"cluster_id\": -2,\n" +
                "    \"component_id\": -106,\n" +
                "    \"component_type_code\": 9,\n" +
                "    \"type\": \"PASSWORD\",\n" +
                "    \"required\": 0,\n" +
                "    \"key\": \"password\",\n" +
                "    \"value\": \"\",\n" +
                "    \"values\": null,\n" +
                "    \"dependencyKey\": null,\n" +
                "    \"dependencyValue\": null,\n" +
                "    \"desc\": null,\n" +
                "    \"gmt_create\": \"2021-02-25 18:12:53\",\n" +
                "    \"gmt_modified\": \"2021-02-25 18:12:53\",\n" +
                "    \"is_deleted\": 0\n" +
                "  }";
        ComponentConfig password = JSONObject.parseObject(passwordComponentConfig, ComponentConfig.class);
        List<ClientTemplate> clientTemplates = ComponentConfigUtils.buildDBDataToClientTemplate(Lists.newArrayList(password));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("password","123456");
        ClientTemplate clientTemplate = clientTemplates.get(0);
        clientTemplate.setDependencyKey("auth");
        ComponentConfigUtils.fillClientTemplate(clientTemplate,jsonObject);
    }

    @Test
    public void buildOthers() {
        Assert.assertNotNull(ComponentConfigUtils.buildOthers("key","value"));
    }


}