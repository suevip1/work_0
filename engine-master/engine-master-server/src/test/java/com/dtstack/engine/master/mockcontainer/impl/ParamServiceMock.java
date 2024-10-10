package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.api.dto.ConsoleParamBO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.vo.console.param.ConsoleParamVO;
import com.dtstack.engine.dao.ConsoleCalenderDao;
import com.dtstack.engine.dao.ConsoleCalenderTimeDao;
import com.dtstack.engine.dao.ConsoleParamDao;
import com.dtstack.engine.dao.ScheduleTaskParamDao;
import com.dtstack.engine.master.mapstruct.ParamStruct;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.po.ConsoleCalender;
import com.dtstack.engine.po.ConsoleCalenderTime;
import com.dtstack.engine.po.ScheduleTaskParam;
import com.dtstack.schedule.common.enums.EParamType;
import org.apache.ibatis.annotations.Param;
import org.assertj.core.util.Lists;

import java.util.ArrayList;
import java.util.List;

public class ParamServiceMock extends BaseMock {

    @MockInvoke(targetClass = ConsoleCalenderDao.class)
    int remove(Long calenderId) {
        return 1;
    }
    @MockInvoke(targetClass = ConsoleCalenderTimeDao.class,targetMethod = "delete")
    void delete1(@Param("calenderId") Long calenderId){

    }

    @MockInvoke(targetClass = ParamStruct.class)
    ConsoleParamBO toBO(ConsoleParam consoleParam) {
        return new ConsoleParamBO();
    }

    @MockInvoke(targetClass = ParamStruct.class)
    ConsoleParam toEntity(ConsoleParamVO consoleParamVO) {
        ConsoleParam param = new ConsoleParam();
        param.setId(consoleParamVO.getId());
        param.setParamType(consoleParamVO.getParamType());
        return param;
    }

    @MockInvoke(targetClass = ConsoleParamDao.class)
    void delete(@Param("paramId") Long paramId) {
        return;
    }

    @MockInvoke(targetClass = ConsoleParamDao.class)
    int selectCount() {
        return 1;
    }

    @MockInvoke(targetClass = ConsoleParamDao.class)
    List<ConsoleParam> pageQuery(PageQuery query) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ConsoleParamDao.class)
    public List<ConsoleParam> selectSysParam() {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ConsoleParamDao.class)
    public ConsoleParam selectByName(String paramName) {
        ConsoleParam consoleParam = new ConsoleParam();
        consoleParam.setId(1L);
        consoleParam.setCalenderId(1L);
        consoleParam.setParamType(EParamType.GLOBAL_PARAM_BASE_TIME.getType());
        consoleParam.setParamName("test");
        consoleParam.setParamValue("yyyy-MM-dd");
        consoleParam.setDateFormat("yyyyMMdd");
        consoleParam.setDateBenchmark(2);
        return consoleParam;
    }

    @MockInvoke(targetClass = ConsoleParamDao.class)
    void updateById(ConsoleParam consoleParam) {
        return;
    }

    @MockInvoke(targetClass = ConsoleParamDao.class)
    List<ConsoleParam> selectByIds(@Param("paramIds") List<Long> paramIds) {
        ConsoleParam consoleParam = new ConsoleParam();
        consoleParam.setId(1L);
        consoleParam.setParamName("test");
        return Lists.newArrayList(consoleParam);
    }


    @MockInvoke(targetClass = ConsoleCalenderTimeDao.class)
    List<String> findClosestBusinessDate(@Param("calenderId") Long calenderId, @Param("operator") String operator, @Param("baseDate") String baseDate, @Param("limit") Integer limit) {
        return Lists.newArrayList("20221201");
    }

    @MockInvoke(targetClass = ConsoleCalenderTimeDao.class)
    List<ConsoleCalenderTime> listAfterTime(@Param("calenderId") Long calenderId, @Param("startTime") Long startTime, @Param("endTime") Long endTime) {
        ConsoleCalenderTime consoleCalenderTime = new ConsoleCalenderTime();
        consoleCalenderTime.setCalenderTime("202212011000");
        consoleCalenderTime.setId(1L);
        consoleCalenderTime.setCalenderId(1L);
        return Lists.newArrayList(consoleCalenderTime);
    }

    @MockInvoke(targetClass = ConsoleCalenderTimeDao.class, targetMethod = "insertBatch")
    void insertConsoleBatch(@Param("times") List<ConsoleCalenderTime> consoleCalenderTime) {
        return;
    }

    @MockInvoke(targetClass = ConsoleCalenderDao.class)
    void insert(@Param("calender") ConsoleCalender consoleCalender) {
        return;
    }

    @MockInvoke(targetClass = ConsoleParamDao.class)
    int updateCalenderIdByPrimaryKey(@Param("calenderId") Long calenderId, @Param("id") Long id) {
        return 1;
    }

    @MockInvoke(targetClass = ConsoleParamDao.class)
    ConsoleParam selectById(@Param("paramId") Long paramId) {
        if (1 == paramId) {
            ConsoleParam consoleParam = new ConsoleParam();
            consoleParam.setId(paramId);
            consoleParam.setCalenderId(1L);
            consoleParam.setDateFormat("yyyyMMdd");
            consoleParam.setParamName("test");
            return consoleParam;
        }
        return null;
    }

    @MockInvoke(targetClass = ConsoleCalenderTimeDao.class)
    List<ConsoleCalenderTime> getByCalenderId(@Param("calenderId") Long calenderId, @Param("size") int size, @Param("startTime") Long startTime) {
        ConsoleCalenderTime consoleCalenderTime = new ConsoleCalenderTime();
        consoleCalenderTime.setCalenderTime("202212011000");
        consoleCalenderTime.setId(1L);
        consoleCalenderTime.setCalenderId(1L);
        return Lists.newArrayList(consoleCalenderTime);
    }

    @MockInvoke(targetClass = ConsoleCalenderDao.class, targetMethod = "selectById")
    ConsoleCalender selectByConsoleId(@Param("paramId") Long paramId) {
        if (1 == paramId) {
            ConsoleCalender consoleCalender = new ConsoleCalender();
            consoleCalender.setCalenderName("test");
            consoleCalender.setLatestCalenderTime("2022-12-01");
            consoleCalender.setCalenderFileName("test.csv");
            return consoleCalender;
        }
        return null;
    }

    @MockInvoke(targetClass = ScheduleTaskParamDao.class)
    void insertBatch(@Param("scheduleTaskParams") List<ScheduleTaskParam> scheduleTaskParams) {
        return;
    }

    @MockInvoke(targetClass = ScheduleTaskParamDao.class)
    void deleteByTaskIds(@Param("taskIds") List<Long> taskIds, @Param("appType") int appType) {
        return;
    }

    @MockInvoke(targetClass = ScheduleTaskParamDao.class)
    int findTotalTasks(@Param("paramId") Long paramId) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleTaskParamDao.class)
    List<ScheduleTaskParam> findTask(@Param("paramId") Long paramId, @Param("limit") int limit) {
        ScheduleTaskParam scheduleTaskParam = new ScheduleTaskParam();
        scheduleTaskParam.setParamId(1L);
        return Lists.newArrayList(scheduleTaskParam);
    }

    @MockInvoke(targetClass = ScheduleTaskParamDao.class)
    List<ScheduleTaskParam> findByTaskId(@Param("taskId") Long taskId, @Param("appType") int appType) {
        ScheduleTaskParam scheduleTaskParam = new ScheduleTaskParam();
        scheduleTaskParam.setTaskId(taskId);
        scheduleTaskParam.setAppType(appType);
        scheduleTaskParam.setParamId(1L);
        return Lists.newArrayList(scheduleTaskParam);
    }

}
