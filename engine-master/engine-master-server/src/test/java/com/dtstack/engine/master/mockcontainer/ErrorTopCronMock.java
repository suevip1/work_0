package com.dtstack.engine.master.mockcontainer;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.vo.JobTopErrorVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobFailedDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.po.ScheduleJobFailed;
import com.google.common.collect.Lists;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author leon
 * @date 2022-06-27 18:44
 **/
public class ErrorTopCronMock {

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Boolean getOpenErrorTop() {
        return true;
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public int getMaxTenantSize() {
        return 20;
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    public List<Long> listAllDtUicTenantId() {
        return Lists.newArrayList(1L);
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    public List<ScheduleTaskShade> listByUicTenantId(@Param("tenantIds") List<Long> tenantIds, @Param("appType") Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setDtuicTenantId(1L);
        scheduleTaskShade.setProjectId(1L);
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    public List<JobTopErrorVO> listTopError(Long dtuicTenantId, Long projectId,Integer type, String startCycTime,String endCycTime, List<Integer> status,PageQuery pageQuery,Integer appType) {
        JobTopErrorVO jobTopErrorVO = new JobTopErrorVO();
        jobTopErrorVO.setTaskId(1L);
        jobTopErrorVO.setErrorCount(1);
        return Lists.newArrayList(jobTopErrorVO);
    }

    @MockInvoke(targetClass = ScheduleJobFailedDao.class)
    public Integer insertBatch(List<ScheduleJobFailed> scheduleJobFaileds) {
        return 1;
    }


    @MockInvoke(targetClass = ScheduleJobFailedDao.class)
    public Integer deleteByGmtCreate(Integer appType, Long uicTenantId, Long projectId,
                              Date toDate) {
        return 1;
    }



}
