package com.dtstack.engine.master.queue;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.GroupPriorityQueueMock;
import com.dtstack.engine.master.mockcontainer.impl.JobPartitionerMock;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/6/27 11:39 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(JobPartitionerMock.class)
public class JobPartitionerTest {

    JobPartitioner jobPartitioner = new JobPartitioner();

    @Test
    public void getDefaultStrategyTest() {
        List<String> nodes = Lists.newArrayList();
        nodes.add("127.0.0.1:8090");
        nodes.add("127.0.0.1:8091");
        nodes.add("127.0.0.1:8092");
        jobPartitioner.getDefaultStrategy(nodes,15);
    }

    @Test
    public void computeBatchJobSizeTest() {
        jobPartitioner.computeBatchJobSize(1,15);
    }


    @Test
    public void computeJobCacheSize() {
        jobPartitioner.computeJobCacheSize("1",15);
    }

    @Test
    public void getGroupInfoByJobResource() {
        jobPartitioner.getGroupInfoByJobResource("1");
    }

}
