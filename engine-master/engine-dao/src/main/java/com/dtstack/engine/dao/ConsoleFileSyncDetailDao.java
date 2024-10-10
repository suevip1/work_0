package com.dtstack.engine.dao;

import com.dtstack.engine.po.ConsoleFileSyncDetail;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @date 2021-12-30 11:43
 */
public interface ConsoleFileSyncDetailDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ConsoleFileSyncDetail record);

    int insertSelective(ConsoleFileSyncDetail record);

    ConsoleFileSyncDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ConsoleFileSyncDetail record);

    int updateByPrimaryKey(ConsoleFileSyncDetail record);

    int batchSave(@Param("list") List<ConsoleFileSyncDetail> list);

    int deleteByClusterId(Long clusterId);

    List<ConsoleFileSyncDetail> listByClusterId(Long clusterId);

    List<ConsoleFileSyncDetail> listSyncFilesByClusterId(@Param("clusterIds") List<Long> clusterIds);

    void modifySyncTime(@Param("clusterId") Long clusterId, @Param("fileNames") List<String> fileNames);
}