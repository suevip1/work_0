package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.api.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConsoleParamDao {

    void insert(@Param("param") ConsoleParam consoleParam);

    void updateById(ConsoleParam consoleParam);

    void delete(@Param("paramId") Long paramId);

    ConsoleParam selectById(@Param("paramId") Long paramId);

    List<ConsoleParam> selectByIds(@Param("paramIds") List<Long> paramIds);

    List<ConsoleParam> pageQuery(PageQuery query);

    int selectCount();

    ConsoleParam selectByName(@Param("paramName")String paramName);

    List<ConsoleParam> selectSysParam();

    int updateCalenderIdByPrimaryKey(@Param("calenderId")Long calenderId, @Param("id")Long id);

    List<String> listByTypes(@Param("types")List<Integer> types);
}
