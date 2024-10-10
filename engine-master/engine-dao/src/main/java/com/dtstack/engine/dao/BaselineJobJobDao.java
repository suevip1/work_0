package com.dtstack.engine.dao;

import com.dtstack.engine.po.BaselineJobJob;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2022/5/11 2:52 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface BaselineJobJobDao {

    Integer batchInsert(@Param("jobJobs") List<BaselineJobJob> jobJobs);

    List<BaselineJobJob> scanningBaselineJobJobByBaselineJobId(@Param("startId") Long startId,
                                                               @Param("baselineJobId") Long baselineJobId,
                                                               @Param("limit") Integer limit);

    List<String> selectJobIdByBaselineJobId(@Param("baselineJobId") Long baselineJobId,
                                            @Param("baselineTaskType") Integer baselineTaskType);

    List<BaselineJobJob> selectBaselineByBaselineJobId(@Param("baselineJobId") Long baselineJobId,
                                            @Param("baselineTaskType") Integer baselineTaskType);

    List<BaselineJobJob> selectBaselineByJobId(@Param("jobId") String jobId);

    Integer deleteByBaselineJobIds(@Param("baselineJobIds") Set<Long> ids);
}
