package com.dtstack.engine.master.sync;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.schedule.job.FillDataConditionInfoVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataRelyInfo;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.dtstack.engine.master.mockcontainer.impl.FillDataEnhanceRunnableMock;
import com.dtstack.engine.master.mockcontainer.impl.FillDataRunnableMock;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.springframework.context.support.StaticApplicationContext;

/**
 * @Auther: dazhi
 * @Date: 2022/12/13 11:19 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(FillDataEnhanceRunnableMock.class)
public class FillDataEnhanceRunnableTest {

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
