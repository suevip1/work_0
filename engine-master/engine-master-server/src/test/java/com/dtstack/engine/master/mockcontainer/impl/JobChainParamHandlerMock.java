package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.dao.ScheduleEngineProjectDao;
import com.dtstack.engine.dao.ScheduleJobChainOutputParamDao;
import com.dtstack.engine.dao.ScheduleTaskChainParamDao;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamCleaner;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamQuerier;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.dtstack.engine.po.ScheduleEngineProject;
import com.dtstack.engine.po.ScheduleJobChainOutputParam;
import com.dtstack.engine.po.ScheduleTaskChainParam;
import com.dtstack.schedule.common.enums.EOutputParamType;
import org.assertj.core.util.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobChainParamHandlerMock {


    @MockInvoke(targetClass = PluginInfoManager.class)
    public JSONObject buildTaskPluginInfo(Long projectId,
                                          Integer appType,
                                          Integer taskType,
                                          Long dtUicTenantId,
                                          String engineTypeStr,
                                          Long dtUicUserId,
                                          Integer deployMode,
                                          Long resourceId,
                                          Map<Integer, String> componentVersionMap) {
        return new JSONObject();
    }


    @MockInvoke(targetClass = JobChainParamCleaner.class)
    public void asyncCleanJobOutputProcessedParamsIfNeed(List<ScheduleJobChainOutputParam> chainOutputParams) {
        return;

    }

    @MockInvoke(targetClass = JobParamReplace.class)
    public Map<String, String> parseTaskParam(List<ScheduleTaskParamShade> paramList, String cycTime, Integer scheduleType) {
        return new HashMap<>();
    }

    @MockInvoke(targetClass = JobParamReplace.class)
    public String paramReplace(String sql, List<ScheduleTaskParamShade> paramList, String cycTime, Integer scheduleType) {
        return sql;
    }

    @MockInvoke(targetClass = ScheduleJobChainOutputParamDao.class)
    int deleteOutputParamsByTask(Long taskId, Integer appType, Integer jobType) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobChainOutputParamDao.class)
    int batchSave(List<ScheduleJobChainOutputParam> list) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobChainOutputParamDao.class)
    int deleteByJobId(String jobId) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobChainOutputParamDao.class)
    List<ScheduleJobChainOutputParam> listByJobId(String jobId) {
        ScheduleJobChainOutputParam scheduleJobChainOutputParam = new ScheduleJobChainOutputParam();
        scheduleJobChainOutputParam.setParamName("output_cons_1");
        scheduleJobChainOutputParam.setParamCommand("'a'");
        scheduleJobChainOutputParam.setTaskType(EScheduleJobType.SHELL.getType());
        scheduleJobChainOutputParam.setOutputParamType(EOutputParamType.PROCESSED.getType());
        return Lists.newArrayList(scheduleJobChainOutputParam);
    }

    @MockInvoke(targetClass = ScheduleJobChainOutputParamDao.class)
    List<ScheduleJobChainOutputParam> listByOutputParamType( String jobId,  Integer outputParamType) {
        ScheduleJobChainOutputParam scheduleJobChainOutputParam = new ScheduleJobChainOutputParam();
        scheduleJobChainOutputParam.setParamName("output_cons_1");
        scheduleJobChainOutputParam.setParamCommand("'a'");
        scheduleJobChainOutputParam.setTaskType(EScheduleJobType.SHELL.getType());
        scheduleJobChainOutputParam.setOutputParamType(EOutputParamType.PROCESSED.getType());
        return Lists.newArrayList(scheduleJobChainOutputParam);
    }

    @MockInvoke(targetClass = ScheduleJobChainOutputParamDao.class)
    List<ScheduleJobChainOutputParam> listOutputParamsByTask(Long taskId, Integer appType, Integer jobType) {
        ScheduleJobChainOutputParam scheduleJobChainOutputParam = new ScheduleJobChainOutputParam();
        scheduleJobChainOutputParam.setTaskId(taskId);
        scheduleJobChainOutputParam.setAppType(appType);
        scheduleJobChainOutputParam.setParamName("output_cons_1");
        scheduleJobChainOutputParam.setParamCommand("'a'");
        scheduleJobChainOutputParam.setTaskType(EScheduleJobType.SHELL.getType());
        scheduleJobChainOutputParam.setOutputParamType(EOutputParamType.PROCESSED.getType());
        return Lists.newArrayList(scheduleJobChainOutputParam);
    }

    @MockInvoke(targetClass = ScheduleTaskChainParamDao.class)
    List<ScheduleTaskChainParam> listParamsByTask(Long taskId, Integer appType, Integer type) {
        ScheduleTaskChainParam scheduleTaskChainParam = new ScheduleTaskChainParam();
        scheduleTaskChainParam.setTaskId(taskId);
        scheduleTaskChainParam.setAppType(appType);
        scheduleTaskChainParam.setType(type);
        scheduleTaskChainParam.setParamName("output_cons_1");
        scheduleTaskChainParam.setParamCommand("'a'");
        scheduleTaskChainParam.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        scheduleTaskChainParam.setOutputParamType(EOutputParamType.CUSTOMIZE.getType());
        if (100L == taskId) {
            scheduleTaskChainParam.setTaskType(EScheduleJobType.SHELL.getType());
            scheduleTaskChainParam.setOutputParamType(EOutputParamType.PROCESSED.getType());
        }
        return Lists.newArrayList(scheduleTaskChainParam);
    }

    @MockInvoke(targetClass = ScheduleEngineProjectDao.class)
    ScheduleEngineProject getProjectByProjectIdAndApptype(Long projectId, Integer appType) {
        ScheduleEngineProject engineProject = new ScheduleEngineProject();
        engineProject.setProjectId(projectId);
        engineProject.setProjectId(1L);
        return engineProject;
    }


    @MockInvoke(targetClass = ClusterService.class)
    public JSONObject pluginInfoJSON(Long projectId, Integer appType, Integer taskType, Long dtUicTenantId, String engineTypeStr, Long dtUicUserId, Integer deployMode, Long resourceId, Map<Integer, String> componentVersionMap) {
        return new JSONObject();

    }

    @MockInvoke(targetClass = ComponentService.class)
    public String buildHdfsTypeName(Long dtUicTenantId, Long clusterId) {
        return "hdfs2";
    }

    @MockInvoke(targetClass = JobChainParamQuerier.class)
    public void deleteHdfsFiles(String engineType, String pluginInfo, List<String> deleteHdfsFiles) {
        return;
    }

    @MockInvoke(targetClass = JobChainParamQuerier.class)
    public boolean isAnyOneTooLarge(Long thresholdInBytes, String engineType, String pluginInfo, List<String> hdfsDirPaths) throws Exception {
        return true;
    }

    @MockInvoke(targetClass = RdosWrapper.class)
    public Integer getDataSourceCodeByDiceName(String diceName, EComponentType eComponentType) {
        return 1;
    }

}
