package com.dtstack.engine.master.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.OmniConstructor;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.param.AuxiliaryQueryParam;
import com.dtstack.engine.api.param.AuxiliarySelectQueryParam;
import com.dtstack.engine.api.vo.auxiliary.AuxiliaryConfigVO;
import com.dtstack.engine.master.dto.ComponentAuxiliaryConfigDTO;
import com.dtstack.engine.master.impl.ComponentAuxiliaryService;
import com.dtstack.engine.po.ConsoleComponentAuxiliary;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@MockWith(ComponentAuxiliaryControllerTest.ComponentAuxiliaryControllerMock.class)
public class ComponentAuxiliaryControllerTest {

    private static final ComponentAuxiliaryController controller = new ComponentAuxiliaryController();


    static class ComponentAuxiliaryControllerMock {

        public String componentConfigJson = "[\n" +
                "  {\n" +
                "    \"id\": 1207289,\n" +
                "    \"cluster_id\": -2,\n" +
                "    \"component_id\": -1000,\n" +
                "    \"component_type_code\": 5,\n" +
                "    \"type\": \"INPUT\",\n" +
                "    \"required\": 1,\n" +
                "    \"key\": \"url\",\n" +
                "    \"value\": \"\",\n" +
                "    \"values\": null,\n" +
                "    \"dependencyKey\": \"\",\n" +
                "    \"dependencyValue\": \"\",\n" +
                "    \"desc\": \"地址\",\n" +
                "    \"gmt_create\": \"2022-04-18 17:50:24\",\n" +
                "    \"gmt_modified\": \"2022-04-18 17:50:24\",\n" +
                "    \"is_deleted\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 1207291,\n" +
                "    \"cluster_id\": -2,\n" +
                "    \"component_id\": -1000,\n" +
                "    \"component_type_code\": 5,\n" +
                "    \"type\": \"INPUT\",\n" +
                "    \"required\": 1,\n" +
                "    \"key\": \"user\",\n" +
                "    \"value\": \"\",\n" +
                "    \"values\": null,\n" +
                "    \"dependencyKey\": \"\",\n" +
                "    \"dependencyValue\": \"\",\n" +
                "    \"desc\": \"用户名\",\n" +
                "    \"gmt_create\": \"2022-04-18 17:50:24\",\n" +
                "    \"gmt_modified\": \"2022-04-18 17:50:24\",\n" +
                "    \"is_deleted\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 1207293,\n" +
                "    \"cluster_id\": -2,\n" +
                "    \"component_id\": -1000,\n" +
                "    \"component_type_code\": 5,\n" +
                "    \"type\": \"PASSWORD\",\n" +
                "    \"required\": 1,\n" +
                "    \"key\": \"password\",\n" +
                "    \"value\": \"\",\n" +
                "    \"values\": null,\n" +
                "    \"dependencyKey\": \"\",\n" +
                "    \"dependencyValue\": \"\",\n" +
                "    \"desc\": \"密码\",\n" +
                "    \"gmt_create\": \"2022-04-18 17:50:24\",\n" +
                "    \"gmt_modified\": \"2022-04-18 17:50:24\",\n" +
                "    \"is_deleted\": 0\n" +
                "  }\n" +
                "]";

        @MockInvoke(targetClass = ComponentAuxiliaryService.class)
        public List<ComponentConfig> loadAuxiliaryTemplate(ConsoleComponentAuxiliary auxiliary) {
            return JSONObject.parseObject(componentConfigJson,new TypeReference<List<ComponentConfig>>() {});
        }

        @MockInvoke(targetClass = ComponentAuxiliaryService.class)
        public List<ConsoleComponentAuxiliary> listAuxiliaries(ConsoleComponentAuxiliary auxiliary) {
            return Lists.newArrayList(OmniConstructor.newArray(ConsoleComponentAuxiliary.class,3));
        }

        @MockInvoke(targetClass = ComponentAuxiliaryService.class)
        public ComponentAuxiliaryConfigDTO queryAuxiliaryConfig(ConsoleComponentAuxiliary auxiliary, boolean shouldEncrypt) {
            return OmniConstructor.newInstance(ComponentAuxiliaryConfigDTO.class);
        }

        @MockInvoke(targetClass = ComponentAuxiliaryService.class)
        public List<AuxiliaryConfigVO> queryConfig(AuxiliaryQueryParam auxiliaryQueryParam, boolean shouldEncrypt) {
            return Lists.newArrayList(OmniConstructor.newArray(AuxiliaryConfigVO.class,3));
        }

        @MockInvoke(targetClass = ComponentAuxiliaryService.class)
        public List<String> queryAuxiliarySelect(AuxiliarySelectQueryParam auxiliarySelectQueryParam) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ComponentAuxiliaryService.class)
        public void addAuxiliaryConfig(ComponentAuxiliaryConfigDTO auxiliaryConfigDTO) {}

        @MockInvoke(targetClass = ComponentAuxiliaryService.class)
        public void removeAuxiliaryConfig(ConsoleComponentAuxiliary auxiliary) {}

        @MockInvoke(targetClass = ComponentAuxiliaryService.class)
        public void switchAuxiliaryConfig(ConsoleComponentAuxiliary auxiliary) {}

        }



    @Test
    public void loadAuxiliaryTemplate() {
        controller.loadAuxiliaryTemplate(OmniConstructor.newInstance(ConsoleComponentAuxiliary.class));
    }

    @Test
    public void listAuxiliaries() {
        controller.listAuxiliaries(OmniConstructor.newInstance(ConsoleComponentAuxiliary.class));
    }

    @Test
    public void queryAuxiliaryConfig() {
        controller.queryAuxiliaryConfig(OmniConstructor.newInstance(ConsoleComponentAuxiliary.class));
    }

    @Test
    public void queryConfig() {
        controller.queryConfig(OmniConstructor.newInstance(AuxiliaryQueryParam.class));
    }

    @Test
    public void queryAuxiliarySelect() {
        controller.queryAuxiliarySelect(OmniConstructor.newInstance(AuxiliarySelectQueryParam.class));
    }

    @Test
    public void addAuxiliaryConfig() {
        controller.addAuxiliaryConfig(OmniConstructor.newInstance(ComponentAuxiliaryConfigDTO.class));
    }

    @Test
    public void removeAuxiliaryConfig() {
        controller.removeAuxiliaryConfig(OmniConstructor.newInstance(ConsoleComponentAuxiliary.class));
    }

    @Test
    public void switchAuxiliaryConfig() {
        controller.switchAuxiliaryConfig(OmniConstructor.newInstance(ConsoleComponentAuxiliary.class));
    }
}