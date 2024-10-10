package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.api.dto.ConsoleParamBO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.param.CalenderParam;
import com.dtstack.engine.api.vo.console.param.ConsoleParamVO;
import com.dtstack.engine.master.impl.ParamService;
import com.dtstack.engine.master.mapstruct.ParamStruct;
import com.dtstack.schedule.common.enums.EParamType;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ParamSdkControllerTest {
    ParamSdkController paramSdkController = new ParamSdkController();

    @Test
    public void testGetAllParam() throws Exception {
        CalenderParam calenderParam = new CalenderParam();
        calenderParam.setCurrentPage(1);
        calenderParam.setPageSize(10);
        paramSdkController.getAllParam(calenderParam);
    }

    @Test
    public void testQueryByName() throws Exception {
        ConsoleParam consoleParam = new ConsoleParam();
        consoleParam.setParamName("test");
        paramSdkController.queryByName(consoleParam);
    }

    @Test
    public void testQueryTaskParam() throws Exception {
        ConsoleParam consoleParam = new ConsoleParam();
        consoleParam.setAppType(1);
        consoleParam.setTaskId(1L);
        paramSdkController.queryTaskParam(consoleParam);
    }

    @Test
    public void testFindById() throws Exception {
        ConsoleParam consoleParam = new ConsoleParam();
        consoleParam.setParamId(1L);
        paramSdkController.findById(consoleParam);
    }
}

class ParamSdkControllerMock {
    @MockInvoke(targetClass = ParamStruct.class)
    ConsoleParamVO toVo(ConsoleParam consoleParam) {
        return new ConsoleParamVO();
    }

    @MockInvoke(targetClass = ParamStruct.class)
    List<ConsoleParamVO> toVos(List<ConsoleParam> consoleParams) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ParamService.class)
    public int generalCount() {
        return 1;

    }

    @MockInvoke(targetClass = ParamService.class)
    public ConsoleParam selectByName(String paramName) {
        ConsoleParam consoleParam = new ConsoleParam();
        consoleParam.setParamName("test");
        consoleParam.setParamType(EParamType.CUSTOMIZE_TYPE.getType());
        return consoleParam;
    }

    @MockInvoke(targetClass = ParamService.class)
    public ConsoleParam findById(long paramId) {
        ConsoleParam consoleParam = new ConsoleParam();
        consoleParam.setParamName("test");
        consoleParam.setParamType(EParamType.CUSTOMIZE_TYPE.getType());
        return consoleParam;
    }

    @MockInvoke(targetClass = ParamService.class)
    public List<ConsoleParam> pageQuery(PageQuery query) {
        ConsoleParam consoleParam = new ConsoleParam();
        consoleParam.setParamName("test");
        consoleParam.setParamType(EParamType.CUSTOMIZE_TYPE.getType());
        return Lists.newArrayList(consoleParam);
    }

    @MockInvoke(targetClass = ParamService.class)
    public List<ConsoleParamBO> selectByTaskId(Long taskId, int appType) {
        ConsoleParamBO consoleParam = new ConsoleParamBO();
        consoleParam.setParamName("test");
        consoleParam.setParamType(EParamType.CUSTOMIZE_TYPE.getType());
        return Lists.newArrayList(consoleParam);
    }
}
