package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.listener.QueueListener;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.queue.GroupInfo;
import com.dtstack.engine.master.queue.QueueInfo;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2022/6/27 11:50 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobPartitionerMock extends BaseMock {

    @MockInvoke(targetClass = QueueListener.class)
    public Map<Integer, Map<String, QueueInfo>> getAllNodesJobQueueInfo() {
        Map<Integer, Map<String, QueueInfo>> allNodesJobQueueTypes = Maps.newHashMap();
        Map<String, QueueInfo> map = Maps.newHashMap();
        QueueInfo queueInfo1 = new QueueInfo();
        queueInfo1.setSize(10);
        QueueInfo queueInfo2 = new QueueInfo();
        queueInfo1.setSize(10);
        QueueInfo queueInfo3 = new QueueInfo();
        queueInfo1.setSize(10);

        map.put("127.0.0.1:8090",queueInfo1);
        map.put("127.0.0.1:8091",queueInfo2);
        map.put("127.0.0.1:8092",queueInfo3);
        allNodesJobQueueTypes.put(1,map);
        return allNodesJobQueueTypes;
    }

    @MockInvoke(targetClass = QueueListener.class)
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
