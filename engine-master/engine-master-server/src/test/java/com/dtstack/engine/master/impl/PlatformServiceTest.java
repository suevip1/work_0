package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.enums.PlatformEventType;
import com.dtstack.engine.master.mockcontainer.impl.PlatformMock;
import com.dtstack.engine.master.vo.PlatformEventVO;
import org.junit.Test;

@MockWith(PlatformMock.class)
public class PlatformServiceTest {
    PlatformService platformService = new PlatformService();

    @Test
    public void testCallback() throws Exception {
        PlatformEventVO platformEventVO = new PlatformEventVO();
        platformEventVO.setEventCode(PlatformEventType.DELETE_TENANT.name());
        platformEventVO.setDtUicTenantId(1L);
        platformService.callback(platformEventVO);

        platformEventVO.setEventCode(PlatformEventType.DELETE_USER.name());
        platformEventVO.setDtUicTenantId(1L);
        platformEventVO.setUserId(1L);
        platformService.callback(platformEventVO);
    }

    @Test
    public void testInit() throws Exception {
        platformService.init();
    }
}