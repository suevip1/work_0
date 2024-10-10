package com.dtstack.engine.master.scheduler.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.Expired;
import com.dtstack.dtcenter.common.enums.Restarted;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.DependencyType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.event.SummitCheckEventType;
import com.dtstack.engine.master.scheduler.parser.ESchedulePeriodType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2023/2/27 1:42 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class JobTimeJobStatusSummitCheckInterceptor extends AbstractJobStatusSummitCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobTimeJobStatusSummitCheckInterceptor.class);

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Override
    public JobCheckRunInfo checkJobStatus(JobStatusSummitCheckContext checkContext) {
        ScheduleTaskShade batchTaskShade = checkContext.getScheduleTaskShade();
        JobCheckRunInfo jobCheckRunInfo = checkContext.getJobCheckRunInfo();
        ScheduleBatchJob scheduleBatchJob = checkContext.getScheduleBatchJob();

        Integer scheduleType = scheduleBatchJob.getScheduleType();
        //判断执行时间是否到达
        if (!EScheduleType.FILL_DATA.getType().equals(scheduleType)) {
            String currStr = sdf.format(new Date());
            long currVal = Long.parseLong(currStr);
            long triggerVal = Long.parseLong(scheduleBatchJob.getCycTime());
            if (currVal < triggerVal) {
                jobCheckRunInfo.setStatus(JobCheckStatus.TIME_NOT_REACH);
                return jobCheckRunInfo;
            }
        }

        JSONObject scheduleConf = JSONObject.parseObject(batchTaskShade.getScheduleConf());
        if(null == scheduleConf){
            return jobCheckRunInfo;
        }
        Integer isExpire = scheduleConf.getInteger("isExpire");
        if(null == isExpire){
            return jobCheckRunInfo;
        }

        //配置了允许过期才能
        if (Expired.EXPIRE.getVal() == isExpire
                && this.checkExpire(scheduleBatchJob, scheduleType, batchTaskShade)) {
            if (!this.validSelfWithExpire(scheduleBatchJob,jobCheckRunInfo)) {
                jobCheckRunInfo.setStatus(JobCheckStatus.TIME_OVER_EXPIRE);
            }
            return jobCheckRunInfo;
        }

        return jobCheckRunInfo;
    }

    private boolean checkExpire(ScheduleBatchJob scheduleBatchJob, Integer scheduleType, ScheduleTaskShade batchTaskShade) {
        //---正常周期任务超过当前时间则标记为过期
        //http://redmine.prod.dtstack.cn/issues/19917
        if (!EScheduleType.NORMAL_SCHEDULE.getType().equals(scheduleType)) {
            return false;
        }
        //分钟 小时任务 才有过期
        if (!batchTaskShade.getPeriodType().equals(ESchedulePeriodType.MIN.getVal())
                && !batchTaskShade.getPeriodType().equals(ESchedulePeriodType.HOUR.getVal())) {
            return false;
        }
        //重跑不走
        if (null != scheduleBatchJob.getScheduleJob() && Restarted.RESTARTED.getStatus() == scheduleBatchJob.getScheduleJob().getIsRestart()) {
            return false;
        }
        if (null != scheduleBatchJob.getScheduleJob() && !"0".equalsIgnoreCase(scheduleBatchJob.getScheduleJob().getFlowJobId())) {
            //工作流子任务不检验自动取消的逻辑
            return false;
        }
        //判断task任务是否配置了允许过期（暂时允许全部任务过期 不做判断）
        //超过时间限制
        String nextCycTime = scheduleBatchJob.getScheduleJob().getNextCycTime();
        if(StringUtils.isBlank(nextCycTime)){
            return false;
        }
        String scheduleConf = batchTaskShade.getScheduleConf();
        if(StringUtils.isBlank(scheduleConf)){
            return false;
        }
        LocalDateTime nextDateCycTime = LocalDateTime.parse(scheduleBatchJob.getScheduleJob().getNextCycTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        JSONObject jsonObject = JSON.parseObject(scheduleConf);
        Boolean isLastInstance = jsonObject.getBoolean("isLastInstance");
        if(null == isLastInstance){
            return nextDateCycTime.isBefore(LocalDateTime.now());
        }
        LocalDateTime cycDateTime = LocalDateTime.parse(scheduleBatchJob.getScheduleJob().getCycTime(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        if (isLastInstance) {
            // 判断当前实例是否是最后一个实例,且设置了执行最后一个任务
            if (nextDateCycTime.getDayOfMonth() != cycDateTime.getDayOfMonth()) {
                // cycTime 和 nextCycTime 是不是同一天 不是同一天 说明这个任务是今天执行的最后一个任务
                return false;
            }
            return nextDateCycTime.isBefore(LocalDateTime.now());
        } else {
            //延迟至第二天后自动取消
            if (nextDateCycTime.getDayOfMonth() == cycDateTime.getDayOfMonth()) {
                //不是当天最后一个任务
                return nextDateCycTime.isBefore(LocalDateTime.now());
            } else {
                //最后一个执行时间 20201105235800 nextCycTime为2020-11-06 23:48:00 当前时间2020-11-06 11:00:00 要过期
                return nextDateCycTime.getDayOfMonth() == LocalDateTime.now().getDayOfMonth();
            }
        }
    }

    /**
     * 自依赖任务开启自动取消 还需要保证并发数为1 所以需要判断当前是否有运行中的任务
     * @param scheduleBatchJob
     * @return
     */
    private Boolean validSelfWithExpire(ScheduleBatchJob scheduleBatchJob,JobCheckRunInfo checkRunInfo) {
        ScheduleJob scheduleJob = scheduleBatchJob.getScheduleJob();
        if (!DependencyType.SELF_DEPENDENCY_END.getType().equals(scheduleJob.getDependencyType()) &&
                !DependencyType.SELF_DEPENDENCY_SUCCESS.getType().equals(scheduleJob.getDependencyType())) {
            return Boolean.FALSE;
        }
        //查询当前自依赖任务 今天调度时间前是否有运行中或提交中的任务 如果有 需要等头部运行任务运行完成 才校验自动取消的逻辑
        String todayCycTime = DateTime.now().withTime(0, 0, 0, 0).toString("yyyyMMddHHmmss");
        List<Integer> checkStatus = new ArrayList<>(RdosTaskStatus.RUNNING_STATUS);
        checkStatus.addAll(RdosTaskStatus.WAIT_STATUS);
        checkStatus.addAll(RdosTaskStatus.SUBMITTING_STATUS);
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listIdByTaskIdAndStatus(scheduleJob.getTaskId(), checkStatus, scheduleJob.getAppType(), todayCycTime, EScheduleType.NORMAL_SCHEDULE.getType());
        if (CollectionUtils.isNotEmpty(scheduleJobs)) {
            ScheduleJob waitFinishJob = scheduleJobs.get(0);
            LOGGER.info("jobId {} selfJob {}  has running status [{}],wait running job finish", scheduleJob.getJobId(), waitFinishJob.getJobId(), waitFinishJob.getStatus());
            checkRunInfo.setStatus(JobCheckStatus.TIME_NOT_REACH);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    protected SummitCheckEventType getEventType() {
        return SummitCheckEventType.job_time_job_status_summit_check_interceptor_end_event;
    }
}
