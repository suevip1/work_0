package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.BaseEntity;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.QueueDao;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QueueService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueService.class);

    private final static long ROOT_QUEUE_ID = -1L;

    private static final long DEFAULT_KUBERNETES_PARENT_NODE = -2L;

    @Autowired
    private QueueDao queueDao;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    /**
     * 更新 yarn queue 信息
     *
     * @param clusterId   集群 id
     * @param description 集群描述
     */
    public void updateQueue(Long clusterId, ComponentTestResult.ClusterResourceDescription description){
        List<Queue> queues = queueDao.listByClusterWithLeaf(Lists.newArrayList(clusterId));
        if(CollectionUtils.isEmpty(queues)){
            newAddQueue(clusterId, ROOT_QUEUE_ID, description.getQueueDescriptions());
        } else {
            Map<String, Queue> existQueueMap = new HashMap<>(queues.size());
            for (Queue queue : queues) {
                existQueueMap.put(queue.getQueuePath(), queue);
            }

            updateAddQueue(existQueueMap, clusterId, ROOT_QUEUE_ID, description.getQueueDescriptions());
            if (!existQueueMap.isEmpty()) {
                // 仍存留的 queue 属于脏 queue(脏 queue：插件没有返回 queue 信息的那些 queue)，应当清除掉
                List<Long> remainDirtyQueueIds = existQueueMap.values().stream().map(BaseEntity::getId).collect(Collectors.toList());
                // 但如果该 queue 已经被引用了，就不清除
                filterReferredQueueIds(remainDirtyQueueIds);
                if (CollectionUtils.isNotEmpty(remainDirtyQueueIds)) {
                    // 剩下的是最终的脏 queue
                    Integer delete = queueDao.deleteByIds(remainDirtyQueueIds, clusterId);
                    if (delete != remainDirtyQueueIds.size()) {
                        throw new RdosDefineException("operation failed");
                    }
                }
            }
        }
    }

    private void newAddQueue(Long clusterId, Long parentQueueId, List<ComponentTestResult.QueueDescription> descriptions) {
        if (CollectionUtils.isNotEmpty(descriptions)) {
            for (ComponentTestResult.QueueDescription queueDescription : descriptions) {
                Queue queue = new Queue();
                queue.setQueueName(queueDescription.getQueueName());
                queue.setClusterId(clusterId);
                queue.setMaxCapacity(queueDescription.getMaximumCapacity());
                queue.setCapacity(queueDescription.getCapacity());
                queue.setQueueState(queueDescription.getQueueState());
                queue.setParentQueueId(parentQueueId);
                queue.setQueuePath(queueDescription.getQueuePath());
                Integer insert = queueDao.insert(queue);
                if (insert != 1) {
                    throw new RdosDefineException("operation failed");
                }
                newAddQueue(clusterId, queue.getId(), queueDescription.getChildQueues());
            }
        }
    }

    private void updateAddQueue(Map<String, Queue> existQueueMap, Long clusterId, Long parentQueueId, List<ComponentTestResult.QueueDescription> descriptions) {
        //不会有空队列的
        if (CollectionUtils.isNotEmpty(descriptions)) {
            for (ComponentTestResult.QueueDescription queueDescription : descriptions) {
                Queue queue = new Queue();
                queue.setQueueName(queueDescription.getQueueName());
                queue.setClusterId(clusterId);
                queue.setMaxCapacity(queueDescription.getMaximumCapacity());
                queue.setCapacity(queueDescription.getCapacity());
                queue.setQueueState(queueDescription.getQueueState());
                queue.setQueuePath(queueDescription.getQueuePath());

                Queue oldQueue = existQueueMap.get(queueDescription.getQueuePath());
                if (oldQueue != null) {
                    if (oldQueue.baseEquals(queue)) {
                        existQueueMap.remove(queueDescription.getQueuePath());
                    } else if (queue.getQueueName().equals(oldQueue.getQueueName())) {
                        oldQueue.setQueueState(queue.getQueueState());
                        oldQueue.setCapacity(queue.getCapacity());
                        oldQueue.setMaxCapacity(queue.getMaxCapacity());
                        queueDao.update(oldQueue);
                        existQueueMap.remove(queueDescription.getQueuePath());
                    }
                    queue.setId(oldQueue.getId());
                } else {
                    queue.setParentQueueId(parentQueueId);
                    Integer insert = queueDao.insert(queue);
                    if (insert != 1) {
                        throw new RdosDefineException("operation failed");
                    }
                }
                // todo 递归调用，当没有子队列后就停止递归
                updateAddQueue(existQueueMap, clusterId, queue.getId(), queueDescription.getChildQueues());
            }
        }
    }

    /**
     * 添加k8s的namespace
     * @param clusterId
     * @param namespace
     */
    public Long addNamespaces(Long clusterId, String namespace) {
        if(StringUtils.isBlank(namespace)){
            throw new RdosDefineException("namespace cannot be empty");
        }

        //校验namespace的是否存在
        List<Queue> namespaces = queueDao.listByClusterId(clusterId);
        if (CollectionUtils.isNotEmpty(namespaces)) {
            List<Long> namespaceIds = namespaces.stream().map(BaseEntity::getId).collect(Collectors.toList());
            Integer delete = queueDao.deleteByIds(namespaceIds, clusterId);
            if (delete != namespaces.size()) {
                throw new RdosDefineException("operation failed");
            }
        }
        Queue queue = new Queue();
        queue.setQueueName(namespace);
        queue.setClusterId(clusterId);
        queue.setMaxCapacity("0");
        queue.setCapacity("0");
        queue.setQueueState("ACTIVE");
        queue.setParentQueueId(DEFAULT_KUBERNETES_PARENT_NODE);
        queue.setQueuePath(namespace);
        Integer insert = queueDao.insert(queue);
        if (insert != 1) {
            throw new RdosDefineException("operation failed");
        }
        return queue.getId();
    }

    public Queue getQueueByPath(Long clusterId, String queuePath) {
        return queueDao.getByClusterIdAndQueuePath(clusterId, queuePath);
    }

    /**
     * 过滤掉已经被引用的 queue
     *
     * @param remainDirtyQueueIds
     */
    private void filterReferredQueueIds(List<Long> remainDirtyQueueIds) {
        if (CollectionUtils.isEmpty(remainDirtyQueueIds)) {
            return;
        }
        remainDirtyQueueIds.removeIf(queueId -> {
            Integer existsQueue = clusterTenantDao.existsQueue(queueId);
            boolean exists = (existsQueue != null);
            if (exists) {
                // 已经被引用的 queue，不视为脏 queue
                LOGGER.warn("queueId:{} already be referred", queueId);
                return true;
            } else {
                return false;
            }
        });
    }
}
