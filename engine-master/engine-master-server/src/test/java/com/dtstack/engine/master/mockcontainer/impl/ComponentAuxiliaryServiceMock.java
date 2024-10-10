package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.po.ConsoleComponentAuxiliary;
import com.dtstack.engine.po.ConsoleComponentAuxiliaryConfig;
import com.google.common.collect.Lists;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-24 19:32
 */
public class ComponentAuxiliaryServiceMock extends BaseMock {

    @MockInvoke(targetClass = ConsoleComponentAuxiliaryDao.class)
    ConsoleComponentAuxiliary queryByClusterAndComponentAndType(Long clusterId, Integer componentTypeCode) {
        ConsoleComponentAuxiliary auxiliary = new ConsoleComponentAuxiliary();
        auxiliary.setComponentTypeCode(componentTypeCode);
        auxiliary.setClusterId(clusterId);
        auxiliary.setOpen(GlobalConst.YES);
        return auxiliary;
    }

    @MockInvoke(targetClass = ConsoleComponentAuxiliaryDao.class, targetMethod = "removeByClusterAndComponentAndType")
    int removeByClusterAndComponentAndTypeOfConfigDao(Long clusterId, Integer componentTypeCode, String type) {
        return 1;
    }

    @MockInvoke(targetClass = ConsoleComponentAuxiliaryDao.class)
    int insertSelective(ConsoleComponentAuxiliary record) {
        return 1;
    }


    @MockInvoke(targetClass = ConsoleComponentAuxiliaryDao.class)
    List<ConsoleComponentAuxiliary> queryByClusterAndComponentCodeAndType(  Long clusterId,   Integer componentTypeCode,  List<String> types) {
        ConsoleComponentAuxiliary auxiliary1 = JSONObject.parseObject("{\"clusterId\":877,\"componentTypeCode\":5,\"type\":\"KNOX\",\"open\":1}", ConsoleComponentAuxiliary.class);
        ConsoleComponentAuxiliary auxiliary2 = JSONObject.parseObject("{\"clusterId\":877,\"componentTypeCode\":5,\"type\":\"LOG_TRACE\",\"open\":1}", ConsoleComponentAuxiliary.class);
        return Lists.newArrayList(auxiliary1, auxiliary2);
    }

    @MockInvoke(targetClass = ConsoleComponentAuxiliaryDao.class)
    ConsoleComponentAuxiliary queryByClusterAndComponentAndTypeAndSwitch(Long clusterId, Integer componentTypeCode, Integer aSwitch, String type) {
        ConsoleComponentAuxiliary auxiliary1 = JSONObject.parseObject("{\"clusterId\":877,\"componentTypeCode\":5,\"type\":\"KNOX\",\"open\":1}", ConsoleComponentAuxiliary.class);
        return auxiliary1;
    }


    @MockInvoke(targetClass = ConsoleComponentAuxiliaryDao.class)
    boolean switchAuxiliary(Long clusterId, Integer componentTypeCode, String type, Integer aSwitch) {
        return true;
    }

    @MockInvoke(targetClass = ConsoleComponentAuxiliaryConfigDao.class)
    int batchSave(@Param("list") List<ConsoleComponentAuxiliaryConfig> list) {
        return 1;
    }

    @MockInvoke(targetClass = ConsoleComponentAuxiliaryConfigDao.class)
    List<ConsoleComponentAuxiliaryConfig> listByAuxiliaryId( Integer auxiliaryId) {
        return JSONArray.parseArray("[{\"key\":\"url\",\"value\":\"a\"},{\"key\":\"user\",\"value\":\"a\"},{\"key\":\"password\",\"value\":\"04d675e222a8d9122dada17a4f6cbe301c46eb545242b9f1b90ea24047732d504293b4bfc90d2a57f802efba81c6491801a84d2814a0b1f4863b8e4994664f76557e2cfe062233b563aca86567144998cb7cf113f3f92362f3135ebe45c36b7cb383\"}]", ConsoleComponentAuxiliaryConfig.class);
    }


    @MockInvoke(targetClass = ConsoleComponentAuxiliaryConfigDao.class)
    int removeByClusterAndComponentAndType(Long clusterId, Integer componentTypeCode, String type) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleDictDao.class)
    ScheduleDict getByNameValue(Integer type, String dictName, String dictValue, String dependName) {
        ScheduleDict dict = new ScheduleDict();
        if (type.equals(DictType.KNOX.type)) {
            dict.setDictValue("999");
        } else if (type.equals(DictType.LOG_TRACE.type)) {
            dict.setDictValue("888");

        }
        return dict;
    }

    @MockInvoke(targetClass = ComponentConfigDao.class)
    List<ComponentConfig> listByComponentId(Long componentId, boolean isFilter) {
        return JSONArray.parseArray("[{\"key\":\"url\",\"desc\":\"地址\",\"required\":1,\"value\":\"\",\"type\":\"INPUT\"},{\"key\":\"user\",\"desc\":\"用户名\",\"required\":1,\"value\":\"\",\"type\":\"INPUT\"},{\"key\":\"password\",\"desc\":\"密码\",\"required\":1,\"value\":\"\",\"type\":\"PASSWORD\"}]", ComponentConfig.class);
    }

}
