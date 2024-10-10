package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.QueueDao;
import org.assertj.core.util.Lists;

import java.util.Collections;
import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-29 18:02
 */
public class QueueServiceMock {

    @MockInvoke(targetClass = QueueDao.class)
    List<Queue> listByClusterWithLeaf(List<Long> clusterId) {
        if (clusterId.contains(-1L)) {
            Queue queue = new Queue();
            queue.setQueueName("default");
            queue.setQueuePath("default");
            queue.setClusterId(-1L);
            queue.setMaxCapacity("20");
            queue.setCapacity("20");
            return Lists.newArrayList(queue);
        } else {
            return Collections.emptyList();
        }
    }

    @MockInvoke(targetClass = ClusterTenantDao.class)
    Integer existsQueue( Long queueId) {
        return null;
    }

    @MockInvoke(targetClass = QueueDao.class)
    Integer deleteByIds(  List<Long> collect,  Long clusterId) {
        return 1;
    }

    @MockInvoke(targetClass = QueueDao.class)
    Integer update(Queue oldQueue) {
        return 1;
    }

    @MockInvoke(targetClass = QueueDao.class)
    List<Queue> listByClusterId(Long clusterId) {
        Queue q = new Queue();
        q.setQueuePath("default");
        q.setQueueName("default");
        q.setClusterId(clusterId);
        q.setParentQueueId(null);
        q.setMaxCapacity("20");
        q.setCapacity("20");
        return Lists.newArrayList(q);
    }

    @MockInvoke(targetClass = QueueDao.class)
    Integer insert(Queue queue) {
        return 1;
    }
}
