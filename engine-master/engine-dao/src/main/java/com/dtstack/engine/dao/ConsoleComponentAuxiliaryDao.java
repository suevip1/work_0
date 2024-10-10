package com.dtstack.engine.dao;

import com.dtstack.engine.po.ConsoleComponentAuxiliary;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-04-14 20:13
 */
public interface ConsoleComponentAuxiliaryDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ConsoleComponentAuxiliary record);

    int insertSelective(ConsoleComponentAuxiliary record);

    ConsoleComponentAuxiliary selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ConsoleComponentAuxiliary record);

    int updateByPrimaryKey(ConsoleComponentAuxiliary record);

    int batchInsert(@Param("list") List<ConsoleComponentAuxiliary> list);

    ConsoleComponentAuxiliary queryByClusterAndComponentAndType(@Param("clusterId") Long clusterId, @Param("componentTypeCode") Integer componentTypeCode);

    List<ConsoleComponentAuxiliary> queryByClusterAndComponentCodeAndType(@Param("clusterId") Long clusterId, @Param("componentTypeCode") Integer componentTypeCode, @Param("types") List<String> types);

    ConsoleComponentAuxiliary queryByClusterAndComponentAndTypeAndSwitch(@Param("clusterId") Long clusterId, @Param("componentTypeCode") Integer componentTypeCode, @Param("aSwitch") Integer aSwitch, @Param("type") String type);

    boolean switchAuxiliary(@Param("clusterId") Long clusterId, @Param("componentTypeCode") Integer componentTypeCode, @Param("type") String type, @Param("aSwitch") Integer aSwitch);

    int removeByClusterAndComponentAndType(@Param("clusterId") Long clusterId, @Param("componentTypeCode") Integer componentTypeCode, @Param("type") String type);
}