package com.dtstack.engine.dao;

import com.dtstack.engine.api.dto.ScheduleProjectParamDTO;
import com.dtstack.engine.po.ScheduleProjectParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-12-05 21:13
 */
public interface ScheduleProjectParamDao {
    int deleteByPrimaryKey(Long id);

    int insert(ScheduleProjectParam record);

    int insertSelective(ScheduleProjectParam record);

    ScheduleProjectParam selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ScheduleProjectParam record);

    int updateByPrimaryKey(ScheduleProjectParam record);

    int modifyById(ScheduleProjectParam record);

    int batchInsert(@Param("list") List<ScheduleProjectParam> list);

    ScheduleProjectParam findByProjectAndName(@Param("projectId")Long projectId, @Param("paramName")String paramName);

    Boolean removeBatch(@Param("ids")List<Long> ids);

    List<ScheduleProjectParam> list(@Param("queryDTO") ScheduleProjectParamDTO queryDTO);

    int count(@Param("queryDTO") ScheduleProjectParamDTO queryDTO);

    List<ScheduleProjectParam> findByIds(@Param("ids") List<Long> ids);

    List<ScheduleProjectParam> findByProjectId(@Param("projectId")Long projectId);

    List<String> listParamNamesByProjectId(@Param("projectId")Long projectId);
}