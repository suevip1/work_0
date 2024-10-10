package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class MrJobStartTriggerTest {
    MrJobStartTrigger mrJobStartTrigger =  new MrJobStartTrigger();


    @Test
    public void testDtScriptHadoopJobTrigger() {
        try {
            ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
            scheduleTaskShade.setTaskId(0L);
            scheduleTaskShade.setExtraInfo("{}");
            scheduleTaskShade.setTenantId(1L);
            scheduleTaskShade.setProjectId(1L);
            scheduleTaskShade.setNodePid(1L);
            scheduleTaskShade.setName("testJob");
            scheduleTaskShade.setTaskType(1);
            scheduleTaskShade.setEngineType(2);
            scheduleTaskShade.setComputeType(1);
            scheduleTaskShade.setSqlText("select");
            scheduleTaskShade.setTaskParams("null");
            scheduleTaskShade.setScheduleConf("{\"selfReliance\":false, \"min\":0,\"hour\":0,\"periodType\":\"2\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"isFailRetry\":true,\"maxRetryNum\":\"3\"}");
            scheduleTaskShade.setPeriodType(1);
            scheduleTaskShade.setScheduleStatus(1);
            scheduleTaskShade.setSubmitStatus(1);
            scheduleTaskShade.setGmtCreate(new Timestamp(1592559742000L));
            scheduleTaskShade.setGmtModified(new Timestamp(1592559742000L));
            scheduleTaskShade.setModifyUserId(1L);
            scheduleTaskShade.setCreateUserId(1L);
            scheduleTaskShade.setOwnerUserId(1L);
            scheduleTaskShade.setVersionId(1);
            scheduleTaskShade.setTaskDesc("null");
            scheduleTaskShade.setAppType(1);
            scheduleTaskShade.setIsDeleted(0);
            scheduleTaskShade.setMainClass("DataCollection");
            scheduleTaskShade.setExeArgs("null");
            scheduleTaskShade.setFlowId(0L);
            scheduleTaskShade.setDtuicTenantId(1L);
            scheduleTaskShade.setProjectScheduleStatus(0);
            scheduleTaskShade.setAppType(AppType.RDOS.getType());
            scheduleTaskShade.setEngineType(1);
            scheduleTaskShade.setTaskType(0);
            scheduleTaskShade.setComputeType(1);
            scheduleTaskShade.setTaskId(1487L);
            scheduleTaskShade.setAppType(AppType.RDOS.getType());
            scheduleTaskShade.setEngineType(6);
            scheduleTaskShade.setTaskType(EScheduleJobType.SHELL.getType());
            scheduleTaskShade.setComputeType(1);
            scheduleTaskShade.setTaskId(1487L);
            scheduleTaskShade.setExtraInfo("{\"info\":\"{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name s6\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2020-11-24 14:38:05\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name s6\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"worker.memory=512m\\\\nworker.cores=1\\\\nexclusive=false\\\\nworker.num=1\\\\njob.priority=10\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"dirtyDataSourceType\\\":7,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"s6\\\",\\\"tenantId\\\":1,\\\"taskId\\\":919}\"}");
            ScheduleJob job = new ScheduleJob();
            job.setJobId("4hsohrrlo7n0");
            JSONObject jsonObject = JSONObject.parseObject(scheduleTaskShade.getExtraInfo());
            JSONObject info = jsonObject.getJSONObject("info");
            Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
            mrJobStartTrigger.readyForTaskStartTrigger(actionParam, scheduleTaskShade, job);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Mock extends BaseMock {

        @MockInvoke(
                targetClass = JobParamReplace.class,
                targetMethod = "paramReplace"
        )
        public String paramReplace(String sql, List<ScheduleTaskParamShade> paramList, String cycTime,
                                   Integer scheduleType) {
            return null;
        }
    }
}
