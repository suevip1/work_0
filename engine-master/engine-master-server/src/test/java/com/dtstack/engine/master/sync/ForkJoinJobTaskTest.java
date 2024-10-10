package com.dtstack.engine.master.sync;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.sync.ForkJoinJobTaskMock;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-19 19:19
 */
@MockWith(ForkJoinJobTaskMock.class)
public class ForkJoinJobTaskTest {
    ForkJoinJobTask forkJoinJobTask;

    @Before
    public void before() {
        ConcurrentHashMap<String, String> results = new ConcurrentHashMap<>();
        forkJoinJobTask = new ForkJoinJobTask("aJobId", results, null, null, false,null);
    }

    @Test
    public void compute() {
        ConcurrentHashMap<String, String> compute = forkJoinJobTask.compute();
        System.out.println(compute);
    }
}