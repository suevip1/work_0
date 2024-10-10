package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.processor.annotation.EnablePrivateAccess;
import com.dtstack.engine.api.param.AuxiliaryQueryParam;
import com.dtstack.engine.api.param.AuxiliarySelectQueryParam;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.dto.ComponentAuxiliaryConfigDTO;
import com.dtstack.engine.master.mockcontainer.impl.ComponentAuxiliaryServiceMock;
import com.dtstack.engine.po.ConsoleComponentAuxiliary;
import com.dtstack.engine.po.ConsoleComponentAuxiliaryConfig;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-24 19:15
 */

@MockWith(value = ComponentAuxiliaryServiceMock.class)
@EnablePrivateAccess(srcClass = ComponentAuxiliaryService.class)
public class ComponentAuxiliaryServiceTest {
    ComponentAuxiliaryService auxiliaryService = new ComponentAuxiliaryService();

    @Test
    public void loadAuxiliaryTemplate() {
        ConsoleComponentAuxiliary auxiliary = JSONObject.parseObject("{\"clusterId\":877,\"componentTypeCode\":5,\"type\":\"KNOX\"}", ConsoleComponentAuxiliary.class);
        auxiliaryService.loadAuxiliaryTemplate(auxiliary);
    }

    @Test
    public void testLoadAuxiliaryTemplate() {
        auxiliaryService.loadAuxiliaryTemplate("KNOX");
    }

    @Test
    public void listAuxiliaries() {
        ConsoleComponentAuxiliary auxiliary = new ConsoleComponentAuxiliary();
        auxiliary.setClusterId(877L);
        auxiliary.setComponentTypeCode(EComponentType.YARN.getTypeCode());
        List<ConsoleComponentAuxiliary> result = auxiliaryService.listAuxiliaries(auxiliary);
        assertTrue(!result.isEmpty());
    }

    @Test
    public void queryAuxiliaryConfig() {
        ConsoleComponentAuxiliary auxiliary = JSONObject.parseObject("{\"clusterId\":877,\"componentTypeCode\":5,\"type\":\"KNOX\"}", ConsoleComponentAuxiliary.class);
        auxiliaryService.queryAuxiliaryConfig(auxiliary, true);
    }

    @Test
    public void addAuxiliaryConfig() {
        ComponentAuxiliaryConfigDTO dto = new ComponentAuxiliaryConfigDTO();
        dto.setAuxiliaryConfigs(JSONArray.parseArray("[{\"key\":\"url\",\"value\":\"a\"},{\"key\":\"user\",\"value\":\"a\"},{\"key\":\"password\",\"value\":\"04d675e222a8d9122dada17a4f6cbe301c46eb545242b9f1b90ea24047732d504293b4bfc90d2a57f802efba81c6491801a84d2814a0b1f4863b8e4994664f76557e2cfe062233b563aca86567144998cb7cf113f3f92362f3135ebe45c36b7cb383\"}]", ConsoleComponentAuxiliaryConfig.class));
        dto.setAuxiliary(JSONObject.parseObject("{\"clusterId\":877,\"componentTypeCode\":5,\"type\":\"KNOX\"}", ConsoleComponentAuxiliary.class));
        auxiliaryService.addAuxiliaryConfig(dto);
    }

    @Test
    public void removeAuxiliaryConfig() {
        ConsoleComponentAuxiliary auxiliary = new ConsoleComponentAuxiliary(-1L, EComponentType.YARN.getTypeCode(), null);
        auxiliaryService.removeAuxiliaryConfig(auxiliary);
    }

    @Test
    public void switchAuxiliaryConfig() {
        ConsoleComponentAuxiliary auxiliary = JSONObject.parseObject("{\"clusterId\":877,\"componentTypeCode\":5,\"type\":\"KNOX\",\"open\":0}", ConsoleComponentAuxiliary.class);
        auxiliaryService.switchAuxiliaryConfig(auxiliary);
    }

    @Test
    public void queryOpenAuxiliaryConfig() {
        ConsoleComponentAuxiliary auxiliary = new ConsoleComponentAuxiliary();
        auxiliary.setClusterId(-1L);
        auxiliary.setComponentTypeCode(EComponentType.YARN.getTypeCode());
        auxiliary.setType("KNOX");
        auxiliaryService.queryOpenAuxiliaryConfig(auxiliary);
    }

    @Test
    public void queryConfig() {
        AuxiliaryQueryParam auxiliary = new AuxiliaryQueryParam();
        auxiliary.setClusterId(-1L);
        auxiliary.setTypes(Lists.newArrayList("KNOX"));
        auxiliary.setComponentTypeCode(EComponentType.YARN.getTypeCode());
        auxiliaryService.queryConfig(auxiliary, true);
    }

    @Test
    public void queryAuxiliarySelect() {
        AuxiliarySelectQueryParam param = new AuxiliarySelectQueryParam();
        param.setClusterId(-1L);
        param.setComponentTypeCode(EComponentType.YARN.getTypeCode());
        param.setType("KNOX");
        auxiliaryService.queryAuxiliarySelect(param);
    }
}