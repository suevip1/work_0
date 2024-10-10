package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.UicUserVo;
import com.dtstack.engine.master.impl.AccountService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@MockWith(AccountSdkControllerMock.class)
public class AccountSdkControllerTest {
    AccountSdkController accountSdkController = new AccountSdkController();

    @Test
    public void testGetTenantUnBandList() throws Exception {
        accountSdkController.getTenantUnBandList(1L, 0);
    }


}

class AccountSdkControllerMock {
   @MockInvoke(targetClass = AccountService.class)
   public List<UicUserVo> getTenantUnBandList(Long dtuicTenantId, Integer engineType) {
       return new ArrayList<>();
   }
}