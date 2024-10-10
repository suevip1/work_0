package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.alert.BaselineBlockJobRecordVO;
import com.dtstack.engine.api.vo.alert.BaselineJobConditionVO;
import com.dtstack.engine.api.vo.alert.BaselineJobPageVO;
import com.dtstack.engine.api.vo.alert.BaselineViewVO;
import com.dtstack.engine.master.impl.BaselineJobService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@MockWith(BaselineJobSdkControllerMock.class)
public class BaselineJobSdkControllerTest {

    BaselineJobSdkController baselineJobSdkController = new BaselineJobSdkController();

    @Test
    public void testPage() throws Exception {
        baselineJobSdkController.page(new BaselineJobConditionVO());
    }

    @Test
    public void testGetBaselineJobInfo() throws Exception {
        baselineJobSdkController.getBaselineJobInfo(1L);
    }

    @Test
    public void testBaselineJobGraph() throws Exception {
        baselineJobSdkController.baselineJobGraph(1L);
    }

    @Test
    public void testBaselineBlockJob() throws Exception {
        baselineJobSdkController.baselineBlockJob(1L);
    }

    @Test
    public void testCreateBaselineJob() throws Exception {
        baselineJobSdkController.createBaselineJob(1L, "date");
    }
}

class BaselineJobSdkControllerMock {

    @MockInvoke(targetClass = BaselineJobService.class)
    public List<String> getBaselineJobInfo(Long baselineJob) {
        return new ArrayList<>();

    }

    @MockInvoke(targetClass = BaselineJobService.class)
    public List<BaselineViewVO> baselineJobGraph(Long baselineTaskId) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = BaselineJobService.class)
    public List<BaselineBlockJobRecordVO> baselineBlockJob(Long baselineJobId) {
        return new ArrayList<>();

    }

    @MockInvoke(targetClass = BaselineJobService.class)
    public void createBaselineJob(Long id, String triggerDay) {
        return;
    }

    @MockInvoke(targetClass = BaselineJobService.class)
    public PageResult<List<BaselineJobPageVO>> page(BaselineJobConditionVO vo) {
        return PageResult.EMPTY_PAGE_RESULT;
    }
}