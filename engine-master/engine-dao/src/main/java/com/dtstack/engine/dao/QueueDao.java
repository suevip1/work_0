package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Queue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QueueDao {

    Integer insert(Queue queue);

    List<Queue> listByClusterId(@Param("clusterId") Long clusterId);

    List<Queue> listByClusterWithLeaf(@Param("clusterIds") List<Long> clusterId);

    Integer update(Queue oldQueue);

    Queue getOne(@Param("id") Long id);

    Integer deleteByIds(@Param("ids") List<Long> collect, @Param("clusterId") Long clusterId);

    Integer countByParentQueueId(@Param("parentQueueId") Long parentQueueId);

    List<Queue> listByIds(@Param("ids") List<Long> ids);

    Queue getByClusterIdAndQueuePath(@Param("clusterId") Long clusterId, @Param("queuePath") String queuePath);
}
