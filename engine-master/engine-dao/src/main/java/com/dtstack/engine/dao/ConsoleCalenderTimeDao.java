package com.dtstack.engine.dao;

import com.dtstack.engine.po.ConsoleCalenderTime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConsoleCalenderTimeDao {

    void insertBatch(@Param("times") List<ConsoleCalenderTime> consoleCalenderTime);

    void delete(@Param("calenderId") Long calenderId);

    List<ConsoleCalenderTime> getByCalenderId(@Param("calenderId") Long calenderId, @Param("size") int size, @Param("startTime") Long startTime);

    ConsoleCalenderTime getByCalenderIdAndCalenderTime(@Param("calenderId") Long calenderId, @Param("calenderTime") String calenderTime);

    List<ConsoleCalenderTime> listAfterTime(@Param("calenderId") Long calenderId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    ConsoleCalenderTime getNearestTime(@Param("calenderId") Long calenderId, @Param("currentTime") Long currentTime, @Param("canEq") boolean canEq);

    List<ConsoleCalenderTime> getNearestOffset(@Param("calenderId") Long calenderId, @Param("currentLong") long currentLong, @Param("canEq") boolean canEq, @Param("symbol") boolean symbol, @Param("limit") Integer limit);
    List<String> findClosestBusinessDate(@Param("calenderId") Long calenderId, @Param("operator") String operator, @Param("baseDate") String baseDate,@Param("limit") Integer limit);

    List<String> findClosestBusinessDateNotEquals(@Param("calenderId") Long calenderId, @Param("operator") String operator, @Param("baseDate") String baseDate,@Param("limit") Integer limit);

}
