package com.dtstack.engine.master.impl;

import com.dtstack.engine.dao.ScheduleFillDataJobDao;
import com.dtstack.engine.po.ScheduleFillDataJob;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.enums.FillGeneratStatusEnum;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class ScheduleFillDataJobService {

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleFillDataJobDao scheduleFillDataJobDao;

    public Long getByName(String jobName, Long projectId) {
        if (projectId == null) {
            return -1L;
        }
        ScheduleFillDataJob scheduleFillDataJob = scheduleFillDataJobDao.getByJobName(jobName, projectId);
        if (null == scheduleFillDataJob) {
            return -1L;
        }
        return scheduleFillDataJob.getId();
    }

    public List<ScheduleFillDataJob> getFillJobList(List<String> fillJobName, long projectId) {
        if (CollectionUtils.isEmpty(fillJobName)) {
            return Lists.newArrayList();
        }

        return scheduleFillDataJobDao.listFillJob(fillJobName, projectId);
    }

    @Transactional(rollbackFor = Exception.class)
    public ScheduleFillDataJob saveData(String jobName, Long tenantId, Long projectId, String runDay,
                                        String fromDay, String toDay, Long userId, Integer appType, Long dtuicTenantId) {

        Timestamp currTimeStamp = Timestamp.valueOf(LocalDateTime.now());

        ScheduleFillDataJob fillDataJob = new ScheduleFillDataJob();
        fillDataJob.setJobName(jobName);
        fillDataJob.setFromDay(fromDay);
        fillDataJob.setToDay(toDay);
        fillDataJob.setRunDay(runDay);
        fillDataJob.setTenantId(tenantId);
        fillDataJob.setProjectId(projectId);
        fillDataJob.setCreateUserId(userId);
        fillDataJob.setGmtModified(currTimeStamp);
        fillDataJob.setGmtCreate(currTimeStamp);
        fillDataJob.setAppType(appType);
        fillDataJob.setDtuicTenantId(dtuicTenantId);
        fillDataJob.setMaxParallelNum(0);
        fillDataJob.setNumberParallelNum(0);
        fillDataJob.setTaskRunOrder(0);
        fillDataJob.setFillDataInfo("");
        fillDataJob.setNodeAddress("");
        fillDataJob.setFillGeneratStatus(FillGeneratStatusEnum.DEFAULT_VALUE.getType());
        scheduleFillDataJobDao.insert(fillDataJob);
        return fillDataJob;
    }

    public List<ScheduleFillDataJob> getFillByFillIds(List<Long> fillIds) {
        if (CollectionUtils.isNotEmpty(fillIds)) {
            return scheduleFillDataJobDao.listFillJobByIds(fillIds);
        }
        return Lists.newArrayList();
    }

    public Integer incrementParallelNum(Long id) {
        if (id != null) {
            return scheduleFillDataJobDao.incrementParallelNum(id);
        }
        return 0;
    }

    public ScheduleFillDataJob getFillById(Long fillId) {
        if (fillId != null) {
            return scheduleFillDataJobDao.getById(fillId);
        }
        return null;
    }

    public Integer decrementParallelNum(Long id) {
        if (id != null) {
            return scheduleFillDataJobDao.decrementParallelNum(id);
        }
        return 0;
    }

    public List<ScheduleFillDataJob> getFillIdByJobIds(Set<String> jobIds) {
        if (CollectionUtils.isNotEmpty(jobIds)) {
            List<Long> fillIds = scheduleJobDao.getFillIdByJobIds(jobIds);

            if (CollectionUtils.isNotEmpty(fillIds)) {
                return scheduleFillDataJobDao.listFillJobByIds(fillIds);
            }
        }

        return Lists.newArrayList();
    }
}
