package com.dtstack.engine.dao;

import com.dtstack.engine.po.ConsoleCalender;
import com.dtstack.engine.api.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConsoleCalenderDao {

    void insert(@Param("calender") ConsoleCalender consoleCalender);

    void updateById(ConsoleCalender consoleCalender);

    void delete(@Param("calenderId") Long calenderId);

    ConsoleCalender selectByName(@Param("calenderName") String calenderName);

    ConsoleCalender selectById(@Param("calenderId") Long calenderId);

    List<ConsoleCalender> pageQuery(PageQuery query);

    int selectCount();

    int remove(Long calenderId);
}
