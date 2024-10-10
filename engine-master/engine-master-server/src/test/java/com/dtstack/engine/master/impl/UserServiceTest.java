package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.vo.JobTopOrderVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobPreViewVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.dto.ScheduleTaskForFillDataDTO;
import com.dtstack.engine.master.mockcontainer.impl.UserServiceMock;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@MockWith(UserServiceMock.class)
public class UserServiceTest {
    UserService userService = new UserService();


    @Test
    public void testFillUser() throws Exception {
        ScheduleTaskVO scheduleTaskVO = new ScheduleTaskVO();
        scheduleTaskVO.setOwnerUserId(1L);
        scheduleTaskVO.setCreateUserId(1L);
        scheduleTaskVO.setModifyUserId(1L);
        userService.fillUser(Collections.singletonList(scheduleTaskVO));
    }


    @Test
    public void testFindByUser() throws Exception {
        User result = userService.findByUser(1L);
        Assert.assertNotNull(result);
    }

    @Test
    public void testFindUserWithFill() throws Exception {
        List<User> result = userService.findUserWithFill(new HashSet<>(Collections.singletonList(1L)));
        Assert.assertNotNull(result);
    }

    @Test
    public void testFillFillDataJobUserName() throws Exception {
        userService.fillFillDataJobUserName(Collections.singletonList(new ScheduleFillDataJobPreViewVO()));
    }

    @Test
    public void testFillTopOrderJobUserName() throws Exception {
        userService.fillTopOrderJobUserName(Collections.singletonList(new JobTopOrderVO()));
    }

    @Test
    public void testFillScheduleTaskForFillDataDTO() throws Exception {
        userService.fillScheduleTaskForFillDataDTO(Collections.singletonList(new ScheduleTaskForFillDataDTO()));
    }

    @Test
    public void testFillScheduleJobVO() throws Exception {
        userService.fillScheduleJobVO(Collections.singletonList(new ScheduleJobVO()));
    }
}