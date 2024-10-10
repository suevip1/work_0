package com.dtstack.engine.master.impl;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.dao.ScheduleJobHistoryDao;
import com.dtstack.engine.po.ScheduleJobHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author yuebai
 * @date 2021-09-10
 */
@Component
public class ScheduleJobHistoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobHistoryService.class);

    @Autowired
    private ScheduleJobHistoryDao jobHistoryDao;

    /**
     * 插入实例运行历史, 只记录实时计算平台任务 和离线数据同步任务
     *
     * @param jobClient     jobClient
     * @param applicationId yarn appId
     */
    public void insertScheduleJobHistory(JobClient jobClient, String applicationId) {
        // 只有流计算记录
        if (null == jobClient) {
            return;
        }
        if (AppType.STREAM.getType().equals(jobClient.getAppType())
                || (AppType.RDOS.getType().equals(jobClient.getAppType()) && EScheduleJobType.SYNC.getType().equals(jobClient.getTaskType()))) {
            try {
                ScheduleJobHistory scheduleJobHistory = new ScheduleJobHistory();
                scheduleJobHistory.setJobId(jobClient.getTaskId());
                scheduleJobHistory.setAppType(jobClient.getAppType());
                scheduleJobHistory.setApplicationId(applicationId);
                scheduleJobHistory.setExecStartTime(new Date());
                scheduleJobHistory.setEngineJobId(jobClient.getEngineTaskId());
                scheduleJobHistory.setVersionName(jobClient.getComponentVersion());
                jobHistoryDao.insert(scheduleJobHistory);
            } catch (Exception e) {
                LOGGER.error("save jobClient {} history error", jobClient, e);
            }
        }
    }


    /**
     * 更新实时计算任务状态结束时间
     *
     * @param applicationId yarn appId
     * @param appType       应用类型
     */
    public void updateScheduleJobHistoryTime(String applicationId, Integer appType) {
        if (!AppType.STREAM.getType().equals(appType)) {
            return;
        }
        try {
            jobHistoryDao.updateByApplicationId(applicationId, appType);
        } catch (Exception e) {
            LOGGER.error("update applicationId {} history end time", applicationId, e);
        }
    }

    /**
     * 分页获取实时计算任务运行历史
     *
     * @param jobId    任务 id
     * @param pageSize 每页大小
     * @param pageNo   当前页码
     * @return 任务运行历史
     */
    public PageResult<List<ScheduleJobHistory>> pageByJobId(String jobId, Integer pageSize, Integer pageNo) {
        PageQuery<ScheduleJobHistory> pageQuery = new PageQuery<>();
        pageQuery.setPage(pageNo);
        pageQuery.setPageSize(pageSize);
        ScheduleJobHistory scheduleJobHistory = new ScheduleJobHistory();
        scheduleJobHistory.setJobId(jobId);
        pageQuery.setModel(scheduleJobHistory);
        List<ScheduleJobHistory> scheduleJobHistories = jobHistoryDao.selectByJobId(pageQuery);
        Integer total = jobHistoryDao.selectJobIdByCount(pageQuery);
        return new PageResult(scheduleJobHistories, total, pageQuery);
    }


    /**
     * 根据jobIds 逻辑删除和applicationId的关联关系
     *
     * @param jobIds
     * @param appType
     */
    public void deleteByJobIds(List<String> jobIds, Integer appType) {
        if (CollectionUtils.isEmpty(jobIds) || !AppType.STREAM.getType().equals(appType)) {
            return;
        }
        jobHistoryDao.deleteByJobIds(jobIds, appType);
    }

    public void deleteByJobIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        jobHistoryDao.deleteByIds(ids);
    }

    public List<ScheduleJobHistory> listJobHistoriesByStartId(long startId, int limit, Timestamp timestamp) {
        return jobHistoryDao.listJobHistoriesByStartId(startId, limit, timestamp);
    }

    public List<String> getJobIdsByAppType(Integer appType){
        return jobHistoryDao.getJobIdsByAppType(appType);
    }

}
