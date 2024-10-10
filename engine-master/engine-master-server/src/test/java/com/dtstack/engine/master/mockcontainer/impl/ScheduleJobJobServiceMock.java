package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.enums.RelyTypeEnum;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.dto.ScheduleJobJobTaskDTO;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.impl.UserService;
import com.dtstack.engine.master.impl.WorkSpaceProjectService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.scheduler.parser.ESchedulePeriodType;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import org.apache.ibatis.annotations.Param;
import org.assertj.core.util.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ScheduleJobJobServiceMock extends BaseMock {

    @MockInvoke(targetClass = ScheduleJobJobDao.class)
    public List<ScheduleJobJob> listByJobKeys(@Param("list") Collection<String> jobKeys, @Param("relyType") Integer relyType) {
        return new ArrayList<>();
    }


    @MockInvoke(targetClass = ScheduleJobService.class)
    public Long getTaskShadeIdFromJobKey(String jobKey) {
        return 100L;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public String getTaskNameByJobKey(String jobKey, Integer appType) {
        return "";
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<String> getTaskNameByJobKeys(List<String> relyJobParentKeys) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShade> listByIdsAndTaskName(List<Long> ids, String taskName) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setTaskId(200L);
        scheduleTaskShade.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public ScheduleTaskShade getBatchTaskById(Long taskId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setTaskId(200L);
        scheduleTaskShade.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        return scheduleTaskShade;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getByJobKey(String jobKey) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setFlowJobId("0");
        scheduleJob.setId(100L);
        scheduleJob.setJobKey("cronTrigger_25125_20220426000000");
        scheduleJob.setJobId("test");
        scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleJob.setTaskId(1L);
        scheduleJob.setCycTime("20220426000000");
        scheduleJob.setPeriodType(ESchedulePeriodType.HOUR.getVal());
        scheduleJob.setBusinessDate("20220514");
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setFlowJobId("0");
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
        return scheduleJob;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getByJobId(String jobId, Integer isDeleted) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setFlowJobId("0");
        scheduleJob.setId(100L);
        scheduleJob.setJobKey("cronTrigger_25125_20220426000000");
        scheduleJob.setJobId("test");
        scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleJob.setTaskId(1L);
        scheduleJob.setCycTime("20220426000000");
        scheduleJob.setPeriodType(ESchedulePeriodType.HOUR.getVal());
        scheduleJob.setBusinessDate("20220514");
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setFlowJobId("0");
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
        return scheduleJob;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public ScheduleJob getJobById(long jobId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setFlowJobId("0");
        scheduleJob.setId(100L);
        scheduleJob.setJobKey("cronTrigger_25125_20220426000000");
        scheduleJob.setJobId("test");
        scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleJob.setTaskId(1L);
        scheduleJob.setCycTime("20220426000000");
        scheduleJob.setPeriodType(ESchedulePeriodType.HOUR.getVal());
        scheduleJob.setBusinessDate("20220514");
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setFlowJobId("0");
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
        return scheduleJob;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    public ScheduleJob getOne(Long jobId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setFlowJobId("0");
        scheduleJob.setId(100L);
        scheduleJob.setJobKey("cronTrigger_25125_20220426000000");
        scheduleJob.setJobId("test");
        scheduleJob.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        scheduleJob.setTaskId(1L);
        scheduleJob.setCycTime("20220426000000");
        scheduleJob.setPeriodType(ESchedulePeriodType.HOUR.getVal());
        scheduleJob.setBusinessDate("20220514");
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setFlowJobId("0");
        return scheduleJob;
    }

    @MockInvoke(targetClass = ScheduleJobJobDao.class)
    List<ScheduleJobJob> listByJobKey(String jobKey, Integer relyType) {
        ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
        scheduleJobJob.setJobKey("cronTrigger_25125_20220426000000");
        scheduleJobJob.setParentJobKey("cronTrigger_25123_20220426000000");
        scheduleJobJob.setRelyType(RelyTypeEnum.NORMAL.getType());
        return Lists.newArrayList(scheduleJobJob);
    }

    @MockInvoke(targetClass = ScheduleJobJobDao.class)
    List<ScheduleJobJob> listByJobKeys(Collection<String> jobKeys) {
        ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
        scheduleJobJob.setJobKey("cronTrigger_25125_20220426000000");
        scheduleJobJob.setParentJobKey("cronTrigger_25123_20220426000000");
        scheduleJobJob.setRelyType(RelyTypeEnum.NORMAL.getType());
        return Lists.newArrayList(scheduleJobJob);
    }

    @MockInvoke(targetClass = ScheduleJobJobDao.class)
    List<ScheduleJobJobTaskDTO> listByParentJobKeysWithOutSelfTask(List<String> jobKeyList) {
        ScheduleJobJobTaskDTO scheduleJobJobTaskDTO = new ScheduleJobJobTaskDTO();
        scheduleJobJobTaskDTO.setTaskId(100L);
        scheduleJobJobTaskDTO.setAppType(1);
        scheduleJobJobTaskDTO.setJobKey("cronTrigger_25125_20220426000000");
        scheduleJobJobTaskDTO.setParentJobKey("cronTrigger_25123_20220426000000");
        scheduleJobJobTaskDTO.setRelyType(RelyTypeEnum.NORMAL.getType());
        return Lists.newArrayList(scheduleJobJobTaskDTO);
    }

    @MockInvoke(targetClass = ScheduleJobJobDao.class)
    List<ScheduleJobJobTaskDTO> listByJobKeysWithOutSelfTask(List<String> jobKeys) {
        ScheduleJobJobTaskDTO scheduleJobJobTaskDTO = new ScheduleJobJobTaskDTO();
        scheduleJobJobTaskDTO.setTaskId(100L);
        scheduleJobJobTaskDTO.setAppType(1);
        scheduleJobJobTaskDTO.setJobKey("cronTrigger_25125_20220426000000");
        scheduleJobJobTaskDTO.setParentJobKey("cronTrigger_25123_20220426000000");
        scheduleJobJobTaskDTO.setRelyType(RelyTypeEnum.NORMAL.getType());
        return Lists.newArrayList(scheduleJobJobTaskDTO);
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> listJobByJobKeys(Collection<String> jobKeys) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setId(100L);
        scheduleJob.setJobKey("cronTrigger_25125_20220426000000");
        scheduleJob.setJobId("test");
        scheduleJob.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        scheduleJob.setTaskId(100L);
        scheduleJob.setCycTime("20220426000000");
        scheduleJob.setPeriodType(ESchedulePeriodType.HOUR.getVal());
        scheduleJob.setBusinessDate("20220514");
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setAppType(1);
        scheduleJob.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
        scheduleJob.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJob.setFlowJobId("0");
        return Lists.newArrayList(scheduleJob);
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShade> getSimpleTaskRangeAllByIds(List<Long> taskIdArray, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setTaskId(100L);
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = WorkSpaceProjectService.class)
    public AuthProjectVO finProject(Long projectId, Integer appType) {
        AuthProjectVO authProjectVO = new AuthProjectVO();
        authProjectVO.setProjectName("test");
        return authProjectVO;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShade> findChildTaskRuleByTaskId(Long taskId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setTaskId(100L);
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = UserService.class)
    public void fillUser(List<ScheduleTaskVO> vos) {
        return;
    }
}