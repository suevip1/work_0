package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.dto.ScheduleTaskTaskShadeDTO;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.sync.FillDataRunnable;
import com.dtstack.engine.master.sync.fill.FillDataTask;
import com.dtstack.engine.master.sync.fill.FillDataTaskFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 11:20 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataEnhanceRunnableMock extends BaseMock {
    @MockInvoke(targetClass = ScheduleJobService.class)
    public void createFillJob(Set<String> all, Set<String> run, List<String> blackTaskKeyList, Long fillId, String fillName, String beginTime, String endTime,
                              String startDay, String endDay, Long projectId, Long tenantId, Long dtuicTenantId,
                              Long userId, Boolean ignoreCycTime) {
    }

    @MockInvoke(targetClass = FillDataRunnable.class)
    Integer updateGeneratStatus(Long fillId,Integer oldStatus, Integer status) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleTaskTaskShadeService.class)
    public List<ScheduleTaskTaskShadeDTO> listChildByTaskKeys(List<String> taskKeys) {
        return Lists.newArrayList();
    }

    @MockInvoke(targetClass = FillDataTaskFactory.class)
    public FillDataTask getFillDataTask(Integer fillDataType) {
        return new FillDataTask() {

            @Override
            public Set<String> getAllList(Set<String> run) {
                return Sets.newHashSet();
            }

            @Override
            public FillDataTypeEnum setFillDataType(Integer fillDataType) {
                return FillDataTypeEnum.BATCH;
            }

            @Override
            public Set<String> getRunList() {
                return Sets.newHashSet();
            }
        };
    }

    @MockInvoke(targetClass = FillDataTask.class)
    public Set<String> getRunList() {
        return Sets.newHashSet();
    }



    @MockInvoke(targetClass = ScheduleJobService.class)
    public void createFillJob(Set<String> all, Set<String> run, List<String> blackTaskKeyList, Long fillId, String fillName, String beginTime, String endTime,
                              String startDay, String endDay, Long projectId, Long tenantId, Long dtuicTenantId,
                              Long userId, Boolean ignoreCycTime, EScheduleType scheduleType) throws Exception {
    }

}
