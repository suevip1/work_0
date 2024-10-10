package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.mockcontainer.model.cluster.ContextMock;
import com.dtstack.engine.master.model.cluster.ComponentFacade;
import com.dtstack.engine.master.model.cluster.system.Context;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-04 19:42
 */
public class ScheduleDictServiceMock extends BaseMock {

    @MockInvoke(targetClass = Context.class)
    public List<String> listSm2EncryptKeys() {
        return Lists.newArrayList("password");
    }

    @MockInvoke(targetClass = ScheduleDictDao.class)
    ScheduleDict getByNameValue(Integer type, String dictName, String dictValue, String dependName) {
        ScheduleDict dict = new ScheduleDict();
        dict.setDictName(dictName);
        dict.setDictValue(dictValue);
        dict.setDependName(dependName);
        return dict;
    }

    @MockInvoke(targetClass = ScheduleDictDao.class)
    List<ScheduleDict> getByDependName(  Integer type,  String dependName) {
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ScheduleDictDao.class)
    List<ScheduleDict> listById(Long id, Integer size) {
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ScheduleDictDao.class)
    List<ScheduleDict> listDictByType(Integer type) {
        if (DictType.HADOOP_VERSION.type.equals(type)) {
            return ContextMock.mockHadoopVersion();
        }
        if (DictType.FLINK_VERSION.type.equals(type)) {
            return ContextMock.mockFlinkVersion();
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ComponentFacade.class)
    public List<Component> tryGetStorageComponent(Long clusterId) {
        return ComponentServiceMock.mockDefaultComponents()
                .stream().filter(c -> c.getComponentTypeCode().equals(EComponentType.HDFS))
                .collect(Collectors.toList());
    }

    @MockInvoke(targetClass = ComponentFacade.class)
    public Component tryGetResourceComponent(Long clusterId) {
        return ComponentServiceMock.mockDefaultComponents()
                .stream().filter(c -> c.getComponentTypeCode().equals(EComponentType.YARN))
                .findFirst().orElse(null);
    }
}