package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.param.CatalogParam;
import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.api.pojo.GrammarCheckParam;
import com.dtstack.engine.api.vo.JobIdAndStatusVo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MockWith(ActionSdkMock.class)
public class ActionSdkControllerTest {
    ActionSdkController actionSdkController = new ActionSdkController();

    @Test
    public void testStatusByJobIds() throws Exception {
        actionSdkController.statusByJobIds(Collections.singletonList("String"));
    }

    @Test
    public void testGrammarCheck() throws Exception {
        actionSdkController.grammarCheck(new GrammarCheckParam());
    }

    @Test
    public void testExecuteCatalog() throws Exception {
        actionSdkController.executeCatalog(new CatalogParam());
    }
}

class ActionSdkMock {
    @MockInvoke(targetClass = ActionService.class)
    public String executeCatalog(CatalogParam catalogParam) throws Exception {
        return "";
    }

    @MockInvoke(targetClass = ActionService.class)
    public List<JobIdAndStatusVo> statusByJobIdsToVo(List<String> jobIds) throws Exception {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public CheckResult grammarCheck(JobClient jobClient) throws Exception {
        return new CheckResult();
    }
}