package com.dtstack.engine.dao;

import com.dtstack.engine.po.BaselineTaskConditionModel;
import com.dtstack.engine.po.BaselineTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/10 10:57 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface BaselineTaskDao {

    /**
     * 获得计数
     */
    Long countByModel(@Param("model") BaselineTaskConditionModel model);


    List<BaselineTask> selectByModel(@Param("model") BaselineTaskConditionModel model);

    BaselineTask selectByPrimaryKey(@Param("id") Long id);

    List<BaselineTask> selectRangeIdOfLimit(@Param("startId") Long startId, @Param("openStatus") Integer openStatus, @Param("limit") Integer limit);

    List<BaselineTask> selectByIds(@Param("ids") List<Long> ids);

    String selectByName(@Param("name") String name,@Param("projectId") Long projectId);

    Integer insert(BaselineTask baselineTask);

    Integer updateByPrimaryKeySelective(BaselineTask baselineTask);

    Integer deleteById(@Param("id") Long id);

    Integer deleteBaselineTaskByProjectId(@Param("projectId") Long projectId,@Param("appType") Integer appType);

    Integer deleteByIds(@Param("ids") List<Long> ids);

    Integer updateOwnerByOwnerIds(@Param("oldOwnerUserIds") List<Long> oldOwnerUserIds, @Param("newOwnerUserId") Long newOwnerUserId);
}
