package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.vo.console.param.ConsoleCalenderTimeShowVO;
import com.dtstack.engine.api.vo.console.param.ConsoleParamVO;
import com.dtstack.engine.master.impl.ParamService;
import com.dtstack.engine.master.impl.RoleService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.impl.UserService;
import com.dtstack.engine.master.mapstruct.ParamStruct;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.engine.po.ScheduleTaskParam;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@MockWith(ConsoleParamControllerTest.Mock.class)
public class ConsoleParamControllerTest {

    private ConsoleParamController controller = new ConsoleParamController();

    public static class Mock {

        @MockInvoke(targetClass = ParamService.class)
        public List<ConsoleParam> pageQuery(PageQuery query) {
            ConsoleParam consoleParam = new ConsoleParam();
            consoleParam.setCreateUserId(1L);
            return Lists.newArrayList(consoleParam);
        }

        @MockInvoke(targetClass = ParamService.class)
        public int generalCount() {
            return 1;
        }

        @MockInvoke(targetClass = ParamStruct.class)
        List<ConsoleParamVO> toVos(List<ConsoleParam> consoleParams) {
            ConsoleParamVO consoleParamVO = new ConsoleParamVO();
            return Lists.newArrayList(consoleParamVO);
        }

        @MockInvoke(targetClass = UserService.class)
        public List<User> findUserWithFill(Set<Long> userIds) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = SessionUtil.class)
        public <T> T getUser(String token, Class<T> clazz) {
            return (T) new UserDTO();
        }

        @MockInvoke(targetClass = RoleService.class)
        public boolean checkIsSysAdmin(UserDTO user) {
            return true;
        }

        @MockInvoke(targetClass = ParamService.class)
        public int findTotalTask(long paramId) {
            return 0;
        }

        @MockInvoke(targetClass = ParamService.class)
        public void deleteById(long paramId) {

        }

        @MockInvoke(targetClass = ParamService.class)
        public List<ScheduleTaskParam> findTask(long paramId, int limit) {
            ScheduleTaskParam scheduleTaskParam = new ScheduleTaskParam();
            scheduleTaskParam.setTaskId(1L);
            scheduleTaskParam.setAppType(1);
            return Lists.newArrayList(scheduleTaskParam);
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public List<ScheduleTaskShade> findTaskIds(List<Long> taskIds, Integer isDeleted, Integer appType, boolean isSimple) {

            ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
            scheduleTaskShade.setName("ds");
            return Lists.newArrayList(scheduleTaskShade);
        }

        @MockInvoke(targetClass = ParamService.class)
        public void addOrUpdate(ConsoleParamVO consoleParamVO) {

        }

        @MockInvoke(targetClass = ParamService.class)
        public ConsoleCalenderTimeShowVO getTimeByCalenderId(Long paramId) {
            return new ConsoleCalenderTimeShowVO();
        }

        @MockInvoke(targetClass = ParamService.class)
        public void addOrUpdate(ConsoleParamVO consoleParamVO, File csvFile) {
        }
    }

    @Test
    public void getParams() throws Exception {
        controller.getParams(1,1);
    }

    @Test
    public void deleteParam() throws Exception {
        controller.deleteParam(1,"");
    }

    @Test
    public void getParamUseTask() throws Exception {
        controller.getParamUseTask(1L,1);
    }

    @Test
    public void addOrUpdateParam() {
        ConsoleParamVO consoleParamVO = new ConsoleParamVO();
        consoleParamVO.setParamName("test_param");
        consoleParamVO.setParamType(10);
        consoleParamVO.setCalenderId(1L);
        controller.addOrUpdateParam(consoleParamVO,null,"",1L);

        ConsoleParamVO consoleParamVO2 = new ConsoleParamVO();
        consoleParamVO2.setParamName("test_param");
        consoleParamVO2.setParamType(8);
        consoleParamVO2.setCalenderId(1L);
        consoleParamVO2.setParamValue("yyyyMMdd");
        consoleParamVO2.setDateBenchmark(2);
        consoleParamVO2.setDateFormat("yyyyMMdd");
        controller.addOrUpdateParam(consoleParamVO2,null,"",1L);
    }

    @Test
    public void showCalenderTime() throws Exception {
        controller.showCalenderTime(1L);
    }


}