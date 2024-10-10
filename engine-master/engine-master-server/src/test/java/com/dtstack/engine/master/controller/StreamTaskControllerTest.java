package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.vo.stream.EngineStreamJobVO;
import com.dtstack.engine.master.impl.StreamTaskService;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@MockWith(StreamTaskControllerTest.StreamTaskControllerMock.class)
public class StreamTaskControllerTest {

    private static final StreamTaskController streamTaskController = new StreamTaskController();

    static class StreamTaskControllerMock {
        @MockInvoke(targetClass = StreamTaskService.class)
        public List<EngineJobCheckpoint> getCheckPoint(String taskId, Long triggerStart, Long triggerEnd) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = StreamTaskService.class)
        public List<EngineJobCheckpoint> getFailedCheckPoint(String taskId, Long triggerStart, Long triggerEnd, Integer size) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = StreamTaskService.class)
        public EngineJobCheckpoint getSavePoint( String taskId) {
            return new EngineJobCheckpoint();
        }

        @MockInvoke(targetClass = StreamTaskService.class)
        public EngineJobCheckpoint getByTaskIdAndEngineTaskId( String taskId,  String engineTaskId){
            return new EngineJobCheckpoint();
        }

        @MockInvoke(targetClass = StreamTaskService.class)
        public List<ScheduleJob> getEngineStreamJob(List<String> taskIds) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = StreamTaskService.class)
        public List<String> getTaskIdsByStatus( Integer status) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = StreamTaskService.class)
        public Integer getTaskStatus(String taskId) {
            return 1;
        }

        @MockInvoke(targetClass = StreamTaskService.class)
        public List<String> getRunningTaskLogUrl( String taskId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = StreamTaskService.class)
        public CheckResult grammarCheck(ParamActionExt paramActionExt) {
            return new CheckResult();
        }

        @MockInvoke(targetClass = StreamTaskService.class)
        public List<EngineStreamJobVO> getEngineStreamJobNew(List<String> taskIds) {
            return new ArrayList<>();
        }

    }

    @Test
    public void getCheckPoint() {
        streamTaskController.getCheckPoint("1",1L,1L);
    }

    @Test
    public void getFailedCheckPoint() {
        streamTaskController.getFailedCheckPoint("1",1L,1L,1);
    }

    @Test
    public void getSavePoint() {
        streamTaskController.getSavePoint("1");
    }

    @Test
    public void getByTaskIdAndEngineTaskId() {
        streamTaskController.getByTaskIdAndEngineTaskId("1","s");
    }

    @Test
    public void getEngineStreamJob() {
        streamTaskController.getEngineStreamJob(Lists.newArrayList("1"));
    }

    @Test
    public void getEngineStreamJobNew() {
        streamTaskController.getEngineStreamJobNew(Lists.newArrayList("1"));
    }

    @Test
    public void getTaskIdsByStatus() {
        streamTaskController.getTaskIdsByStatus(1);
    }

    @Test
    public void getTaskStatus() {
        streamTaskController.getTaskStatus("1");
    }

    @Test
    public void getRunningTaskLogUrl() {
        streamTaskController.getRunningTaskLogUrl("1");
    }

    @Test
    public void grammarCheck() {
        streamTaskController.grammarCheck(new ParamActionExt());
    }
}