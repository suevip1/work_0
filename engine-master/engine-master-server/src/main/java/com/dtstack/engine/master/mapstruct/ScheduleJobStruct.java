package com.dtstack.engine.master.mapstruct;

import com.dtstack.dtcenter.loader.dto.JobExecInfo;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import com.dtstack.engine.api.vo.job.JobExecInfoVO;
import com.dtstack.engine.api.vo.job.JobExpandVO;
import com.dtstack.engine.api.vo.job.JobRunCountVO;
import com.dtstack.engine.api.vo.job.JobViewElementVO;
import com.dtstack.engine.api.vo.job.JobViewSideVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataJobListVO;
import com.dtstack.engine.api.vo.stream.ScheduleStreamJobVO;
import com.dtstack.engine.dto.FillDataQueryDTO;
import com.dtstack.engine.dto.ScheduleJobJobTaskDTO;
import com.dtstack.engine.po.ScheduleJobExpand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author yuebai
 * @date 2021-09-07
 */
@Mapper(componentModel = "spring",imports = com.dtstack.engine.common.enums.RdosTaskStatus.class)
public interface ScheduleJobStruct {

    List<ActionJobEntityVO> toActionJobEntityVO(List<ScheduleJob> job);

    FillDataQueryDTO toFillDataQueryDTO(FillDataJobListVO vo);

    @Mapping(source = "scheduleJob.jobId", target = "jobId")
    @Mapping(expression = "java(RdosTaskStatus.getShowStatus(scheduleJob.getStatus()))", target = "status")
    @Mapping(source = "scheduleJob.execStartTime", target = "execStartTime")
    @Mapping(source = "scheduleJob.execEndTime", target = "execEndTime")
    @Mapping(source = "scheduleJob.gmtCreate", target = "gmtCreate")
    @Mapping(source = "taskShade.businessType", target = "businessType")
    @Mapping(source = "taskShade.taskId", target = "taskId")
    @Mapping(source = "taskShade.name", target = "name")
    @Mapping(source = "taskShade.dtuicTenantId", target = "dtuicTenantId")
    @Mapping(source = "taskShade.taskType", target = "taskType")
    @Mapping(source = "taskShade.componentVersion", target = "componentVersion")
    @Mapping(source = "taskShade.gmtModified", target = "submitTime")
    @Mapping(source = "scheduleJob.gmtModified", target = "operatorTime")
    @Mapping(source = "taskShade.modifyUserId", target = "modifyUserId")
    @Mapping(source = "taskShade.ownerUserId", target = "ownerUserId")
    @Mapping(source = "ownerUser.userName", target = "ownerUserName")
    @Mapping(source = "modifyUser.userName", target = "modifyUserName")
    ScheduleStreamJobVO toStreamJob(ScheduleJob scheduleJob, ScheduleTaskShade taskShade, User ownerUser, User modifyUser);

    List<ScheduleJobVO> toVos(List<ScheduleJob> scheduleJobs);

    JobViewElementVO toJobViewElementVO(ScheduleJob scheduleJob);

    List<JobViewSideVO> scheduleJobJobTaskDTOToJobViewSideVO(List<ScheduleJobJobTaskDTO> scheduleJobJobs);

    List<JobViewSideVO> toJobViewSideVO(List<ScheduleJobJob> scheduleJobJobs);

    List<JobRunCountVO> toJobRunCountVO(List<ScheduleJobExpand> expands);

    JobExecInfoVO toJobExecInfoVO(JobExecInfo jobExecInfo);

    @Mapping(expression = "java(com.alibaba.fastjson.JSONObject.parseObject(jobExpand.getJobExtraInfo()).getIntValue(\"coreNum\"))", target = "coreNum")
    @Mapping(expression = "java(com.alibaba.fastjson.JSONObject.parseObject(jobExpand.getJobExtraInfo()).getIntValue(\"memNum\"))", target = "memNum")
    JobExpandVO toJobExpandVO(ScheduleJobExpand jobExpand);

    List<JobExpandVO> toJobExpandVOs(List<ScheduleJobExpand> jobExpands);
}
