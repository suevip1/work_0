package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.param.OperatorParam;
import com.dtstack.engine.api.vo.ScheduleJobOperateVO;
import com.dtstack.engine.dao.ScheduleJobOperateDao;
import com.dtstack.engine.master.mapstruct.OperateStruct;
import com.dtstack.engine.po.ScheduleJobOperate;
import org.assertj.core.util.Lists;

import java.util.List;
import java.util.stream.Collectors;


public class ScheduleJobOperatorMock {


    @MockInvoke(targetClass = ScheduleJobOperateDao.class)
    void insert(ScheduleJobOperate scheduleJobOperate) {
        return;
    }

    @MockInvoke(targetClass = ScheduleJobOperateDao.class)
    void insertBatch(List<ScheduleJobOperate> scheduleJobOperates) {
        return;
    }

    @MockInvoke(targetClass = ScheduleJobOperateDao.class)
    Integer countPage(String jobId) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobOperateDao.class)
    List<ScheduleJobOperate> page(OperatorParam pageParam) {
        ScheduleJobOperate scheduleJobOperate = new ScheduleJobOperate();
        return Lists.newArrayList(scheduleJobOperate);
    }

    @MockInvoke(targetClass = OperateStruct.class)
    List<ScheduleJobOperateVO> toScheduleJobOperateVOs(List<ScheduleJobOperate> scheduleJobOperate) {
        return scheduleJobOperate.stream().map(jobOperate -> {
            ScheduleJobOperateVO scheduleJobOperateVO = new ScheduleJobOperateVO();
            scheduleJobOperateVO.setJobId(jobOperate.getJobId());
            return scheduleJobOperateVO;
        }).collect(Collectors.toList());
    }
}
