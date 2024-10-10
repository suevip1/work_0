package com.dtstack.engine.master.cron;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.vo.JobTopErrorVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobFailedDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.impl.ProjectStatisticsService;
import com.dtstack.engine.po.ScheduleJobFailed;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class ErrorTopCronTest {
    ErrorTopCron errorTopCron = new ErrorTopCron();

    @Test
    public void test() {
        errorTopCron.runErrorTop();
        try {
            errorTopCron.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Mock {
        @MockInvoke(
                targetClass = EnvironmentContext.class,
                targetMethod = "getOpenErrorTop"
        )
        public Boolean getOpenErrorTop() {
            return true;
        }

        @MockInvoke(
                targetClass = ClusterTenantDao.class,
                targetMethod = "listAllDtUicTenantId"
        )
        public List<Long> listAllDtUicTenantId() {
            return Lists.newArrayList(1L);
        }

        @MockInvoke(
                targetClass = EnvironmentContext.class,
                targetMethod = "getMaxTenantSize"
        )
        public int getMaxTenantSize() {
            return 10;
        }

        @MockInvoke(
                targetClass = ScheduleTaskShadeDao.class,
                targetMethod = "listByUicTenantId"
        )
        public List<ScheduleTaskShade> listByUicTenantId(List<Long> tenantIds, Integer appType) {
            ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
            return Lists.newArrayList(scheduleTaskShade);
        }

        @MockInvoke(
                targetClass = ScheduleJobDao.class,
                targetMethod = "listTopError"
        )
        public List<JobTopErrorVO> listTopError(Long dtuicTenantId, Long projectId, Integer type,
                                                String startCycTime, String endCycTime, List<Integer> status, PageQuery pageQuery,
                                                Integer appType) {
            JobTopErrorVO jobTopErrorVO = new JobTopErrorVO();
            jobTopErrorVO.setErrorCount(1);
            jobTopErrorVO.setTaskId(1);
            return Lists.newArrayList(jobTopErrorVO);
        }
        @MockInvoke(
                targetClass = ScheduleJobFailedDao.class,
                targetMethod = "insertBatch"
        )
        public Integer insertBatch(List<ScheduleJobFailed> scheduleJobFaileds) {
            return 1;
        }

        @MockInvoke(
                targetClass = ScheduleJobFailedDao.class,
                targetMethod = "deleteByGmtCreate"
        )
        public Integer deleteByGmtCreate(Integer appType, Long uicTenantId, Long projectId,
                                         Date toDate) {
            return 1;
        }

        @MockInvoke(
                targetClass = ScheduleDictDao.class,
                targetMethod = "update"
        )
        public Integer update(String dictCode, String dictName, String update, String oldUpdate) {
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
