package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ScheduleDictService;
import org.assertj.core.util.Lists;

import java.util.List;

public class RdosWrapperMock {


    @MockInvoke(targetClass = ScheduleDictService.class)
    public List<ScheduleDict> listByDictType(DictType dictType) {
        ScheduleDict scheduleDict = new ScheduleDict();
        JSONObject dictValue = new JSONObject();
        dictValue.put("Hadoop", 1);
        scheduleDict.setDictValue(dictValue.toJSONString());
        scheduleDict.setDictName(EComponentType.HDFS.getName());

        ScheduleDict scheduleDict2 = new ScheduleDict();
        JSONObject dictValue2 = new JSONObject();
        dictValue2.put("3.x-cdp", 1);
        scheduleDict2.setDictValue(dictValue2.toJSONString());
        scheduleDict2.setDictName(EComponentType.HIVE_SERVER.getName());
        return Lists.newArrayList(scheduleDict2,scheduleDict);
    }


    @MockInvoke(targetClass = ComponentService.class)
    public Component getComponentByClusterId(Long clusterId, Integer componentType, String componentVersion) {
        Component component = new Component();
        component.setVersionName("3.x-cdp");
        return component;
    }
}
