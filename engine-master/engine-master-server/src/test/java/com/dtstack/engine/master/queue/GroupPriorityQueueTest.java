package com.dtstack.engine.master.queue;

import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.mockcontainer.impl.GroupPriorityQueueMock;
import org.junit.Before;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/6/27 8:48 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(GroupPriorityQueueMock.class)
public class GroupPriorityQueueTest {

    GroupPriorityQueue groupPriorityQueue = GroupPriorityQueue.builder();


    @Before
    public void before() {
        PrivateAccessor.set(groupPriorityQueue,"jobDealer",new JobDealer());
    }

    @Test
    public void addTest() {

        groupPriorityQueue.setJobResource("123");
        groupPriorityQueue.build();

        groupPriorityQueue.add(
                new JobClient(),true,true
        );

        groupPriorityQueue.add(
                new JobClient(),false,true
        );
    }


}
