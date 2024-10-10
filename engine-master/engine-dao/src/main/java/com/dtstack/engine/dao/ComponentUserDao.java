package com.dtstack.engine.dao;

import com.dtstack.engine.po.ComponentUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ComponentUserDao {

    ComponentUser getOne(@Param("id") Long id);

    Integer insert(ComponentUser component);

    void  deleteByComponentAndCluster(@Param("clusterId")Long clusterId,@Param("componentTypeCode")Integer componentTypeCode);

    void batchInsert(@Param("addComponentUserList") List<ComponentUser> addComponentUserList);

    List<ComponentUser> getComponentUserByCluster(@Param("clusterId") Long clusterId, @Param("componentTypeCode") Integer componentTypeCode);

    ComponentUser getComponentUser(@Param("clusterId")Long clusterId, @Param("componentTypeCode")Integer componentTypeCode,@Param("label")String label, @Param("userName") String userName);

    int batchUpdate(@Param("componentUserList") List<ComponentUser> componentUserList);

    int deleteByIds(@Param("idList") List<Long> idList);
}

