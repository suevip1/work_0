package com.dtstack.engine.dao;

import com.dtstack.engine.po.ConsoleSSL;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConsoleSSLDao {
    List<ConsoleSSL> getByClusterIdAndComponentTypeAndComponentVersion(
            @Param("clusterId") Long clusterId,
            @Param("componentType") Integer componentType,
            @Param("componentVersion") String componentVersion);
    Integer insert(ConsoleSSL engine);
    Integer update(ConsoleSSL engine);
    Integer delete(@Param("id") Long id);
    Integer deleteByComponent(@Param("clusterId") Long clusterId,
                              @Param("componentType") Integer componentType,
                              @Param("componentVersion") String componentVersion);
}
