package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.AccountTenantVo;
import com.dtstack.engine.api.vo.AccountVo;
import com.dtstack.engine.api.vo.AccountVoLists;
import com.dtstack.engine.master.mockcontainer.controller.AccountControllerMock;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

@MockWith(AccountControllerMock.class)
public class AccountControllerTest {

    private static final AccountController accountController = new AccountController();

    @Test
    public void bindAccount() throws Exception {
        accountController.bindAccount(new AccountVo(), null, "", true);
    }

    @Test
    public void bindAccountList() throws Exception {
        AccountVoLists accountVoLists = new AccountVoLists();
        List<AccountVo> accountVoList = Lists.newArrayList();
        AccountVo accountVo = new AccountVo();
        accountVoList.add(accountVo);
        accountVoLists.setAccountList(accountVoList);
        accountController.bindAccountList(accountVoLists, null, "", true);
    }

    @Test
    public void unbindAccount() throws Exception {
        accountController.unbindAccount(new AccountTenantVo(), null, null, null, true);
    }

    @Test
    public void updateBindAccount() throws Exception {
        accountController.updateBindAccount(new AccountTenantVo(), null, null, "", true);
    }

    @Test
    public void pageQuery() {
        accountController.pageQuery(1L, "leon", 1, 1, 1, 1L);
    }

}