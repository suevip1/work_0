package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.impl.PlatformService;
import com.dtstack.engine.master.vo.PlatformEventVO;
import org.junit.Test;

@MockWith(PlatformControllerTest.PlatformControllerTestMock.class)
public class PlatformControllerTest {

    private PlatformController controller = new PlatformController();

    static class PlatformControllerTestMock {
        @MockInvoke(targetClass = PlatformService.class)
        public void callback(PlatformEventVO eventVO) {

        }
    }

    @Test
    public void callBack() {
        controller.callBack(new PlatformEventVO());
    }
}