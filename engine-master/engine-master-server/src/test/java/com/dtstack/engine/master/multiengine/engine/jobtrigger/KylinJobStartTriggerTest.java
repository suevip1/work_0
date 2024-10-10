package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Map;

public class KylinJobStartTriggerTest {
    KylinJobStartTrigger kylinJobStartTrigger = new KylinJobStartTrigger();

    @Test
    public void testReadyForTaskStartTrigger() throws Exception {
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
            String extraInfo = "{\"isFailRetry\":true,\"taskParamsToReplace\":\"[{\\\"gmtCreate\\\":1610445599000,\\\"gmtModified\\\":1610445599000,\\\"id\\\":1419,\\\"isDeleted\\\":0,\\\"paramCommand\\\":\\\"yyyyMMdd-1\\\",\\\"paramName\\\":\\\"bdp.system.bizdate\\\",\\\"taskId\\\":1571,\\\"type\\\":0}]\",\"sqlText\":\"\",\"computeType\":1,\"pluginInfo\":{\"password\":\"KYLIN\",\"typeName\":\"kylin\",\"hostPort\":\"http://172.16.101.17:7070\",\"connectParams\":null,\"cubeName\":\"kylin_sales_cube\",\"username\":\"ADMIN\"},\"engineType\":\"kylin\",\"taskParams\":\"\",\"maxRetryNum\":3,\"taskType\":4,\"ldapPassword\":\"admin123\",\"multiEngineType\":3,\"name\":\"regress_kylin_01\",\"tenantId\":1001,\"ldapUserName\":\"hxb\",\"taskId\":1571}";
            Map<String, Object> actionMap = JSONObject.parseObject(extraInfo, Map.class);
            scheduleTaskShade.setExeArgs("{\"sourceId\":\"395\",\"cubeName\":\"kylin_sales_cube\",\"startTime\":\"\",\"endTime\":\"\",\"isUseSystemVar\":true,\"systemVar\":\"${bdp.system.bizdate}\",\"noPartition\":false}");
            scheduleTaskShade.setEngineType(ScheduleEngineType.Kylin.getVal());
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setCycTime("20210112000000");
            kylinJobStartTrigger.readyForTaskStartTrigger(actionMap, scheduleTaskShade, scheduleJob);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
