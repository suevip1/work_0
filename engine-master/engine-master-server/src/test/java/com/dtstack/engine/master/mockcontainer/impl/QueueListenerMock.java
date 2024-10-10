package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.master.executor.JobExecutorTrigger;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.listener.QueueListener;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.queue.GroupInfo;
import com.dtstack.engine.master.queue.QueueInfo;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2022/6/27 8:34 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueueListenerMock extends BaseMock {


    @MockInvoke(targetClass = JobExecutorTrigger.class)
    public Map<String, Map<Integer, QueueInfo>> getAllNodesJobQueueInfo() {
        Map<String, Map<Integer, QueueInfo>> allNodesJobQueueTypes = Maps.newHashMap();
        Map<Integer, QueueInfo> map = Maps.newHashMap();
        QueueInfo queueInfo1 = new QueueInfo();
        queueInfo1.setSize(10);
        QueueInfo queueInfo2 = new QueueInfo();
        queueInfo1.setSize(10);
        QueueInfo queueInfo3 = new QueueInfo();
        queueInfo1.setSize(10);

        map.put(1,queueInfo1);
        map.put(2,queueInfo2);
        map.put(3,queueInfo3);
        allNodesJobQueueTypes.put("127.0.0.1:8090",map);
        return allNodesJobQueueTypes;
    }

    @MockInvoke(targetClass = JobDealer.class)
    public Map<String, Map<String, GroupInfo>> getAllNodesGroupQueueInfo() {
        Map<String, Map<String, GroupInfo>> allNodesJobQueueTypes = Maps.newHashMap();
        Map<String, GroupInfo> map = Maps.newHashMap();
        GroupInfo queueInfo1 = new GroupInfo();
        queueInfo1.setSize(10);
        GroupInfo queueInfo2 = new GroupInfo();
        queueInfo1.setSize(10);
        GroupInfo queueInfo3 = new GroupInfo();
        queueInfo1.setSize(10);

        map.put("127.0.0.1:8090",queueInfo1);
        map.put("127.0.0.1:8091",queueInfo2);
        map.put("127.0.0.1:8092",queueInfo3);
        allNodesJobQueueTypes.put("1",map);
        return allNodesJobQueueTypes;
    }

}
