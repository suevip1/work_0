package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.dto.ScheduleJobJobTaskDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleJobJobDao {

    List<ScheduleJobJob> listByJobKey(@Param("jobKey") String jobKey,@Param("relyType") Integer relyType);

    List<ScheduleJobJob> listByJobKeys(@Param("list") Collection<String> jobKeys,@Param("relyType") Integer relyType);

    List<ScheduleJobJob> listByParentJobKey(@Param("jobKey") String jobKey,@Param("relyType") Integer relyType);

    Integer insert(ScheduleJobJob scheduleJobJob);

    Integer batchInsert(Collection batchJobJobs);

    Integer update(ScheduleJobJob scheduleJobJob);

    List<ScheduleJobJob> listByParentJobKeys(@Param("list") Collection<String> list);

    List<ScheduleJobJobTaskDTO> listByParentJobKeysWithOutSelfTask(@Param("jobKeyList") List<String> jobKeyList);

    List<ScheduleJobJobTaskDTO> listByJobKeysWithOutSelfTask(@Param("jobKeyList") List<String> jobKeys);

    void deleteByJobKey(@Param("jobKeyList") List<String> jobKeyList);
    void deleteByParentJobKey(@Param("jobKeyList") List<String> jobKeyList);

    Integer removeRely(@Param("relyJobKey") String relyJobKey, @Param("parentKeys") List<String> parentKeys, @Param("relyType") Integer relyType,@Param("oldRelyType") Integer oldRelyType);
}
