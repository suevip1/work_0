package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.AccountTenantVo;
import com.dtstack.engine.api.vo.AccountVo;
import com.dtstack.engine.api.vo.UicUserVo;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.master.mockcontainer.impl.AccountServiceMock;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;


@MockWith(AccountServiceMock.class)
public class AccountServiceTest {
    AccountService accountService = new AccountService();

    @Test
    public void testBindAccount() throws Exception {
        AccountVo accountVo = new AccountVo();
        accountVo.setPassword("test");
        accountVo.setUsername("test");
        accountVo.setUserId(1L);
        accountVo.setBindTenantId(1L);
        accountVo.setName("test");
        accountVo.setBindUserId(1L);
        accountVo.setEngineType(MultiEngineType.TIDB.getType());
        accountService.bindAccount(accountVo);
    }


    @Test
    public void testUnbindAccount() throws Exception {
        AccountTenantVo accountTenantVo = new AccountTenantVo();
        accountTenantVo.setModifyDtUicUserId(1L);
        accountTenantVo.setEngineType(MultiEngineType.TIDB.getType());
        accountTenantVo.setModifyUserName("test");
        accountTenantVo.setId(1L);
        accountTenantVo.setName("test");
        accountService.unbindAccount(accountTenantVo);
    }

    @Test
    public void testUpdateBindAccount() throws Exception {
        AccountTenantVo accountTenantVo = new AccountTenantVo();
        accountTenantVo.setModifyDtUicUserId(1L);
        accountTenantVo.setEngineType(MultiEngineType.TIDB.getType());
        accountTenantVo.setModifyUserName("test");
        accountTenantVo.setId(1L);
        accountTenantVo.setName("test");
        accountService.updateBindAccount(accountTenantVo);
    }

    @Test
    public void testPageQuery() throws Exception {
        PageResult<List<AccountVo>> result = accountService.pageQuery(1L, "username", 0, 0, 0, 1L);
        Assert.assertNotNull(result);

    }

    @Test
    public void testGetTenantUnBandList() throws Exception {
        List<UicUserVo> result = accountService.getTenantUnBandList(1L, 0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testDeleteUser() throws Exception {
        accountService.deleteUser(1L);
    }
}