package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.dao.ScheduleJobChainOutputParamDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.dto.IdRangeDTO;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamQuerier;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.dtstack.engine.po.ScheduleJobChainOutputParam;
import com.dtstack.schedule.common.enums.EOutputParamType;
import org.apache.ibatis.annotations.Param;
import org.assertj.core.util.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JobChainParmaCleanerMock {


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


    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getAfterOrBeforeJobsLimitOne(Long taskId, boolean isAfter, String cycTime, Integer appType, Integer type) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId("test");
        scheduleJob.setDtuicTenantId(1L);
        return scheduleJob;

    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getRdosJobByJobId(String jobId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(jobId);
        scheduleJob.setDtuicTenantId(1L);
        scheduleJob.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        return scheduleJob;
    }


    @MockInvoke(targetClass = RdosWrapper.class)
    public Integer getDataSourceCodeByDiceName(String diceName, EComponentType eComponentType) {
        return 1;
    }

    @MockInvoke(targetClass = ClusterService.class)
    public JSONObject pluginInfoJSON(Long projectId, Integer appType, Integer taskType, Long dtUicTenantId, String engineTypeStr, Long dtUicUserId, Integer deployMode, Long resourceId, Map<Integer, String> componentVersionMap) {
        JSONObject pluginInfo = new JSONObject();
        return pluginInfo;
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
    public boolean deleteHdfsFile(String engineType, String pluginInfo, String deleteHdfsFile) {
        return true;
    }

    @MockInvoke(targetClass = ScheduleJobChainOutputParamDao.class)
    List<ScheduleJobChainOutputParam> listTempJobOutputParamsByThreshold(Integer retainInDay) {
        ScheduleJobChainOutputParam scheduleJobChainOutputParam = new ScheduleJobChainOutputParam();
        scheduleJobChainOutputParam.setJobId("test");
        scheduleJobChainOutputParam.setJobType(EScheduleJobType.SHELL.getType());
        scheduleJobChainOutputParam.setParamName("test");
        scheduleJobChainOutputParam.setParamCommand("echo $a");
        scheduleJobChainOutputParam.setParamValue("/data/job/1/11/output_output_process_1_4iefacmktc3f_20220403235000");
        scheduleJobChainOutputParam.setOutputParamType(EOutputParamType.PROCESSED.getType());
        return Lists.newArrayList(scheduleJobChainOutputParam);
    }

    @MockInvoke(targetClass = ScheduleJobChainOutputParamDao.class)
    int deleleBatchByIds(List<Integer> ids) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleDictDao.class)
    ScheduleDict getByNameValue(Integer type, String dictName, String dictValue, String dependName) {
        ScheduleDict scheduleDict = new ScheduleDict();
        scheduleDict.setDictValue("{\"deleteDateConfig\":396}");
        return scheduleDict;
    }

    @MockInvoke(targetClass = ScheduleJobChainOutputParamDao.class)
    long queryCountByCondition(String whereSql) {
        return 1L;
    }


    @MockInvoke(targetClass = ScheduleJobChainOutputParamDao.class)
    IdRangeDTO queryIdRange(String whereSql) {
        IdRangeDTO idRangeDTO = new IdRangeDTO();
        idRangeDTO.setEndId(100L);
        idRangeDTO.setStartId(1L);
        return idRangeDTO;
    }

    @MockInvoke(targetClass = ScheduleJobChainOutputParamDao.class)
    int deleteByJobId(String jobId) {
        return 1;
    }


    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    ScheduleTaskShade getOneIncludeDelete(Long taskId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskType(EScheduleJobType.SHELL.getType());
        scheduleTaskShade.setProjectId(1L);
        scheduleTaskShade.setDtuicTenantId(1L);
        scheduleTaskShade.setAppType(1);
        return scheduleTaskShade;
    }

    @MockInvoke(targetClass = ScheduleJobChainOutputParamDao.class)
    List<ScheduleJobChainOutputParam> queryByCondition(String whereSql) {
        ScheduleJobChainOutputParam scheduleJobChainOutputParam = new ScheduleJobChainOutputParam();
        scheduleJobChainOutputParam.setJobId("test");
        scheduleJobChainOutputParam.setJobType(EScheduleJobType.SHELL.getType());
        scheduleJobChainOutputParam.setParamName("test");
        scheduleJobChainOutputParam.setParamCommand("echo $a");
        scheduleJobChainOutputParam.setParamValue("/data/job/1/11/output_output_process_1_4iefacmktc3f_20220403235000");
        scheduleJobChainOutputParam.setOutputParamType(EOutputParamType.PROCESSED.getType());
        return Lists.newArrayList(scheduleJobChainOutputParam);
    }

    @MockInvoke(targetClass = ScheduleJobChainOutputParamDao.class)
    long deleteByCondition(String whereSql) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobChainOutputParamDao.class)
    List<ScheduleJobChainOutputParam> listByOutputParamType(String jobId, Integer outputParamType) {
        ScheduleJobChainOutputParam scheduleJobChainOutputParam = new ScheduleJobChainOutputParam();
        scheduleJobChainOutputParam.setJobId("test");
        scheduleJobChainOutputParam.setJobType(EScheduleJobType.SHELL.getType());
        scheduleJobChainOutputParam.setParamName("test");
        scheduleJobChainOutputParam.setParamCommand("echo $a");
        scheduleJobChainOutputParam.setParamValue("a");
        scheduleJobChainOutputParam.setTaskId(100L);
        scheduleJobChainOutputParam.setAppType(1);
        scheduleJobChainOutputParam.setOutputParamType(EOutputParamType.CONS.getType());
        return Lists.newArrayList(scheduleJobChainOutputParam);
    }


    @MockInvoke(targetClass = ScheduleJobDao.class)
    public List<ScheduleJob> getJobRangeByCycTimeByLimit(@Param("taskId") Long taskId, @Param("isAfter") boolean isAfter, @Param("cycTime") String cycTime, @Param("appType") Integer appType,
                                                   @Param("type") Integer type, @Param("limit") Integer limit, @Param("fillId")Long fillId) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = JobChainParamQuerier.class)
    public boolean deleteHdfsFile(Long dtuicTenantId, Integer dataSourceCode, String pluginInfo, String deleteHdfsFile) {
        return true;
    }

    }
