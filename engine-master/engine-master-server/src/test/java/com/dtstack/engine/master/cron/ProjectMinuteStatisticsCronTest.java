package com.dtstack.engine.master.cron;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.AlertTriggerRecordDao;
import com.dtstack.engine.dao.AlertTriggerRecordReceiveDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.dto.StatusCount;
import com.dtstack.engine.master.impl.ProjectStatisticsService;
import com.dtstack.engine.master.impl.WorkSpaceProjectService;
import com.dtstack.engine.po.AlertTriggerRecord;
import com.dtstack.engine.po.AlertTriggerRecordReceive;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProjectMinuteStatisticsCronTest {
    ProjectMinuteStatisticsCron cron = new ProjectMinuteStatisticsCron();

    @Test
    public void test() {
        AtomicBoolean isMaster = new AtomicBoolean(true);
        PrivateAccessor.set(cron, "diffInterval", 1);
//        cron.handle();
    }


    public static class Mock {
        @MockInvoke(
                targetClass = ScheduleTaskShadeDao.class,
                targetMethod = "listOwnerIds"
        )
        public List<Map<String, Integer>> listOwnerIds(Integer appType) {
            Map<String, Integer> map = new HashMap<>();
            map.put("ownerUserId", 1);
            map.put("projectId", 1);
            map.put("tenantId", 1);
            return Lists.newArrayList(map);
        }

        @MockInvoke(
                targetClass = ProjectStatisticsService.class,
                targetMethod = "getRecentStartTime"
        )
        public LocalDateTime getRecentStartTime() {
            return LocalDateTime.now().plusDays(-1);
        }

        @MockInvoke(
                targetClass = AlertTriggerRecordDao.class,
                targetMethod = "selectLast"
        )
        public AlertTriggerRecord selectLast(Integer appType) {
            AlertTriggerRecord record = new AlertTriggerRecord();
            record.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
            return record;
        }

        @MockInvoke(
                targetClass = AlertTriggerRecordReceiveDao.class,
                targetMethod = "selectRecordUserIdByReceiverIds"
        )
        public List<AlertTriggerRecordReceive> selectRecordUserIdByReceiverIds(Timestamp gmtCreate) {
            return new ArrayList<>();
        }


        @MockInvoke(
                targetClass = WorkSpaceProjectService.class,
                targetMethod = "finProject"
        )
        public AuthProjectVO finProject(Long projectId, Integer appType) {
            return null;
        }


        @MockInvoke(
                targetClass = ScheduleTaskShadeDao.class,
                targetMethod = "listTaskIdsInOwnerId"
        )
        public List<Long> listTaskIdsInOwnerId(Integer appType, Long projectId, Long ownerUserId) {
            return Lists.newArrayList(1L);
        }

        @MockInvoke(
                targetClass = ScheduleJobDao.class,
                targetMethod = "getJobsStatusStatistics"
        )
        public List<StatusCount> getJobsStatusStatistics(ScheduleJobDTO object) {
            StatusCount statusCount = new StatusCount();
            statusCount.setStatus(RdosTaskStatus.RUNNING.getStatus());
            statusCount.setCount(1);
            return Lists.newArrayList(statusCount);
        }

        @MockInvoke(
                targetClass = ProjectStatisticsService.class,
                targetMethod = "getTimeInterval"
        )
        public Integer getTimeInterval() {
            return 1;
        }

        @MockInvoke(
                targetClass = ProjectStatisticsService.class,
                targetMethod = "delete"
        )
        public void delete(Date gmtCreate) {
        }
    }
}
