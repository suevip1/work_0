package com.dtstack.engine.master.controller.sdk;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.dto.QueryJobDTO;
import com.dtstack.engine.api.dto.ScheduleSqlTextDTO;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.FillDataParams;
import com.dtstack.engine.api.param.RollingJobLogParam;
import com.dtstack.engine.api.param.ScheduleJobAuthParam;
import com.dtstack.engine.api.vo.JobStatusVo;
import com.dtstack.engine.api.vo.JobTopOrderVO;
import com.dtstack.engine.api.vo.RestartJobInfoVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobDetailVO;
import com.dtstack.engine.api.vo.ScheduleJobKillJobVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.api.vo.TaskExeInfoVo;
import com.dtstack.engine.api.vo.diagnosis.JobDiagnosisInformationVO;
import com.dtstack.engine.api.vo.job.BlockJobNumVO;
import com.dtstack.engine.api.vo.job.BlockJobVO;
import com.dtstack.engine.api.vo.job.JobExecInfoVO;
import com.dtstack.engine.api.vo.job.JobExpandVO;
import com.dtstack.engine.api.vo.job.JobGraphBuildJudgeVO;
import com.dtstack.engine.api.vo.job.JobRunCountVO;
import com.dtstack.engine.api.vo.job.JobStopProcessVO;
import com.dtstack.engine.api.vo.job.MaxCpuMemVO;
import com.dtstack.engine.api.vo.job.ScheduleJobAuthVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobTopRunTimeVO;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.diagnosis.JobDiagnosisChain;
import com.dtstack.engine.master.enums.RestartType;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.impl.ScheduleJobAuthService;
import com.dtstack.engine.master.impl.ScheduleJobExpandService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.mapstruct.ScheduleJobStruct;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.engine.po.ScheduleJobExpand;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/11/17 下午3:27
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/sdk/scheduleJob")
@Api(value = "/node/sdk/scheduleJob", tags = {"任务实例接口"})
public class ScheduleJobSdkController {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobStruct scheduleJobStruct;

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private JobParamReplace jobParamReplace;

    @Autowired
    private JobDiagnosisChain jobDiagnosisChain;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ScheduleJobAuthService scheduleJobAuthService;

    @Autowired
    private ScheduleJobExpandService scheduleJobExpandService;

    @RequestMapping(value = "/restartJob", method = {RequestMethod.POST, RequestMethod.GET})
    public boolean restartJob(@RequestParam("jobIds") List<String> jobIds,
                              @RequestParam("restartType") Integer restartType,
                              @RequestParam("operateId") Long operateId) {
        RestartType byCode = RestartType.getByCode(restartType);

        if (byCode == null) {
            throw new RdosDefineException("请选择正确的重跑模式");
        }

        return scheduleJobService.restartJob(byCode, jobIds, operateId);
    }

    @RequestMapping(value = "/restartJobInfo", method = {RequestMethod.POST})
    @ApiOperation(value = "获取重跑的数据节点信息")
    public List<RestartJobInfoVO> restartJobInfo(@RequestParam("jobKey") String jobKey, @RequestParam("taskId") Long parentTaskId) {
        return scheduleJobService.restartJobInfo(jobKey, parentTaskId, null);
    }

    @RequestMapping(value = "/queryTopTenJob", method = {RequestMethod.POST})
    @ApiOperation(value = "运行时长topN排序")
    public List<JobTopOrderVO> queryTopTenJob(@RequestBody @Valid ScheduleJobTopRunTimeVO topRunTimeVO) {
        return scheduleJobService.queryTopSizeRunTimeJob(topRunTimeVO);
    }

    @PostMapping(value = "/listByJobIds")
    public List<ScheduleJobVO> listByJobIds(@RequestParam("jobIds") List<String> jobIds) {
        if (CollectionUtils.isEmpty(jobIds)) {
            return new ArrayList<>();
        }
        if (jobIds.size() > environmentContext.getMaxBatchSize()) {
            jobIds = jobIds.subList(0, environmentContext.getMaxBatchSize());
        }
        List<ScheduleJob> scheduleJobs = scheduleJobService.listByJobIds(jobIds);
        return scheduleJobStruct.toVos(scheduleJobs);
    }


    @RequestMapping(value = "/queryJobsStatusStatistics", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务的状态统计信息")
    public JobStatusVo queryJobsStatusStatistics(@RequestBody QueryJobDTO vo) {
        return scheduleJobService.queryJobsStatusStatisticsToVo(vo);
    }

    @RequestMapping(value = "/statisticsTaskRecentInfo", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务执行信息")
    public List<TaskExeInfoVo> statisticsTaskRecentInfo(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType, @RequestParam("projectId") Long projectId, @RequestParam("count") Integer count) {
        return scheduleJobService.statisticsTaskRecentInfoToVo(taskId, appType, projectId, count,
                Lists.newArrayList(EScheduleType.NORMAL_SCHEDULE.getType(), EScheduleType.FILL_DATA.getType()));
    }


    @PostMapping(value = "/stopJobByConditionTotal")
    public Integer stopJobByConditionTotal(@RequestBody @Valid ScheduleJobKillJobVO vo) {
        return scheduleJobService.stopJobByConditionTotal(vo);
    }

    @RequestMapping(value = "/querySqlText", method = {RequestMethod.POST, RequestMethod.GET})
    public List<ScheduleSqlTextDTO> querySqlText(@RequestParam("jobIds") List<String> jobIds) {
        return scheduleJobService.querySqlText(jobIds);
    }


    @PostMapping(value = "/getJobGetFillDataDetailInfo")
    public PageResult<ScheduleFillDataJobDetailVO> getJobGetFillDataDetailInfo(@RequestBody FillDataParams fillDataParams) throws Exception {
        return scheduleJobService.getJobGetFillDataDetailInfo(fillDataParams.getTaskName(),
                fillDataParams.getBizStartDay(), fillDataParams.getBizEndDay(), fillDataParams.getFlowJobIdList(), fillDataParams.getFillJobName(),
                fillDataParams.getDutyUserId(), fillDataParams.getSearchType(), fillDataParams.getAppType(),
                fillDataParams.getProjectId(), fillDataParams.getDtuicTenantId(), fillDataParams.getExecTimeSort(), fillDataParams.getExecStartSort(),
                fillDataParams.getExecEndSort(), fillDataParams.getCycSort(), fillDataParams.getBusinessDateSort(), fillDataParams.getRetryNumSort(),
                fillDataParams.getTaskType(), fillDataParams.getJobStatuses(), fillDataParams.getCurrentPage(), fillDataParams.getPageSize());
    }

    @ApiOperation(value = "获取任务执行日志")
    @PostMapping(value = "/getRunSqlText")
    public String getRunSqlText(@RequestParam("jobId") String jobId, @RequestParam("runNum") Integer runNum) throws Exception {
        String runSqlText = scheduleJobExpandDao.getRunSqlText(jobId,runNum);
        //提交后才有runSqlText
        if (StringUtils.isNotBlank(runSqlText)) {
            return runSqlText;
        }
        ScheduleJob job = scheduleJobService.getByJobId(jobId, Deleted.NORMAL.getStatus());
        if (null == job) {
            return "";
        }
        String info = scheduleTaskShadeDao.getExtInfoByTaskIdIncludeDelete(job.getTaskId(), job.getAppType());

        if (StringUtils.isBlank(info)) {
            return "";
        }

        Map<String, Object> actionParam = PublicUtil.strToMap(info);
        Object sqlObj = actionParam.get(GlobalConst.sqlText);
        String sql = JSONObject.toJSONString(sqlObj);
        if (StringUtils.isBlank(sql)) {
            return sql;
        }

        List<ScheduleTaskParamShade> totalTaskParamShade = new ArrayList<>();

        actionService.fillConsoleParam(actionParam, job.getTaskId(), job.getAppType(), jobId);
        // paramService.convertGlobalToParamType(JSONObject.toJSONString(actionParam), (globalTaskParams, selfTaskParams) -> {
        //     List<ScheduleTaskParamShade> globalTaskShades = paramStruct.BOtoTaskParams(globalTaskParams);
        //     totalTaskParamShade.addAll(globalTaskShades);
        //     totalTaskParamShade.addAll(selfTaskParams);
        // });

        //统一替换下sql
        sql = jobParamReplace.paramReplace(sql, totalTaskParamShade, job.getCycTime(), job.getType(), job.getProjectId());
        return sql;
    }

    @PostMapping(value = "/getRunSubTaskId")
    public List<Long> getRunSubTaskId(@RequestParam("jobId") String jobId) {
        return scheduleJobService.getRunSubTaskId(jobId);
    }

    @PostMapping(value = "/canBuildJobGraphToday")
    public JobGraphBuildJudgeVO canBuildJobGraphToday(@RequestBody ScheduleTaskShadeDTO scheduleTaskShadeDTO) {
        return scheduleJobService.canBuildJobGraphToday(scheduleTaskShadeDTO);
    }


    @PostMapping(value = "/listJobByJobKeys")
    public List<ScheduleJobVO> listJobByJobKeys(@RequestParam("jobKeys") List<String> keys) {
        List<ScheduleJob> scheduleJobs = scheduleJobService.listJobByJobKeys(keys);
        return scheduleJobStruct.toVos(scheduleJobs);
    }


    @PostMapping(value = "/diagnosis")
    public List<JobDiagnosisInformationVO> diagnosis(@RequestParam("jobId") String jobId) throws Exception {
        ScheduleJobExpand jobExpand = scheduleJobExpandDao.getExpandByJobId(jobId);
        if (null == jobExpand) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        return diagnosisByRunNum(jobId, jobExpand.getRunNum());
    }

    @PostMapping(value = "/listJobExpandByJobId")
    public List<JobExpandVO> listJobExpandByJobId(@RequestParam("jobId") String jobId) throws Exception {

        List<ScheduleJobExpand> expandByJobIds = scheduleJobExpandDao.getExpandByJobIds(Lists.newArrayList(jobId));
        return scheduleJobStruct.toJobExpandVOs(expandByJobIds) ;
    }

    @PostMapping(value = "/diagnosisByRunNum")
    public List<JobDiagnosisInformationVO> diagnosisByRunNum(@RequestParam("jobId") String jobId, @RequestParam("runNum") Integer runNum) throws Exception {
        return jobDiagnosisChain.diagnosisByRunNum(jobId, runNum);
    }

    /**
     * 迁移到接口 {@link ScheduleJobSdkController#getRunningTaskLogUrlV2}
     * @param jobId
     * @return
     */
    @Deprecated
    @PostMapping(value = "/getRunningTaskLogUrl")
    @ApiOperation(value = "实时获取离线运行任务的日志URL")
    public List<String> getRunningTaskLogUrl(@RequestParam("jobId") String jobId) {
        RollingJobLogParam rollingJobLogParam = new RollingJobLogParam();
        rollingJobLogParam.setJobId(jobId);
        return scheduleJobService.getRunningTaskLogUrl(rollingJobLogParam);
    }

    @PostMapping(value = "/getRunningTaskLogUrlV2")
    @ApiOperation(value = "获取离线运行任务的日志URL")
    public List<String> getRunningTaskLogUrlV2(@RequestBody RollingJobLogParam rollingJobLogParam) {
        return scheduleJobService.getRunningTaskLogUrl(rollingJobLogParam);
    }

    @PostMapping(value = "/getJobExecInfo")
    @ApiOperation(value = "实时获取离线运行任务运行报告")
    public JobExecInfoVO getJobExecInfo(@RequestParam("jobId") String jobId) {
        return scheduleJobService.getJobExecInfo(jobId);
    }

    @PostMapping(value = "/getJobExtraInfoOfValue")
    @ApiOperation(value = "获取JobExtra参数")
    public String getJobExtraInfoOfValue(String jobId, String key) {
        return scheduleJobService.getJobExtraInfoOfValue(jobId,key);
    }


    @PostMapping(value = "/getStopProgress")
    @ApiOperation(value = "获取任务停止进展")
    public List<JobStopProcessVO> getStopProgress(@RequestParam("jobIds") List<String> jobIds) {
        if (CollectionUtils.isEmpty(jobIds)) {
            return new ArrayList<>();
        }
        return scheduleJobService.listStopProcess(jobIds);
    }

    @PostMapping(value = "/getJobExtraInfoOfValueById")
    @ApiOperation(value = "获取JobExtra参数")
    public String getJobExtraInfoOfValueById(Long id, String key) {
        return scheduleJobService.getJobExtraInfoOfValueById(id,key);
    }

    @PostMapping(value = "/runNumList")
    @ApiOperation(value = "获得实例执行次数列表")
    public List<JobRunCountVO> runNumList(String jobId) {
        return scheduleJobService.runNumList(jobId);
    }

    @PostMapping(value = "/blockJobList")
    @ApiOperation(value = "获得实例实际被阻塞的任务")
    public List<BlockJobVO> blockJobList(String jobId){
        return scheduleJobService.blockJobList(jobId);
    }

    @PostMapping(value = "/blockJobNum")
    @ApiOperation(value = "获得实例实际被阻塞的任务")
    public BlockJobNumVO blockJobNum(String jobId){
        // 需求临时砍掉，所以暂时不走逻辑
        return new BlockJobNumVO();
    }


    /**
     * 统计近7天的资源使用情况
     * @param appType
     * @param projectIds
     * @return
     */
    @PostMapping(value = "/getSevenDayResources")
    @ApiOperation(value = "统计近7天的资源使用情况")
    public List<MaxCpuMemVO> getSevenDayResources(@RequestParam("appType") Integer appType,@RequestParam("projectIds") List<Long> projectIds){

        Assert.notNull(appType,"appType can not be null");
        Assert.notEmpty(projectIds,"projectIds can not be null");
        return scheduleJobExpandService.getSevenDayResources(appType,projectIds);

    }
    @PostMapping(value = "/getJobAuth")
    @ApiOperation(value = "获得实例执行使用的认证信息")
    public ScheduleJobAuthVO getJobAuth(@RequestBody ScheduleJobAuthParam scheduleJobAuthParam) {
        if (StringUtils.isBlank(scheduleJobAuthParam.getJobId())) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        return scheduleJobAuthService.getJobAuth(scheduleJobAuthParam);
    }
}
