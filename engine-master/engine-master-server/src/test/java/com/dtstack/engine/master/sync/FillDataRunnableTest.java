package com.dtstack.engine.master.sync;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.project.FillDataChooseProjectVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataConditionInfoVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataRelyInfo;
import com.dtstack.engine.api.vo.task.FillDataChooseTaskVO;
import com.dtstack.engine.api.vo.task.TaskKeyVO;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.dtstack.engine.master.mockcontainer.impl.FillDataRunnableMock;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.springframework.context.support.StaticApplicationContext;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/6/30 10:33 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(FillDataRunnableMock.class)
public class FillDataRunnableTest {


    @Test
    public void testFillDataRunnable() {
        List<FillDataChooseProjectVO> fillDataChooseProjectVOS =  Lists.newArrayList();
        List<FillDataChooseTaskVO> taskIds =  Lists.newArrayList();
        List<FillDataChooseTaskVO> whitelist =  Lists.newArrayList();
        List<FillDataChooseTaskVO> blacklist =  Lists.newArrayList();
        FillDataChooseTaskVO fillDataChooseTaskVO = new FillDataChooseTaskVO();
        StaticApplicationContext staticApplicationContext = new StaticApplicationContext();
        FillDataRunnable fillDataRunnable = new FillDataRunnable(1L,"test_fill", FillDataTypeEnum.BATCH.getType(),
                true,fillDataChooseProjectVOS,taskIds,whitelist,blacklist,fillDataChooseTaskVO,
                "2022-06-01","2022-06-01",null,null,1L,1L,1l,1L,
                null,staticApplicationContext);

        Thread thread = new Thread(fillDataRunnable);
        thread.start();

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFillDataEnhanceRunnable() {
        StaticApplicationContext staticApplicationContext = new StaticApplicationContext();
        FillDataEnhanceRunnable fillDataRunnable = new FillDataEnhanceRunnable(1L,"test_fill", FillDataTypeEnum.BATCH.getType(),
                Lists.newArrayList(),new FillDataConditionInfoVO(),new FillDataRelyInfo(),Lists.newArrayList(),Lists.newArrayList(),"","","","",1L,
                1L,1L,1L,1,1, EScheduleType.NORMAL_SCHEDULE,staticApplicationContext);

        Thread thread = new Thread(fillDataRunnable);
        thread.start();

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
