package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.BaseEntity;
import com.dtstack.engine.po.ScheduleJobBuildRecord;
import com.dtstack.engine.dto.ScheduleJobBuildRecordQuery;
import com.dtstack.engine.api.enums.JobBuildStatus;
import com.dtstack.engine.api.enums.JobBuildType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.dao.ScheduleJobBuildRecordDao;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 实例构建记录 service
 *
 * @author ：wangchuan
 * date：Created in 16:45 2022/8/31
 * company: www.dtstack.com
 */
@Service
public class ScheduleJobBuildRecordService {

    @Resource
    private ScheduleJobBuildRecordDao scheduleJobBuildRecordDao;

    /**
     * 插入一条记录
     *
     * @param scheduleJobBuildRecord 任务实例构建记录
     */
    public void insert(ScheduleJobBuildRecord scheduleJobBuildRecord) {
        if (Objects.isNull(scheduleJobBuildRecord)) {
            return;
        }
        scheduleJobBuildRecordDao.insert(scheduleJobBuildRecord);
    }

    /**
     * 获取当天正常调度、立即生成实例但还未进行构建的记录, 按创建时间排序
     *
     * @return 实例记录
     */
    public List<ScheduleJobBuildRecord> getRecordNotBuildForNormalImmediately() {
        // 构建查询条件
        ScheduleJobBuildRecordQuery recordQuery = new ScheduleJobBuildRecordQuery();
        recordQuery.setJobBuildStatusList(Lists.newArrayList(JobBuildStatus.CREATE.getStatus()));
        recordQuery.setJobBuildTypeList(Lists.newArrayList(JobBuildType.IMMEDIATELY.getType()));
        recordQuery.setScheduleTypeList(Lists.newArrayList(EScheduleType.NORMAL_SCHEDULE.getType()));
        // 当天 0 点
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        recordQuery.setGreaterTime(new Timestamp(calendar.getTime().getTime()));
        return scheduleJobBuildRecordDao.getByCondition(recordQuery);
    }

    public void updateJobBuildStatusByIdList(Long recordId, Integer jobBuildStatus){
        updateJobBuildStatusByIdList(Lists.newArrayList(recordId), jobBuildStatus);
    }

    public void updateJobBuildStatusByIdList(List<Long> recordIdList, Integer jobBuildStatus){
        if (CollectionUtils.isEmpty(recordIdList) || Objects.isNull(jobBuildStatus)) {
            return;
        }
        scheduleJobBuildRecordDao.updateJobBuildStatus(recordIdList, jobBuildStatus);
    }

    public void updateJobBuildStatusAndLogByIdList(Long recordIdList, Integer jobBuildStatus, String log){
        updateJobBuildStatusAndLogByIdList(Lists.newArrayList(recordIdList), jobBuildStatus, log);
    }

    public void updateJobBuildStatusAndLogByIdList(List<Long> recordIdList, Integer jobBuildStatus, String log){
        if (CollectionUtils.isEmpty(recordIdList) || Objects.isNull(jobBuildStatus)) {
            return;
        }
        scheduleJobBuildRecordDao.updateJobBuildStatusAndLog(recordIdList, jobBuildStatus, log);
    }

    /**
     * 更新所运行中状态记录为指定状态
     *
     * @param jobBuildStatus 实例构建状态
     */
    public void refreshRunningStatus(Integer jobBuildStatus) {
        // 构建查询条件
        ScheduleJobBuildRecordQuery recordQuery = new ScheduleJobBuildRecordQuery();
        recordQuery.setJobBuildStatusList(Lists.newArrayList(JobBuildStatus.RUNNING.getStatus()));
        List<ScheduleJobBuildRecord> recordList = scheduleJobBuildRecordDao.getByCondition(recordQuery);
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        List<Long> recordIdList = recordList.stream().map(BaseEntity::getId).collect(Collectors.toList());
        updateJobBuildStatusByIdList(recordIdList, jobBuildStatus);
    }
}
