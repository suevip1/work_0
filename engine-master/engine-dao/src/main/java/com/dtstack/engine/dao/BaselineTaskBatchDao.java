package com.dtstack.engine.dao;

import com.dtstack.engine.po.BaselineTaskBatch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2023/1/30 3:03 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface BaselineTaskBatchDao {

    List<BaselineTaskBatch> selectByBaselineTaskId(@Param("baselineTaskId") Long baselineTaskId);

    BaselineTaskBatch selectByBaselineTaskIdAndCycTime(@Param("baselineTaskId") Long baselineTaskId, @Param("cycTime") String cycTime);

    List<BaselineTaskBatch> selectByBaselineTaskIds(@Param("baselineTaskIds") List<Long> baselineTaskIds);

    Boolean deleteByBaselineTaskId(@Param("baselineTaskId") Long baselineTaskId);

    Integer batchInsert(@Param("baselineTaskBatches") List<BaselineTaskBatch> baselineTaskBatches);


}
