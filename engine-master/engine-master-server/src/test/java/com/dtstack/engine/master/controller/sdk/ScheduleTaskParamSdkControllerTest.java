package com.dtstack.engine.master.controller.sdk;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.vo.ScheduleTaskParamReplaceVO;
import com.dtstack.engine.master.impl.ParamService;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.BiConsumer;

@MockWith(ScheduleTaskParamSdkControllerMock.class)
public class ScheduleTaskParamSdkControllerTest {
    ScheduleTaskParamSdkController scheduleTaskParamSdkController = new ScheduleTaskParamSdkController();

    @Test
    public void testReplace() throws Exception {
        String str = "{  \"taskParamShades\" : [ {    \"id\" : 11597,    \"gmtCreate\" : 1656578832000,    \"gmtModified\" : 1656578832000,    \"isDeleted\" : 0,    \"taskId\" : 51175,    \"type\" : 1,    \"paramName\" : \"qiezi_1\",    \"paramCommand\" : \"dahaoren\",    \"version\" : 31629  } ],  \"replaceText\" : \"show tables;\",  \"cycTime\" : \"20220719000000\"}";
        ScheduleTaskParamReplaceVO parse = JSONObject.parseObject(str, ScheduleTaskParamReplaceVO.class);
        scheduleTaskParamSdkController.replace(parse);
    }
}

class ScheduleTaskParamSdkControllerMock {
    @MockInvoke(targetClass = ParamService.class)
    public void convertGlobalToParamType(List<ScheduleTaskParamShade> taskParamsToReplace, BiConsumer<List<ConsoleParam>, List<ScheduleTaskParamShade>> convertConsumer) {
        return;
    }

    @MockInvoke(targetClass = JobParamReplace.class)
    public String paramReplace(String sql, List<ScheduleTaskParamShade> paramList, String cycTime) {
        return "";
    }

    @MockInvoke(targetClass = JobParamReplace.class)
    public String paramReplace(String sql,
                               List<ScheduleTaskParamShade> paramList,
                               String cycTime,
                               Integer scheduleType,
                               Timestamp runtime) {
        return "";
    }
}