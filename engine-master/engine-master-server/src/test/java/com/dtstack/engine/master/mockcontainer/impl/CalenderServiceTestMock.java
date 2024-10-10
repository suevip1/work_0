package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.vo.calender.CalenderTaskVO;
import com.dtstack.engine.dao.ConsoleCalenderDao;
import com.dtstack.engine.dao.ConsoleCalenderTimeDao;
import com.dtstack.engine.dao.ScheduleTaskCalenderDao;
import com.dtstack.engine.dto.CalenderTaskDTO;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.mapstruct.CalenderStruct;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.po.ConsoleCalender;
import com.dtstack.engine.po.ConsoleCalenderTime;
import com.dtstack.engine.po.ScheduleTaskCalender;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantDeletedVO;
import org.assertj.core.util.Lists;

import java.util.*;

public class CalenderServiceTestMock extends BaseMock {

    @MockInvoke(targetClass = ConsoleCalenderDao.class)
    ConsoleCalender selectByName(String calenderName) {
        return null;
    }


    @MockInvoke(targetClass = ConsoleCalenderTimeDao.class)
    void insertBatch( List<ConsoleCalenderTime> consoleCalenderTime) {
        return;
    }

    @MockInvoke(targetClass = ConsoleCalenderTimeDao.class, targetMethod = "delete")
    void deleteTime(Long calenderId) {
        return;
    }

    @MockInvoke(targetClass = ConsoleCalenderDao.class)
    void delete(Long calenderId) {
        return;
    }

    @MockInvoke(targetClass = ScheduleTaskCalenderDao.class)
    int findTotalTasks( Long calenderId) {
        if(calenderId == 2L){
            return 0;
        }
        return 1;
    }

    @MockInvoke(targetClass = ScheduleTaskCalenderDao.class)
    ScheduleTaskCalender findByTaskId(Long taskId, int appType){
        ScheduleTaskCalender scheduleTaskCalender  =new ScheduleTaskCalender();
        scheduleTaskCalender.setTaskId(1L);
        scheduleTaskCalender.setAppType(1);
        scheduleTaskCalender.setCalenderId(2L);
        return scheduleTaskCalender;
    }
    
    @MockInvoke(targetClass = ScheduleTaskCalenderDao.class,targetMethod = "insertBatch")
    void taskInsertBatch( List<ScheduleTaskCalender> scheduleTaskTimes){
        
    }
    @MockInvoke(targetClass = ScheduleTaskCalenderDao.class)
    void deleteByTaskIds(List<Long> taskIds, int appType){
             
    }
    @MockInvoke(targetClass = ScheduleTaskCalenderDao.class)
    List<CalenderTaskDTO> pageQuery(PageQuery query) {
        CalenderTaskDTO calenderTaskDTO = new CalenderTaskDTO();
        calenderTaskDTO.setCalenderId(1L);
        calenderTaskDTO.setTaskId(1L);
        calenderTaskDTO.setAppType(1);
        calenderTaskDTO.setProjectId(1L);
        return Lists.newArrayList(calenderTaskDTO);
    }

    @MockInvoke(targetClass = ConsoleCalenderDao.class,targetMethod = "pageQuery")
    List<ConsoleCalender> consolePageQuery(PageQuery query){
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ConsoleCalenderDao.class)
    void insert(ConsoleCalender consoleCalender) {
        return;
    }

    @MockInvoke(targetClass = CalenderStruct.class)
    CalenderTaskVO toTaskVO(CalenderTaskDTO calenderTaskDTO) {
        return new CalenderTaskVO();
    }

    @MockInvoke(targetClass = TenantService.class)
    public Map<Long, TenantDeletedVO> listAllTenantByDtUicTenantIds(Collection<Long> ids) {
        return new HashMap<>();
    }

    @MockInvoke(targetClass = ConsoleCalenderDao.class)
    int selectCount() {
        return 1;
    }
}
