package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.task.TaskViewRetrunVO;
import com.dtstack.engine.api.vo.task.TaskViewVO;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

@MockWith(ScheduleTaskTaskShadeSdkControllerMock.class)
public class ScheduleTaskTaskShadeSdkControllerTest {
    ScheduleTaskTaskShadeSdkController scheduleTaskTaskShadeSdkController = new ScheduleTaskTaskShadeSdkController();

    @Test
    public void testView() throws Exception {
        scheduleTaskTaskShadeSdkController.view(new TaskViewVO());
    }
}

class ScheduleTaskTaskShadeSdkControllerMock {

    @MockInvoke(targetClass = ScheduleTaskTaskShadeService.class)
    public TaskViewRetrunVO view(TaskViewVO taskViewVO) {
        return null;
    }
}