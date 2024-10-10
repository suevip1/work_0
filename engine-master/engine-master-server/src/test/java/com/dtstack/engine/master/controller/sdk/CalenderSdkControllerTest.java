package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.param.CalenderParam;
import com.dtstack.engine.api.vo.calender.ConsoleCalenderVO;
import com.dtstack.engine.api.vo.calender.TaskCalenderVO;
import com.dtstack.engine.dto.CalenderTaskDTO;
import com.dtstack.engine.master.impl.CalenderService;
import com.dtstack.engine.master.mapstruct.CalenderStruct;
import com.dtstack.engine.po.ConsoleCalender;
import com.dtstack.engine.po.ConsoleCalenderTime;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@MockWith(CalendarControllerServiceMock.class)
public class CalenderSdkControllerTest {

    CalenderSdkController calenderSdkController = new CalenderSdkController();

    @Test
    public void testGetAllCalender() throws Exception {
        CalenderParam calenderParam = new CalenderParam();
        calenderParam.setPageSize(1);
        calenderParam.setCurrentPage(1);
        calenderSdkController.getAllCalender(calenderParam);
    }

    @Test
    public void testOverview() throws Exception {
        calenderSdkController.overview(new CalenderParam());
    }

    @Test
    public void testQueryByName() throws Exception {
        CalenderParam calenderParam = new CalenderParam();
        calenderParam.setCalenderName("1");
        calenderSdkController.queryByName(calenderParam);
    }

    @Test
    public void testQueryTaskCalender() throws Exception {
        CalenderParam calenderParam = new CalenderParam();
        calenderParam.setAppType(1);
        calenderParam.setTaskIds(Lists.newArrayList(1L));
        calenderSdkController.queryTaskCalender(calenderParam);
    }

    @Test
    public void testFindById() throws Exception {
        calenderSdkController.findById(new CalenderParam());
    }
}

class CalendarControllerServiceMock {
    @MockInvoke(targetClass = CalenderStruct.class)
    ConsoleCalenderVO toVo(ConsoleCalender consoleCalender) {
        ConsoleCalenderVO consoleCalenderVO = new ConsoleCalenderVO();
        consoleCalenderVO.setCalenderName(consoleCalender.getCalenderName());
        return consoleCalenderVO;
    }

    @MockInvoke(targetClass = CalenderStruct.class)
    List<TaskCalenderVO> toTaskCalenderVO(List<CalenderTaskDTO> calenderByTasks) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = CalenderStruct.class)
    List<ConsoleCalenderVO> toVOs(List<ConsoleCalender> consoleCalenders) {
        return consoleCalenders.stream().map(consoleCalender -> {
            ConsoleCalenderVO consoleCalenderVO = new ConsoleCalenderVO();
            consoleCalenderVO.setCalenderName(consoleCalender.getCalenderName());
            return consoleCalenderVO;
        }).collect(Collectors.toList());
    }

    @MockInvoke(targetClass = CalenderService.class)
    public ConsoleCalender findByName(String name) {
        ConsoleCalender consoleCalender = new ConsoleCalender();
        consoleCalender.setId(1L);
        consoleCalender.setCalenderName("test");
        return consoleCalender;
    }

    @MockInvoke(targetClass = CalenderService.class)
    public ConsoleCalender findById(long calenderId) {
        ConsoleCalender consoleCalender = new ConsoleCalender();
        consoleCalender.setId(calenderId);
        consoleCalender.setCalenderName("test");
        return consoleCalender;
    }

    @MockInvoke(targetClass = CalenderService.class)
    public List<ConsoleCalenderTime> getTimeByCalenderId(Long calenderId, int size, Long afterTime) {
        ConsoleCalenderTime consoleCalenderTime = new ConsoleCalenderTime();
        consoleCalenderTime.setCalenderTime("202207151200");
        consoleCalenderTime.setCalenderId(calenderId);
        return Lists.newArrayList(consoleCalenderTime);

    }

    @MockInvoke(targetClass = CalenderService.class)
    public List<CalenderTaskDTO> getCalenderByTasks(List<Long> taskIds, int appType) {
        return new ArrayList<>();

    }

    @MockInvoke(targetClass = CalenderService.class)
    public int generalCount() {
        return 1;
    }

    @MockInvoke(targetClass = CalenderService.class)
    public List<ConsoleCalender> pageQuery(PageQuery query) {
        ConsoleCalender consoleCalender = new ConsoleCalender();
        consoleCalender.setCalenderName("test");
        return Lists.newArrayList(consoleCalender);
    }
}