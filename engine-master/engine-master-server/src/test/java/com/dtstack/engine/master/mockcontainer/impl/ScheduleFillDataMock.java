package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.dao.ScheduleFillDataJobDao;
import com.dtstack.engine.po.ScheduleFillDataJob;
import org.assertj.core.util.Lists;

import java.util.List;

public class ScheduleFillDataMock {

    @MockInvoke(targetClass = ScheduleFillDataJobDao.class)
    ScheduleFillDataJob getByJobName(String jobName, Long projectId) {
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();
        return scheduleFillDataJob;
    }

    @MockInvoke(targetClass = ScheduleFillDataJobDao.class)
    Integer insert(ScheduleFillDataJob fillDataJob) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleFillDataJobDao.class)
    List<ScheduleFillDataJob> listFillJob(List<String> nameList, long projectId) {
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();
        return Lists.newArrayList(scheduleFillDataJob);
    }

    @MockInvoke(targetClass = ScheduleFillDataJobDao.class)
    List<ScheduleFillDataJob> listFillJobByPageQuery(PageQuery<ScheduleJobDTO> pageQuery) {
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();
        return Lists.newArrayList(scheduleFillDataJob);
    }

    @MockInvoke(targetClass = ScheduleFillDataJobDao.class)
    List<ScheduleFillDataJob> getFillJobList(List<Long> fillIdList, Long projectId, Long tenantId, Long dtuicTenantId) {
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();
        return Lists.newArrayList(scheduleFillDataJob);
    }

    @MockInvoke(targetClass = ScheduleFillDataJobDao.class)
    Integer updateGeneratStatus(Long fillId, Integer oldStatus, Integer status) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleFillDataJobDao.class)
    List<ScheduleFillDataJob> listFillJobByIds(List<Long> fillIds) {
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();
        return Lists.newArrayList(scheduleFillDataJob);
    }

    @MockInvoke(targetClass = ScheduleFillDataJobDao.class)
    ScheduleFillDataJob getById(Long id) {
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();
        return scheduleFillDataJob;
    }

    @MockInvoke(targetClass = ScheduleFillDataJobDao.class)
    Integer incrementParallelNum(Long id) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleFillDataJobDao.class)
    Integer decrementParallelNum(Long id) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleFillDataJobDao.class)
    Integer countListFillJobByPageQuery(PageQuery<ScheduleJobDTO> pageQuery) {
        return 1;
    }
}
