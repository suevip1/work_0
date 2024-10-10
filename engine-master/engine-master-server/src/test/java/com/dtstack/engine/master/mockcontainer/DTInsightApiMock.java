package com.dtstack.engine.master.mockcontainer;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.sdk.core.common.DtInsightApi;
import com.dtstack.sdk.core.common.DtInsightServer;

/**
 * @author leon
 * @date 2022-06-23 17:17
 **/
public class DTInsightApiMock extends BaseMock {

    @MockInvoke(targetClass = DtInsightApi.class)
    public <T extends DtInsightServer> T getSlbApiClient(Class<T> t) {
        return null;
    }
}
