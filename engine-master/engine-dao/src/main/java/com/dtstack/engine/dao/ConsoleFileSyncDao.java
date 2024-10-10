package com.dtstack.engine.dao;

import com.dtstack.engine.po.ConsoleFileSync;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @date 2021-12-29 14:40
 */
public interface ConsoleFileSyncDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ConsoleFileSync record);

    int insertSelective(ConsoleFileSync record);

    ConsoleFileSync selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ConsoleFileSync record);

    int updateByPrimaryKey(ConsoleFileSync record);

    ConsoleFileSync getByClusterId(Long clusterId);

    List<ConsoleFileSync> listSyncDirectories();

    void modifyMd5(@Param("clusterId") Long clusterId, @Param("md5") String md5);

    int removeByClusterId(@Param("clusterId") Long clusterId);
}