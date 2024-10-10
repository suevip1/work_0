package com.dtstack.engine.master.model.cluster;

import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.mockcontainer.impl.ComponentServiceMock;
import com.dtstack.engine.master.mockcontainer.model.cluster.ContextMock;
import com.dtstack.engine.master.model.cluster.system.Context;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.Collections;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-24 09:30
 */
public class PartClusterTest {
    PartCluster partCluster;
    Context context;
    DataSource dataSource;
    Long clusterId = -1L;


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

        dataSource = (DataSource) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{DataSource.class},
                (loader, method, args) -> {
                    if (method.getName().equals("listComponentConfig")) {
                        return Collections.emptyList();
                    }
                    if (method.getName().equals("listAllByClusterId")) {
                        return ComponentServiceMock.mockDefaultComponents();
                    }
                    return null;
                });

        context = new Context(dictDao);
        partCluster = new PartCluster(clusterId, context, dataSource);
    }

    @Test
    public void testCreate() {
        partCluster.create(EComponentType.YARN, "", EComponentType.HDFS, null);
    }
}