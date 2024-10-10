package com.dtstack.engine.dao;

import com.dtstack.engine.po.ResourceHandOver;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ResourceHandOverDao {

    List<ResourceHandOver> findByProjectIdAndOldResourceIds(@Param("projectId") Long projectId,@Param("appType")Integer appType, @Param("oldResourceIds") List<Long> oldResourceIds);

    void updateTargetResourceIdByProjectIdAndTargetResourceId(@Param("projectId") Long projectId,@Param("appType")Integer appType,
                                                              @Param("oldResourceId") Long oldResourceId,
                                                              @Param("targetResourceId") Long targetResourceId);

    void update(ResourceHandOver resourceHandOver);
    void insert(ResourceHandOver resourceHandOver);

    void deleteByIds(@Param("ids") List<Long> ids);
}
