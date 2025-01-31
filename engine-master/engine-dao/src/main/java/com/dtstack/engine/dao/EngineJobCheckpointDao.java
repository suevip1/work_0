package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/12
 */
public interface EngineJobCheckpointDao {

    int insert(@Param("taskId")String taskId, @Param("engineTaskId")String engineTaskId,
               @Param("checkpointId") String checkpointId,
               @Param("checkpointTrigger") Timestamp checkpointTrigger,
               @Param("checkpointSavepath") String checkpointSavepath,
               @Param("checkpointCounts") String checkpointCounts,
               @Param("checkpointSize")Long checkpointSize,
               @Param("checkpointDuration")Long checkpointDuration);

    List<EngineJobCheckpoint> listByTaskIdAndRangeTime(@Param("taskId") String taskEngineId,
                                                       @Param("triggerStart") Long triggerStart,
                                                       @Param("triggerEnd") Long triggerEnd);

    List<EngineJobCheckpoint> listFailedByTaskIdAndRangeTime(@Param("taskId") String taskEngineId,
                                                             @Param("triggerStart") Long triggerStart,
                                                             @Param("triggerEnd") Long triggerEnd, @Param("size") Integer size);

    void updateFailedCheckpoint(@Param("checkPointList") List<EngineJobCheckpoint> checkPointList);

    EngineJobCheckpoint findLatestSavepointByTaskId(@Param("taskId") String taskEngineId);

    EngineJobCheckpoint getByTaskIdAndEngineTaskId(@Param("taskId") String taskId, @Param("taskEngineId") String taskEngineId);

    List<EngineJobCheckpoint> getByTaskEngineIdAndCheckpointIndexAndCount(@Param("taskEngineId") String taskEngineId,
                                                                          @Param("taskId") String taskId,
                                                                          @Param("startIndex") int startIndex,
                                                                          @Param("count") int count);

    void cleanAllCheckpointByTaskEngineId(@Param("taskEngineId")  String taskEngineId);

    EngineJobCheckpoint getByTaskId(@Param("taskId") String taskId);

    Integer deleteByTaskId(@Param("taskId") String taskId);

    List<EngineJobCheckpoint> listByTaskIdAndLimit(@Param("id") Long id, @Param("taskId") String taskId, @Param("limit") Integer limit,@Param("isE")Boolean isE);

    Long getMinIdByTaskId(@Param("taskId") String taskId);

    Integer deleteByEngineJobId(@Param("ids") List<Long> ids);

    Long getTotalSize(@Param("taskId") String taskId);
}
