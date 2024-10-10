package com.dtstack.engine.master.cron;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.dto.StatusCount;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.QueryWorkFlowModel;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.AlertTriggerRecordDao;
import com.dtstack.engine.dao.AlertTriggerRecordReceiveDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.impl.ProjectStatisticsService;
import com.dtstack.engine.master.impl.WorkSpaceProjectService;
import com.dtstack.engine.po.AlertTriggerRecord;
import com.dtstack.engine.po.AlertTriggerRecordReceive;
import com.dtstack.engine.po.ProjectStatistics;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2022/8/11
 */
@Component
public class ProjectMinuteStatisticsCron {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectMinuteStatisticsCron.class);

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ProjectStatisticsService projectStatisticsService;

    @Autowired
    private AlertTriggerRecordReceiveDao alertTriggerRecordReceiveDao;

    @Autowired
    private AlertTriggerRecordDao alertTriggerRecordDao;

    @Autowired
    private WorkSpaceProjectService projectService;

    @Resource
    private ThreadPoolTaskExecutor projectMinuteStatisticsExecutor;

    @Value("${project.statistics.diff.interval:5}")
    private Integer diffInterval;

    @EngineCron
    @Scheduled(cron = "${project.statistics.scan.cron:0 0/5 * * * ? } ")
    public void handle() {
        LOGGER.info("{} do handle", this.getClass().getSimpleName());

        Integer appType = AppType.RDOS.getType();
        List<Map<String, Integer>> ownerInfo = scheduleTaskShadeDao.listOwnerIds(appType);
        Table<Long, Long, Long> ownerTable = HashBasedTable.create();
        HashMap<Long, Long> projectIds = new HashMap<>();
        for (Map<String, Integer> owner : ownerInfo) {
            try {
                ownerTable.put(owner.get("ownerUserId").longValue(), owner.get("projectId").longValue(), owner.get("tenantId").longValue());
                projectIds.put(owner.get("projectId").longValue(), owner.get("tenantId").longValue());
            } catch (Exception e) {
                LOGGER.error("get project info error", e);
            }
        }

        LocalDateTime startDateTime = projectStatisticsService.getRecentStartTime();

        List<AlertTriggerRecordReceive> receiverUserIds = isNewAlarmCreate(appType, startDateTime);

        boolean hasNewAlarm = CollectionUtils.isNotEmpty(receiverUserIds);
        LOGGER.info("has new alarm create ");
        if (!CollectionUtils.isEmpty(receiverUserIds)) {
            //告警可能发送的人不再责任人里
            fillAlarmReceiver(appType, ownerTable, projectIds, receiverUserIds);
        }

        //个人统计
        flushOwner(appType, ownerTable, hasNewAlarm, startDateTime);

        //项目统计
        flushProject(appType, projectIds, startDateTime, hasNewAlarm);

        //清理
        clear(startDateTime);

    }

    private void fillAlarmReceiver(Integer appType, Table<Long, Long, Long> ownerTable, HashMap<Long, Long> projectIds, List<AlertTriggerRecordReceive> receiverUserIds) {
        for (AlertTriggerRecordReceive receiverUserId : receiverUserIds) {
            Long projectTenant = projectIds.get(receiverUserId.getProjectId());
            if (null == projectTenant) {
                AuthProjectVO authProjectVO = projectService.finProject(receiverUserId.getProjectId(), appType);
                if (null == authProjectVO) {
                    continue;
                }
                projectTenant = authProjectVO.getDtuicTenantId();
            }
            ownerTable.put(receiverUserId.getReceiveUserId(), receiverUserId.getProjectId(), projectTenant);
            projectIds.put(receiverUserId.getProjectId(), projectTenant);
        }
    }


    private void clear(LocalDateTime startDateTime) {
        LocalDateTime clearDateTime = startDateTime.plusDays(-1).plusMinutes(-projectStatisticsService.getTimeInterval());
        projectStatisticsService.delete(Date.from(clearDateTime.atZone(ZoneId.systemDefault()).toInstant()));
    }

    private void flushOwner(Integer appType, Table<Long, Long, Long> ownerTable, boolean hasNewAlarm, LocalDateTime startDateTime) {
        //实例总数是不会变化的 只统一一次就行
        //统计间隔累加 个人统计
        List<List<Cell<Long, Long, Long>>> batchCells = Lists.partition(new ArrayList<>(ownerTable.cellSet()), 10);
        for (List<Cell<Long, Long, Long>> batchCell : batchCells) {
            computeOwnerBatch(batchCell, appType, hasNewAlarm, startDateTime);
        }
    }

    public void computeOwnerBatch(List<Cell<Long, Long, Long>> batchCells, Integer appType, boolean hasNewAlarm, LocalDateTime startDateTime) {
        CompletableFuture.runAsync(() -> {
            List<ProjectStatistics> statisticsInfos = batchCells.stream().map(cell -> {
                try {
                    Long userId = cell.getRowKey();
                    Long projectId = cell.getColumnKey();
                    Long tenantId = cell.getValue();
                    ProjectStatistics statisticsInfo = buildStatisticsByQuery(appType, userId, projectId, tenantId);
                    if (hasNewAlarm) {
                        //告警 根据个人统计 一条告警发多个人 项目不能直接聚合个人总数
                        Timestamp time = new Timestamp(DateTime.now().withTime(0, 0, 0, 0).getMillis());
                        Integer receiverAlarm = alertTriggerRecordReceiveDao.selectCountByReceiverId(userId, time, appType, projectId);
                        statisticsInfo.setAlarmTotal(receiverAlarm);
                    } else {
                        ProjectStatistics projectStatistics = projectStatisticsService.queryRecent(projectId, appType, tenantId, userId);
                        statisticsInfo.setAlarmTotal(projectStatistics == null ? 0 : projectStatistics.getAlarmTotal());
                    }
                    return statisticsInfo;
                } catch (Exception e) {
                    LOGGER.error("get owner {} error", cell.getRowKey(), e);
                }
                return null;
            }).collect(Collectors.toList());
            flush(statisticsInfos, startDateTime);
        }, projectMinuteStatisticsExecutor);
    }


    private void flushProject(Integer appType, HashMap<Long, Long> projectIds, LocalDateTime startDateTime, boolean hasNewAlarm) {
        projectIds.keySet().forEach(project -> CompletableFuture.runAsync(() -> {
            ProjectStatistics statisticsInfo = buildStatisticsByQuery(appType, null, project, projectIds.get(project));
            if (hasNewAlarm) {
                Timestamp time = new Timestamp(DateTime.now().withTime(0, 0, 0, 0).getMillis());
                Integer totalAlarm = alertTriggerRecordDao.countAlarmByGmtCreate(time, project, appType);
                statisticsInfo.setAlarmTotal(totalAlarm);
            } else {
                ProjectStatistics projectStatistics = projectStatisticsService.queryRecent(project, appType, projectIds.get(project), -1L);
                statisticsInfo.setAlarmTotal(projectStatistics == null ? 0 : projectStatistics.getAlarmTotal());
            }
            flush(Lists.newArrayList(statisticsInfo), startDateTime);
        }, projectMinuteStatisticsExecutor));
    }

    private void flush(List<ProjectStatistics> statistics, LocalDateTime localDateTime) {
        statistics = statistics.stream().peek(projectStatistics -> projectStatistics.setGmtCreate(Timestamp.valueOf(localDateTime)))
                .collect(Collectors.toList());
        projectStatisticsService.insertBatch(statistics);
    }


    private List<AlertTriggerRecordReceive> isNewAlarmCreate(Integer appType, LocalDateTime startDateTime) {
        AlertTriggerRecord alertTriggerRecord = alertTriggerRecordDao.selectLast(appType);
        // 每一次间隔 如果没有新告警 则取上次统计数据
        // 如果重启导致一个间隔暂停 则永远统计统计不到了 放宽到多个周期
        Date lastCreateTime = Date.from(startDateTime.toInstant(DateUtil.DEFAULT_ZONE)
                .plus((long) -diffInterval * projectStatisticsService.getTimeInterval(), ChronoUnit.MINUTES));
        if (alertTriggerRecord != null &&
                alertTriggerRecord.getGmtCreate().after(lastCreateTime)) {
            Timestamp time = new Timestamp(DateTime.now().withTime(0, 0, 0, 0).getMillis());
            return alertTriggerRecordReceiveDao.selectRecordUserIdByReceiverIds(time);
        }
        return new ArrayList<>();
    }

    private ProjectStatistics buildStatisticsByQuery(Integer appType, Long userId, Long projectId, Long tenantId) {
        ProjectStatistics statisticsInfo = new ProjectStatistics();
        statisticsInfo.setProjectId(projectId);
        statisticsInfo.setTenantId(tenantId);
        statisticsInfo.setAppType(appType);
        statisticsInfo.setUserId(userId == null ? -1L : userId);

        ScheduleJobDTO scheduleJobDTO = new ScheduleJobDTO();
        scheduleJobDTO.setDtuicTenantId(tenantId);
        scheduleJobDTO.setProjectId(projectId);
        if (null != userId) {
            List<Long> taskIds = scheduleTaskShadeDao.listTaskIdsInOwnerId(appType, projectId, userId);
            scheduleJobDTO.setTaskIds(taskIds);
        }
        scheduleJobDTO.setAppType(appType);
        scheduleJobDTO.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        String cycTimeStartDay = DateUtil.getUnStandardFormattedDate(DateUtil.calTodayMills());
        String cycTimeEndDay = DateUtil.getUnStandardFormattedDate(DateUtil.TOMORROW_ZERO());
        scheduleJobDTO.setCycStartDay(cycTimeStartDay);
        scheduleJobDTO.setCycEndDay(cycTimeEndDay);
        scheduleJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        //总实例
        List<StatusCount> jobsStatusStatistics = scheduleJobDao.getJobsStatusStatistics(scheduleJobDTO);
        Integer total = jobsStatusStatistics.stream().map(StatusCount::getCount).reduce(0, Integer::sum);
        statisticsInfo.setTotalJob(total);

        //未提交
        ArrayList<Integer> unSubmitStatusList = Lists.newArrayList();
        unSubmitStatusList.addAll(RdosTaskStatus.UNSUBMIT_STATUS);
        unSubmitStatusList.addAll(RdosTaskStatus.WAIT_STATUS);
        unSubmitStatusList.addAll(RdosTaskStatus.SUBMITTING_STATUS);
        scheduleJobDTO.setJobStatuses(unSubmitStatusList);
        List<StatusCount> unSubmitStatus = scheduleJobDao.getJobsStatusStatistics(scheduleJobDTO);
        Integer unSubmit = unSubmitStatus.stream().map(StatusCount::getCount).reduce(0, Integer::sum);
        statisticsInfo.setUnSubmitJob(unSubmit);

        //失败
        scheduleJobDTO.setJobStatuses(RdosTaskStatus.FAILED_STATUS);
        List<StatusCount> failStatus = scheduleJobDao.getJobsStatusStatistics(scheduleJobDTO);
        Integer fail = failStatus.stream().map(StatusCount::getCount).reduce(0, Integer::sum);
        statisticsInfo.setFailJob(fail);

        //运行
        scheduleJobDTO.setJobStatuses(RdosTaskStatus.RUNNING_STATUS);
        List<StatusCount> runStatus = scheduleJobDao.getJobsStatusStatistics(scheduleJobDTO);
        Integer run = runStatus.stream().map(StatusCount::getCount).reduce(0, Integer::sum);
        statisticsInfo.setRunJob(run);
        return statisticsInfo;
    }
}
