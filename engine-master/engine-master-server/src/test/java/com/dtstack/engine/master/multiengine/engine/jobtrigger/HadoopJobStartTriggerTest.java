package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.master.mockcontainer.impl.HadoopJobStartTriggerMock;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Map;

@MockWith(HadoopJobStartTriggerMock.class)
public class HadoopJobStartTriggerTest {
    HadoopJobStartTrigger hadoopJobStartTrigger = new HadoopJobStartTrigger();

    @Test
    public void testSQLHadoopJobTrigger() throws Exception {
        ScheduleTaskShade scheduleTaskShade = getTemplate();
        scheduleTaskShade.setExtraInfo("{\"info\":\"{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[{\\\\\\\"gmtCreate\\\\\\\":1608882425000,\\\\\\\"gmtModified\\\\\\\":1608882425000,\\\\\\\"id\\\\\\\":157,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"paramCommand\\\\\\\":\\\\\\\"yyyyMMddHHmmss\\\\\\\",\\\\\\\"paramName\\\\\\\":\\\\\\\"bdp.system.cyctime\\\\\\\",\\\\\\\"taskId\\\\\\\":1487,\\\\\\\"type\\\\\\\":0}]\\\",\\\"sqlText\\\":\\\"use beihai_test1;\\\\nset hive.default.fileformat=parquet;\\\\ninsert into table_aka_99_1 VALUES(1,${bdp.system.cyctime});\\\\n\\\",\\\"computeType\\\":1,\\\"engineType\\\":\\\"spark\\\",\\\"taskParams\\\":\\\"## Driver程序使用的CPU核数,默认为1\\\\r\\\\n# driver.cores=1\\\\r\\\\n\\\\n## Driver程序使用内存大小,默认512m\\\\r\\\\n# driver.memory=512m\\\\r\\\\n\\\\n## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。\\\\n## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\\\\r\\\\n# driver.maxResultSize=1g\\\\r\\\\n\\\\n## SparkContext 启动时是否记录有效 SparkConf信息,默认false\\\\r\\\\n# logConf=false\\\\r\\\\n\\\\n## 启动的executor的数量，默认为1\\\\r\\\\nexecutor.instances=1\\\\r\\\\n\\\\n## 每个executor使用的CPU核数，默认为1\\\\r\\\\nexecutor.cores=1\\\\r\\\\n\\\\n## 每个executor内存大小,默认512m\\\\r\\\\n# executor.memory=512m\\\\r\\\\n\\\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\\\r\\\\njob.priority=10\\\\r\\\\n\\\\n## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\\\r\\\\n# logLevel = INFO\\\\r\\\\n\\\\n## spark中所有网络交互的最大超时时间\\\\r\\\\n# spark.network.timeout=120s\\\\r\\\\n\\\\n## executor的OffHeap内存，和spark.executor.memory配置使用\\\\r\\\\n# spark.yarn.executor.memoryOverhead\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"dirtyDataSourceType\\\":7,\\\"taskType\\\":0,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"1\\\",\\\"tenantId\\\":1,\\\"taskId\\\":1487}\"}");
        JSONObject jsonObject = JSONObject.parseObject(scheduleTaskShade.getExtraInfo());
        JSONObject info = jsonObject.getJSONObject("info");
        Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
        ScheduleJob job = new ScheduleJob();
        hadoopJobStartTrigger.readyForTaskStartTrigger(actionParam, scheduleTaskShade, job);
    }

    @NotNull
    private ScheduleTaskShade getTemplate() {
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
        return scheduleTaskShade;
    }



    @Test
    public void testShellAgentHadoopJobTrigger() {
        try {
            ScheduleTaskShade sqlTaskShade = getTemplate();
            sqlTaskShade.setAppType(AppType.RDOS.getType());
            sqlTaskShade.setEngineType(ScheduleEngineType.DTSCRIPT_AGENT.getVal());
            sqlTaskShade.setTaskType(EScheduleJobType.SHELL_ON_AGENT.getType());
            sqlTaskShade.setComputeType(1);
            sqlTaskShade.setTaskId(1487L);
            sqlTaskShade.setTaskParams("asdasfas=123\n" +
                    "\n" +
                    "node.label=aaaa\n" +
                    "node.label=default\n");
            sqlTaskShade.setExtraInfo("{\"info\":\"{\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[]\\\",\\\"sqlText\\\":\\\"#name s6\\\\n#type Shell\\\\n#author admin@dtstack.com\\\\n#create time 2020-11-24 14:38:05\\\\n#desc \\\\n\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 0 --app-type shell --app-name s6\\\",\\\"engineType\\\":\\\"dtScript\\\",\\\"taskParams\\\":\\\"worker.memory=512m\\\\nworker.cores=1\\\\nexclusive=false\\\\nworker.num=1\\\\njob.priority=10\\\",\\\"maxRetryNum\\\":3,\\\"userId\\\":1,\\\"dirtyDataSourceType\\\":7,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"s6\\\",\\\"tenantId\\\":1,\\\"taskId\\\":919}\"}");
            ScheduleJob job = new ScheduleJob();
            job.setJobId("4hsohrrlo7n0");
            JSONObject jsonObject = JSONObject.parseObject(sqlTaskShade.getExtraInfo());
            JSONObject info = jsonObject.getJSONObject("info");
            Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
            hadoopJobStartTrigger.readyForTaskStartTrigger(actionParam, sqlTaskShade, job);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
