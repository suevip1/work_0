package com.dtstack.engine.dao;

import com.dtstack.engine.po.ResourceQueueUsed;
import com.dtstack.engine.dto.TimeUsedNode;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ResourceQueueUsedDao {

    void clearByTimeInterval(@Param("timeInterval") Integer timeInterval);


    void batchInsert(@Param("useds") List<ResourceQueueUsed> resourceQueueUseds);

    ResourceQueueUsed listNewest(@Param("clusterId") Long clusterId,
                                       @Param("queueName") String queueName);

    List<TimeUsedNode> listLastTwoDayByClusterIdAndQueueName(@Param("clusterId") Long clusterId,
                                                             @Param("queueName") String queueName,
                                                             @Param("start") Date start,
                                                             @Param("end") Date stop);
}
