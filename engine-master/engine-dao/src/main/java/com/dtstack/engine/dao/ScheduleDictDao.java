package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleDict;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author yuebai
 * @date 2021-03-02
 */
public interface ScheduleDictDao {

    List<ScheduleDict> listDictByType(@Param("type") Integer type);

    List<ScheduleDict> listDictByTypes(@Param("types") Collection<Integer> types);

    ScheduleDict getByNameValue(@Param("type") Integer type, @Param("dictName") String dictName, @Param("dictValue") String dictValue,@Param("dependName") String dependName);

    List<ScheduleDict> getByDependName(@Param("type") Integer type,@Param("dependName") String dependName);

    Integer update(@Param("dictCode") String dictCode, @Param("dictName") String dictName, @Param("update") String update, @Param("oldUpdate") String oldUpdate);

    Integer insert(ScheduleDict dict);

    Integer updateValue(ScheduleDict dict);

    List<ScheduleDict> listById(@Param("id") Long id, @Param("size") Integer size);
    ScheduleDict getOne(@Param("id") Long id);

    List<ScheduleDict> listByTypeAndNames(@Param("type")Integer type,@Param("dictNames") List<String> dictNames);
    ScheduleDict listByTypeAndName(@Param("type")Integer type,@Param("dictName") String dictName);
}
