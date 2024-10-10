package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.calender.CalenderTaskVO;
import com.dtstack.engine.api.vo.calender.ConsoleCalenderVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dto.CalenderTaskDTO;
import com.dtstack.engine.master.impl.CalenderService;
import com.dtstack.engine.master.impl.RoleService;
import com.dtstack.engine.master.mapstruct.CalenderStruct;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.engine.po.ConsoleCalender;
import com.dtstack.engine.po.ConsoleCalenderTime;
import com.dtstack.engine.po.ScheduleTaskCalender;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@MockWith(CalenderControllerTest.Mock.class)
public class CalenderControllerTest {

    private CalenderController calenderController = new CalenderController();

    public static class Mock {
        @MockInvoke(targetClass = CalenderService.class)
        public List<ConsoleCalender> pageQuery(PageQuery query) {
            ConsoleCalender consoleCalender = new ConsoleCalender();
            consoleCalender.setId(1L);
            return Lists.newArrayList(consoleCalender);
        }

        @MockInvoke(targetClass = CalenderService.class)
        public int generalCount() {
            return 1;
        }

        @MockInvoke(targetClass = CalenderService.class)
        public List<CalenderTaskDTO> getTaskNameByCalenderId(List<Long> calenderIds) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = CalenderStruct.class)
        public List<ConsoleCalenderVO> toVOs(List<ConsoleCalender> consoleCalenders) {
            ConsoleCalenderVO consoleCalenderVO = new ConsoleCalenderVO();
            consoleCalenderVO.setLatestCalenderTime(System.currentTimeMillis());
            return Lists.newArrayList(consoleCalenderVO);
        }

        @MockInvoke(targetClass = SessionUtil.class)
        public <T> T getUser(String token, Class<T> clazz) {
            return (T)new UserDTO();
        }

        @MockInvoke(targetClass = RoleService.class)
        public boolean checkIsSysAdmin(UserDTO user) {
            return true;
        }

        @MockInvoke(targetClass = CalenderService.class)
        public void deleteById(long calenderId) {

        }

        @MockInvoke(targetClass = CalenderService.class)
        public PageResult<List<CalenderTaskVO>> listTaskByCalender(long calenderId, PageQuery<ScheduleTaskCalender> pageQuery) {
            return PageResult.EMPTY_PAGE_RESULT;
        }

        @MockInvoke(targetClass = EnvironmentContext.class)
        public int getMaxExcelSize() {
            return 1;
        }

        @MockInvoke(targetClass = CalenderService.class)
        public List<ConsoleCalenderTime> getTimeByCalenderId(Long calenderId, int size, Long afterTime) {
            return new ArrayList<>();
        }
        }

    @Test
    public void getCalenders() throws Exception {
        calenderController.getCalenders(1,1,"1");
    }

    @Test
    public void deleteCalender() throws Exception {
        calenderController.deleteCalender(1L,"");
    }

    @Test
    public void listTaskByCalender() throws Exception {
        calenderController.listTaskByCalender(1L,1,1);
    }

    @Test
    public void getCalenderTime() throws Exception {
        calenderController.getCalenderTime(1L);
    }
}