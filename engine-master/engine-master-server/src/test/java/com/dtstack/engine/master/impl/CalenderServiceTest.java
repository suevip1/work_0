package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.master.mockcontainer.impl.CalenderServiceTestMock;
import com.google.common.collect.Lists;
import org.junit.Test;

@MockWith(CalenderServiceTestMock.class)
public class CalenderServiceTest {

    private CalenderService calenderService = new CalenderService();

//    @Test
//    public void addOrUpdateCalenderExcel(){
//        URL resource = getClass().getClassLoader().getResource("csv/calendar.csv");
//        File file = new File(resource.getFile());
//        calenderService.addOrUpdateCalenderExcel(file,"test",null);
//    }

    @Test
    public void listTaskByCalender(){
        calenderService.listTaskByCalender(1L,new PageQuery<>());
    }

    @Test
    public void pageQuery(){
        calenderService.pageQuery(new PageQuery<>());
    }

    @Test
    public void deleteById(){
        calenderService.deleteById(2L);
    }
//    @Test
//    public void addOrUpdateTaskCalender(){
//        calenderService.addOrUpdateTaskCalender(1L,1,1L);
//    }

    @Test
    public void findByName(){
        calenderService.findByName("test");
    }

    @Test
    public void deleteTask(){
        calenderService.deleteTask(Lists.newArrayList(1L),1);
    }
}
