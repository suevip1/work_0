package com.dtstack.engine.master.mockcontainer.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.AccountTenantVo;
import com.dtstack.engine.api.vo.AccountVo;
import com.dtstack.engine.master.impl.AccountService;
import com.dtstack.engine.master.router.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author leon
 * @date 2022-07-01 11:40
 **/
public class AccountControllerMock {

    @MockInvoke(targetClass = HttpServletRequest.class)
    public Cookie[] getCookies() {
        return new Cookie[]{};
    }

    @MockInvoke(targetClass = CookieUtil.class)
    public static long getUserId(Cookie[] cookies) {
        return -1;
    }

    @MockInvoke(targetClass = CookieUtil.class)
    public static String getDtUserName(Cookie[] cookies) {
        return "";
    }

    @MockInvoke(targetClass = AccountService.class)
    public void bindAccount(AccountVo accountVo) throws Exception {}

    @MockInvoke(targetClass = AccountService.class)
    public void bindAccountList(List<AccountVo> list) throws Exception {}

    @MockInvoke(targetClass = AccountService.class)
    public void unbindAccount(AccountTenantVo accountTenantVo) throws Exception {}

    @MockInvoke(targetClass = AccountService.class)
    public void updateBindAccount(AccountTenantVo accountTenantVo) throws Exception {}


    @MockInvoke(targetClass = AccountService.class)
    public PageResult<List<AccountVo>> pageQuery(Long dtuicTenantId, String username, Integer currentPage,
                                                 Integer pageSize, Integer engineType, Long dtuicUserId) {

        return PageResult.EMPTY_PAGE_RESULT;
    }
}
