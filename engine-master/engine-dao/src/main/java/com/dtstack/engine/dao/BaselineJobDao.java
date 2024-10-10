package com.dtstack.engine.dao;

import com.dtstack.engine.po.BaselineJobConditionModel;
import com.dtstack.engine.po.BaselineView;
import com.dtstack.engine.po.BaselineJob;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2022/5/11 2:52 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface BaselineJobDao {

    Integer insert(BaselineJob baselineJob);

    List<BaselineJob> scanningBaselineJobById(@Param("startId") Long startId,
                                              @Param("businessDate") Timestamp businessDate,
                                              @Param("baselineStatues") List<Integer> baselineStatues,
                                              @Param("finishStatus") Integer finishStatus,
                                              @Param("limit") Integer limit);

    Integer updateFinishStatus(@Param("id") Long id,
                               @Param("finishStatus") Integer finishStatus,
                               @Param("finishTime") Timestamp finishTime,
                               @Param("baselineStatus") Integer baselineStatus);

    Long countByModel(@Param("model") BaselineJobConditionModel model);

    List<BaselineJob> selectByModel(@Param("model") BaselineJobConditionModel model);

    List<BaselineView> selectBaselineJobGraph(@Param("baselineTaskId") Long baselineTaskId, @Param("time") Long time);

    List<BaselineJob> selectByBusinessDate(@Param("timestamp") Timestamp timestamp, @Param("baselineTaskId") Long baselineTaskId);

    List<BaselineJob> selectByIds(@Param("ids") Set<Long> ids);

    Integer deleteByIds(@Param("ids") Set<Long> ids);
}
