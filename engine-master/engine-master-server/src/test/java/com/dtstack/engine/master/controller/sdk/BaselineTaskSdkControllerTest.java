package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.alert.AlarmChooseTaskVO;
import com.dtstack.engine.api.vo.alert.BaselineTaskConditionVO;
import com.dtstack.engine.api.vo.alert.BaselineTaskPageVO;
import com.dtstack.engine.api.vo.alert.BaselineTaskVO;
import com.dtstack.engine.master.impl.BaselineTaskService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MockWith(BaselineTaskSdkControllerMock.class)
public class BaselineTaskSdkControllerTest {
    BaselineTaskSdkController baselineTaskSdkController = new BaselineTaskSdkController();

    @Test
    public void testPage() throws Exception {
        baselineTaskSdkController.page(new BaselineTaskConditionVO());
    }

    @Test
    public void testAddOrUpdateBaselineTask() throws Exception {
        baselineTaskSdkController.addOrUpdateBaselineTask(new BaselineTaskVO());
    }

    @Test
    public void testUpdateBaselineOwner() throws Exception {
        baselineTaskSdkController.updateBaselineOwner(Collections.singletonList(1L), 1L);

    }

    @Test
    public void testDeleteBaselineTask() throws Exception {
        baselineTaskSdkController.deleteBaselineTask(1L);
    }

    @Test
    public void testDeleteBaselineTaskByProject() throws Exception {
        baselineTaskSdkController.deleteBaselineTaskByProject(1L, 0);
    }

    @Test
    public void testOpenOrClose() throws Exception {
        baselineTaskSdkController.openOrClose(1L, 0);
    }

    @Test
    public void testGetBaselineTaskDetails() throws Exception {
        baselineTaskSdkController.getBaselineTaskDetails(1L);
    }

    @Test
    public void testGetBaselineTaskDetailsByIds() throws Exception {
        baselineTaskSdkController.getBaselineTaskDetailsByIds(Collections.singletonList(1L));
    }

    @Test
    public void testGetBaselineTaskInfo() throws Exception {
        baselineTaskSdkController.getBaselineTaskInfo(1L, 0);
    }

    @Test
    public void testEstimatedFinish() throws Exception {
        baselineTaskSdkController.estimatedFinish(Collections.singletonList(1L), 0);
    }
}

class BaselineTaskSdkControllerMock {

    @MockInvoke(targetClass = BaselineTaskService.class)
    public PageResult<List<BaselineTaskPageVO>> page(BaselineTaskConditionVO vo) {
        return PageResult.EMPTY_PAGE_RESULT;
    }

    @MockInvoke(targetClass = BaselineTaskService.class)
    public Integer updateBaselineOwner(List<Long> oldOwnerUserIds, Long newOwnerUserId) {
        return 1;
    }

    @MockInvoke(targetClass = BaselineTaskService.class)
    public Integer deleteBaselineTask(Long id) {
        return 1;
    }

    @MockInvoke(targetClass = BaselineTaskService.class)
    public String estimatedFinish(List<Long> taskIds, Integer appType) {
        return "";

    }

    @MockInvoke(targetClass = BaselineTaskService.class)
    public List<AlarmChooseTaskVO> getAlarmChooseTaskVOS(Long id, Integer appType) {
        return new ArrayList<>();

    }

    @MockInvoke(targetClass = BaselineTaskService.class)
    public Integer openOrClose(Long id, Integer openStatus) {
        return 1;

    }

    @MockInvoke(targetClass = BaselineTaskService.class)
    public BaselineTaskVO getBaselineTaskDetails(Long id) {
        return null;
    }

    @MockInvoke(targetClass = BaselineTaskService.class)
    public List<BaselineTaskVO> getBaselineTaskDetailsByIds(List<Long> ids) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = BaselineTaskService.class)
    public Integer deleteBaselineTaskByProjectId(Long projectId, Integer appType) {
        return 1;
    }

    @MockInvoke(targetClass = BaselineTaskService.class)
    public Integer addOrUpdateBaselineTask(BaselineTaskVO baselineTaskVO) {
        return 1;
    }
}