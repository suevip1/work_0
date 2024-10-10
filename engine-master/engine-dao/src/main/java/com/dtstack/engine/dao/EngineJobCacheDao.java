package com.dtstack.engine.dao;

import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.dto.GroupOverviewDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/12
 */
public interface EngineJobCacheDao {

    int insert(@Param("jobId")String jobId, @Param("engineType") String engineType,
               @Param("computeType") Integer computeType, @Param("stage") int stage,
               @Param("jobInfo")String jobInfo, @Param("nodeAddress") String nodeAddress,
               @Param("jobName") String jobName, @Param("jobPriority") Long jobPriority, @Param("jobResource") String jobResource,
               @Param("tenantId")Long tenantId, @Param("taskName") String taskName
               );

    int delete(@Param("jobId")String jobId);

    EngineJobCache getOne(@Param("jobId")String jobId);

    int updateStage(@Param("jobId") String jobId, @Param("stage") Integer stage,@Param("nodeAddress") String nodeAddress, @Param("jobPriority") Long jobPriority, @Param("waitReason") String waitReason);

    int updateTaskName(@Param("jobId") String jobId, @Param("taskName") String taskName);

    int updateStageBatch(@Param("jobIds") List<String> jobIds, @Param("stage") Integer stage,@Param("nodeAddress") String nodeAddress);

    List<EngineJobCache> listByStage(@Param("startId") Long id,
                                     @Param("nodeAddress") String nodeAddress,
                                     @Param("stage") Integer stage,
                                     @Param("tenantIds") List<Long> tenantIds,
                                     @Param("jobResource") String jobResource);

    List<String> getJobIdWithTaskNameIsNull();

    int countLessByStage(@Param("startId") Long id, @Param("nodeAddress") String nodeAddress, @Param("jobResource") String jobResource,@Param("stages") List<Integer> stages);

    List<EngineJobCache> getByJobIds(@Param("jobIds") List<String> jobIds);

    List<String> listNames(@Param("jobName") String jobName);

    int countByStage(@Param("jobResource") String jobResource, @Param("stages") List<Integer> stages, @Param("nodeAddress") String nodeAddress);

    Long minPriorityByStage(@Param("jobResource") String jobResource, @Param("stages") List<Integer> stages, @Param("nodeAddress") String nodeAddress);

    List<String> getAllNodeAddress();

    Integer updateNodeAddressFailover(@Param("nodeAddress") String nodeAddress, @Param("jobIds") List<String> ids, @Param("stage") Integer stage);

    List<EngineJobCache> listByFailover(@Param("startId") Long id, @Param("nodeAddress") String nodeAddress, @Param("stage") Integer stage);

    List<String> getJobResources();

    List<Map<String,Object>> groupByJobResource(@Param("nodeAddress") String nodeAddress);

    List<Map<String,Object>> groupByJobResourceFilterByCluster(@Param("nodeAddress") String nodeAddress, @Param("clusterName") String clusterName,@Param("tenantId")Long tenantId);

    List<GroupOverviewDTO> getGroupOverviews(@Param("nodeAddress") String nodeAddress, @Param("tenantIds") List<Long> tenantIds);

    Long countByJobResource(@Param("jobResource") String jobResource,
                            @Param("stage") Integer stage,
                            @Param("nodeAddress") String nodeAddress,
                            @Param("projectId") Long projectId,
                            @Param("taskName") String taskName,
                            @Param("tenantIds") List<Long> tenantIds);

    List<EngineJobCache> listByJobResource(@Param("jobResource") String jobResource,
                                           @Param("stage") Integer stage,
                                           @Param("nodeAddress") String nodeAddress,
                                           @Param("start") Integer start,
                                           @Param("pageSize") Integer pageSize,
                                           @Param("projectId") Long projectId,
                                           @Param("taskName") String taskName,
                                           @Param("tenantIds") List<Long> tenantIds);

    Integer deleteByJobIds(@Param("jobIds") List<String> jobIds);

    EngineJobCache getOneByJobResource(@Param("jobResource") String jobResource);

    Integer updateJobInfo(@Param("jobInfo") String jobInfo, @Param("jobId") String jobId);

    List<EngineJobCache> findByJobResourceNoTenant(@Param("jobResource") String jobResource, @Param("tenantIds") List<Long> tenantIds);

    List<String> listJobIdByJobResourceAndNodeAddressAndStages(@Param("jobResource") String jobResource,
                                                               @Param("nodeAddress") String nodeAddress,
                                                               @Param("stage") Integer stage);

    List<EngineJobCache> getAllCache(@Param("stage") Integer stage, @Param("jobResource") String jobResource, @Param("aliveNodes") List<String> aliveNodes,@Param("minPriority") Long minPriority,@Param("maxPriority") Long maxPriority );

    List<String> getAllJobResource(@Param("localAddress") String localAddress);

    Long getMaxCurrentPriority();
}
