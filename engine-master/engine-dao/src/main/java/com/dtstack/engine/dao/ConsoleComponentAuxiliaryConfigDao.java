package com.dtstack.engine.dao;

import com.dtstack.engine.po.ConsoleComponentAuxiliaryConfig;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-04-14 20:13
 */
public interface ConsoleComponentAuxiliaryConfigDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ConsoleComponentAuxiliaryConfig record);

    int insertSelective(ConsoleComponentAuxiliaryConfig record);

    ConsoleComponentAuxiliaryConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ConsoleComponentAuxiliaryConfig record);

    int updateByPrimaryKey(ConsoleComponentAuxiliaryConfig record);

    int batchInsert(@Param("list") List<ConsoleComponentAuxiliaryConfig> list);

    int batchSave(@Param("list") List<ConsoleComponentAuxiliaryConfig> list);

    int removeByAuxiliaryId(@Param("auxiliaryId") Integer auxiliaryId);

    List<ConsoleComponentAuxiliaryConfig> listByAuxiliaryId(@Param("auxiliaryId") Integer auxiliaryId);

    int removeByClusterAndComponentAndType(@Param("clusterId") Long clusterId, @Param("componentTypeCode") Integer componentTypeCode,  @Param("type") String type);
}