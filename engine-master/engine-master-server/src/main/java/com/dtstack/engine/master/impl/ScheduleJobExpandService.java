package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.po.ScheduleJobExpand;
import com.dtstack.engine.api.vo.job.MaxCpuMemVO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.resource.JobResourceMonitor;
import com.dtstack.engine.po.ScheduleJobExpand;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UserDTO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UserVo;
import com.dtstack.sdk.core.common.ApiResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-08-26 16:00
 */
@Service
public class ScheduleJobExpandService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleJobExpandService.class);

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    @Autowired
    private UicUserApiClient uicUserApiClient;

    @Autowired
    private JobResourceMonitor jobResourceMonitor;

    @Async("jobLogExecutor")
    public void asyncAddKillLog(List<String> jobIds, String userName, Date dateTime,Integer status) {
        addKillLog(jobIds, userName, dateTime,status);
    }

    @Async("jobLogExecutor")
    public void asyncAddKillLog(List<String> jobIds, Long userId, Date dateTime,Integer status) {
        if (CollectionUtils.isEmpty(jobIds)) {
            return;
        }
        if (userId == null || userId.equals(-1L)) {
            return;
        }
        if (dateTime == null) {
            dateTime = new Date();
        }

        UserVo userVo = new UserVo();
        userVo.setId(userId);
        ApiResponse<UserDTO> user = null;
        try {
            user = uicUserApiClient.findById(userVo);
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
        }
        String killOperatorName = StringUtils.EMPTY;
        if (user != null && user.getData() != null) {
            killOperatorName = Objects.toString(user.getData().getUsername(), StringUtils.EMPTY);
        }
        if (StringUtils.isEmpty(killOperatorName)) {
            LOGGER.info("asyncAddKillLog, killOperatorName is empty, userId:{}", userId);
            return;
        }
        addKillLog(jobIds, killOperatorName, dateTime,status);
    }

    public void addKillLog(List<String> jobIds, String userName, Date dateTime,Integer status) {
        if (CollectionUtils.isEmpty(jobIds)) {
            return;
        }
        if (StringUtils.isEmpty(userName)) {
            return;
        }
        if (dateTime == null) {
            dateTime = new Date();
        }

        StringBuilder logInfo = new StringBuilder();
        logInfo.append("########").append(System.lineSeparator());
        logInfo.append("杀任务操作人:").append(userName).append(System.lineSeparator());
        logInfo.append("杀任务发起时间:").append(DateUtil.timestampToString(dateTime)).append(System.lineSeparator());
        logInfo.append("########").append(System.lineSeparator());
        for (String jobId : jobIds) {
            addLogInfo(jobId, logInfo.toString(), null,status);
        }
    }

    /**
     * 追加日志信息
     * @param jobId
     * @param prependInfo 前置追加
     * @param appendInfo 后置追加
     * @return
     */
    public void addLogInfo(String jobId, String prependInfo, String appendInfo,Integer status) {
        if (StringUtils.isEmpty(jobId)) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        if (StringUtils.isBlank(prependInfo) && StringUtils.isBlank(appendInfo)) {
            return;
        }
        ScheduleJobExpand logByJobId = scheduleJobExpandDao.getLogByJobId(jobId);
        if (logByJobId == null) {
            return;
        }

        String logInfoInDb = logByJobId.getLogInfo();
        JSONObject logJson;
        prependInfo = Objects.toString(prependInfo, StringUtils.EMPTY);
        appendInfo = Objects.toString(appendInfo, StringUtils.EMPTY);

        // 空对象，直接填入日志信息
        if (StringUtils.isBlank(logInfoInDb)) {
            logJson = new JSONObject();
            logJson.put(JobResult.JOB_ID_KEY, jobId);
            logJson.put(JobResult.MSG_INFO, prependInfo.concat(appendInfo));
            scheduleJobExpandDao.updateLogInfoByJobId(jobId, logJson.toJSONString(),status);
            return;
        }

        // 符合约定的 json 字符串，直接转为 json 对象
        if (logInfoInDb.startsWith("{") && logInfoInDb.endsWith("}")) {
            logJson = JSONObject.parseObject(logInfoInDb);
            String msgInfo = Objects.toString(logJson.getString(JobResult.MSG_INFO), StringUtils.EMPTY);
            StringBuilder sb = new StringBuilder(prependInfo);
            sb.append(System.lineSeparator())
                    .append(msgInfo)
                    .append(System.lineSeparator())
                    .append(appendInfo);
            logJson.put(JobResult.MSG_INFO, sb.toString());
            scheduleJobExpandDao.updateLogInfoByJobId(jobId, logJson.toJSONString(),status);
            return;
        }

        // 纯文本，直接追加日志
        StringBuilder sb = new StringBuilder(prependInfo);
        sb.append(System.lineSeparator())
                .append(logInfoInDb)
                .append(System.lineSeparator())
                .append(appendInfo);
        scheduleJobExpandDao.updateLogInfoByJobId(jobId, sb.toString(),status);
    }

    public Integer updateTaskStatusNotStopped(String jobId, Integer status, List<Integer> stoppedStatus) {
        return scheduleJobExpandDao.updateTaskStatusNotStopped(jobId,status,stoppedStatus);
    }

    public Integer updateJobStatusAndExecTime(String jobId, Integer status) {
        return scheduleJobExpandDao.updateJobStatusAndExecTime(jobId,status);
    }


    /**
     * 统计近7天的资源使用情况
     * @param appType
     * @param projectIds
     * @return
     */
    public List<MaxCpuMemVO> getSevenDayResources(Integer appType, List<Long> projectIds) {
        List<MaxCpuMemVO> sevenDayResources = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            MaxCpuMemVO maxCpuMemVO = new MaxCpuMemVO();
            String time = DateTime.now().plusDays(-i).toString(DateUtil.DATE_FORMAT);
            maxCpuMemVO.setDate(time);
            maxCpuMemVO.setMaxMemNum(projectIds.stream()
                    .map(p -> jobResourceMonitor.refreshTotalValue(time, JobResourceMonitor.MEM_REDIS_PRE, appType, p, Integer.MIN_VALUE))
                    .max(Integer::compareTo)
                    .orElse(0));
            maxCpuMemVO.setMaxCoreNum(projectIds.stream()
                    .map(p -> jobResourceMonitor.refreshTotalValue(time, JobResourceMonitor.CORE_REDIS_PRE, appType, p, Integer.MIN_VALUE))
                    .max(Integer::compareTo)
                    .orElse(0));
            sevenDayResources.add(maxCpuMemVO);
        }
        return sevenDayResources;
    }
}