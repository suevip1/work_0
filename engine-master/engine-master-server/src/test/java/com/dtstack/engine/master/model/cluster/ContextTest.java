package com.dtstack.engine.master.model.cluster;

import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.processor.annotation.EnablePrivateAccess;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.common.enums.EComponentScheduleType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployType;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.mockcontainer.impl.ComponentServiceMock;
import com.dtstack.engine.master.mockcontainer.model.cluster.ContextMock;
import com.dtstack.engine.master.model.cluster.part.*;
import com.dtstack.engine.master.model.cluster.system.Context;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-30 20:09
 */
@MockWith(value = ContextMock.class)
@EnablePrivateAccess(srcClass = Context.class)
public class ContextTest {

    Context context = null;

    PartImpl partImpl = null;

    ResourcePart resourcePart = null;

    StoragePart storagePart = null;

    StandaloneComputePart standaloneComputePart = null;

    SingleComputePart singleComputePart = null;

    DependComputePart dependComputePart = null;

    DependNoVersionStoragePart dependNoVersionStoragePart = null;

    DependNoVersionComputePart dependNoVersionComputePart = null;

    @Before
    public void before() {
        ScheduleDictDao dictDao = (ScheduleDictDao) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{ScheduleDictDao.class},
                (loader, method, args) -> {
                    if (method.getName().equals("listDictByType")) {
                        Integer dictType = (Integer) args[0];
                        if (dictType == null) {
                            return Collections.emptyList();
                        }
                        if (DictType.HADOOP_VERSION.type.equals(dictType)) {
                            return ContextMock.mockHadoopVersion();
                        }
                        if (DictType.COMPONENT_MODEL.type.equals(dictType)) {
                            return ContextMock.mockComponentModel();
                        }
                        if (DictType.TYPENAME_MAPPING.type.equals(dictType)) {
                            return ContextMock.mockTypeName();
                        }
                        if (DictType.RESOURCE_MODEL_CONFIG.type.equals(dictType)) {
                            return ContextMock.mockComponentModelConfig();
                        }
                        if (DictType.EXTRA_VERSION_TEMPLATE.type.equals(dictType)) {
                            return ContextMock.mockExtraConfig();
                        }
                        if (DictType.TASK_CLIENT_TYPE.type.equals(dictType)) {
                            return ContextMock.mockTaskClientType();
                        }
                        return Collections.emptyList();
                    }
                    return null;
                });

        DataSource dataSource = (DataSource) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{DataSource.class},
                (loader, method, args) -> {
                    if (method.getName().equals("listComponentConfig")) {
                        return Collections.emptyList();
                    }
                    return null;
                });

        context = new Context(dictDao);

        // yarn
        Map<EComponentScheduleType, List<Component>> scheduleTypeComponentsMap = ComponentServiceMock.mockDefaultComponents()
                .stream()
                .collect(Collectors.groupingBy(c -> context.getOwner(EComponentType.getByCode(c.getComponentTypeCode())),
                        Collectors.collectingAndThen(Collectors.toList(), component -> component)));
        partImpl = new PartImpl(EComponentType.SFTP, "", null,
                scheduleTypeComponentsMap, context, dataSource, null);

        resourcePart = new ResourcePart(EComponentType.YARN, "Apache Hadoop 2.x", EComponentType.HDFS,
                scheduleTypeComponentsMap, context, dataSource);

        storagePart = new StoragePart(EComponentType.HDFS, "Apache Hadoop 2.x", EComponentType.HDFS,
                scheduleTypeComponentsMap, context, dataSource);

        standaloneComputePart = new StandaloneComputePart(EComponentType.FLINK, "1.10", EComponentType.HDFS,
                scheduleTypeComponentsMap, context, dataSource, EDeployType.YARN);

        singleComputePart = new SingleComputePart(EComponentType.MYSQL, "", null,
                scheduleTypeComponentsMap, context, dataSource);

        dependComputePart = new DependComputePart(EComponentType.FLINK, "1.10", EComponentType.HDFS,
                scheduleTypeComponentsMap, context, dataSource, EDeployType.YARN);

        // k8s
        scheduleTypeComponentsMap = ComponentServiceMock.mockDefaultComponentsK8s().stream()
                .collect(Collectors.groupingBy(c -> context.getOwner(EComponentType.getByCode(c.getComponentTypeCode())),
                        Collectors.collectingAndThen(Collectors.toList(), component -> component)));

        dependNoVersionStoragePart = new DependNoVersionStoragePart(EComponentType.HDFS, "Apache Hadoop 2.x", EComponentType.HDFS,
                scheduleTypeComponentsMap, context, dataSource);

        dependNoVersionComputePart = new DependNoVersionComputePart(EComponentType.FLINK, "1.10", EComponentType.HDFS,
                scheduleTypeComponentsMap, context, dataSource, null);

    }

    /************ Context start ************/
    @Test
    public void getOwner() {
        System.out.println(context.getOwner(EComponentType.SFTP));
    }

    @Test
    public void getDependsOn() {
        System.out.println(context.getDependsOn(EComponentType.SFTP));
    }

    @Test
    public void getBaseTemplateId() {
        System.out.println(context.getBaseTemplateId("dummy"));
    }

    @Test
    public void getComponentModel() {
        System.out.println(context.getComponentModel(EComponentType.SFTP));
    }

    @Test
    public void getModelConfig() {
        System.out.println(context.getModelConfig(EComponentType.FLINK, "1.12"));
    }

    @Test
    public void getModelExtraVersionParameters() {
        System.out.println(context.getModelExtraVersionParameters(EComponentType.YARN, "BMR 2.x"));
    }

    @Test
    public void getClientType() {
        System.out.println(context.getClientType("hadoop"));
    }

    /************ Context end ************/

    /*********** PartImpl start **********/
    @Test
    public void testPartImpl() {
        System.out.println(partImpl.getPluginName());
        System.out.println(partImpl.loadTemplate());
        System.out.println(partImpl.getType());
        System.out.println(partImpl.getVersionValue());
        System.out.println(partImpl.getResourceType());
        System.out.println(partImpl.getExtraVersionParameters());
    }

    @Test
    public void testResourcePart() {
        System.out.println(resourcePart.getPluginName());
        System.out.println(resourcePart.getType());
        System.out.println(resourcePart.getVersionValue());
    }

    @Test
    public void testStoragePart() {
        System.out.println(storagePart.getPluginName());
        System.out.println(storagePart.getType());
        System.out.println(storagePart.getVersionValue());
        System.out.println(storagePart.getExtraVersionParameters());
    }

    @Test
    public void testGetStandaloneComputePart() {
        System.out.println(standaloneComputePart.getPluginName());
        System.out.println(standaloneComputePart.getVersionValue());
        System.out.println(standaloneComputePart.getExtraVersionParameters());
        System.out.println(standaloneComputePart.getType());
    }

    @Test
    public void testSingleComputePart() {
        System.out.println(singleComputePart.getPluginName());
        System.out.println(singleComputePart.getVersionValue());
    }

    @Test
    public void testDependComputePart() {
        System.out.println(dependComputePart.getType());
        System.out.println(dependComputePart.getPluginName());
        System.out.println(dependComputePart.getResourceType());
        System.out.println(dependComputePart.getVersionValue());
        System.out.println(dependComputePart.getExtraVersionParameters());
    }

    @Test
    public void testDependNoVersionStoragePart() {
        System.out.println(dependNoVersionStoragePart.getPluginName());
        System.out.println(dependNoVersionStoragePart.getVersionValue());
        System.out.println(dependNoVersionStoragePart.getExtraVersionParameters());
    }

    @Test
    public void testDependNoVersionComputePart() {
        System.out.println(dependNoVersionComputePart.getPluginName());
        System.out.println(dependNoVersionComputePart.getType());
        System.out.println(dependNoVersionComputePart.getExtraVersionParameters());
        System.out.println(dependNoVersionComputePart.getResourceType());
        System.out.println(dependNoVersionComputePart.getVersionValue());
    }
}