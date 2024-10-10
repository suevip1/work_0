package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.template.TaskTemplateResultVO;
import com.dtstack.engine.api.vo.template.TaskTemplateVO;
import com.dtstack.engine.master.impl.TaskParamTemplateService;
import org.junit.Test;

import static org.junit.Assert.*;

@MockWith(TaskParamControllerTest.TaskParamControllerMock.class)
public class TaskParamControllerTest {

    private static final TaskParamController taskParamController = new TaskParamController();


    static class TaskParamControllerMock {

        @MockInvoke(targetClass = TaskParamTemplateService.class)
        public TaskTemplateResultVO getEngineParamTmplByComputeType(Integer engineType, Integer computeType, Integer taskType) {
            return new TaskTemplateResultVO();
        }
    }

    @Test
    public void getEngineParamTmplByComputeType() {
        TaskTemplateVO taskTemplateVO = new TaskTemplateVO();
        taskTemplateVO.setEngineType(1);
        taskTemplateVO.setComputeType(1);
        taskTemplateVO.setTaskType(1);
        taskParamController.getEngineParamTmplByComputeType(taskTemplateVO);
    }
}