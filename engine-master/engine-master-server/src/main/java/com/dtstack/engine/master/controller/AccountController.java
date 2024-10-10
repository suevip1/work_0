package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.AccountTenantVo;
import com.dtstack.engine.api.vo.AccountVo;
import com.dtstack.engine.api.vo.AccountVoLists;
import com.dtstack.engine.api.vo.UicUserVo;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.AccountService;
import com.dtstack.engine.master.router.DtParamOrHeader;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.engine.master.router.permission.Authenticate;
import com.dtstack.engine.master.router.util.CookieUtil;
import com.dtstack.pubsvc.sdk.usercenter.client.UIcUserTenantRelApiClient;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/node/account")
@Api(value = "/node/account", tags = {"账户接口"})
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private SessionUtil sessionUtil;

    @Autowired
    private UIcUserTenantRelApiClient uIcUserTenantRelApiClient;

    @RequestMapping(value = "/bindAccount", method = {RequestMethod.POST})
    @ApiOperation(value = "绑定数据库账号 到对应数栈账号下的集群")
    @Authenticate(all = "console_resource_bind_account_all",
            tenant = "console_resource_bind_account_tenant")// todo
    public void bindAccount(@RequestBody AccountVo accountVo,
                            HttpServletRequest request,
                            @RequestParam("dtToken") String dtToken,
                            @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) throws Exception {

        long userId = CookieUtil.getUserId(request.getCookies());
        String dtUserName = CookieUtil.getDtUserName(request.getCookies());
        accountVo.setModifyUserName(dtUserName);
        accountVo.setUserId(userId);

        Long bindTenantId = accountVo.getBindTenantId();

        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        if (!isAllAuth) {
            List<Long> tenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();
            if (!tenantIds.contains(bindTenantId)) {
                // 说明不相交
                throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
            }
        }

        accountService.bindAccount(accountVo);
    }

    @RequestMapping(value = "/bindAccountList", method = {RequestMethod.POST})
    @ApiOperation(value = "绑定数据库账号列表 到对应数栈账号下")
    @Authenticate(all = "console_resource_bind_account_all",
            tenant = "console_resource_bind_account_tenant")
    public void bindAccountList(@RequestBody AccountVoLists accountVoLists,HttpServletRequest request,
                                @RequestParam("dtToken") String dtToken,
                                @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) throws Exception {
        long userId = CookieUtil.getUserId(request.getCookies());
        String dtUserName = CookieUtil.getDtUserName(request.getCookies());
        if (null == accountVoLists || CollectionUtils.isEmpty(accountVoLists.getAccountList())) {
            throw new RdosDefineException("绑定账号不能为空");
        }
        // 校验是否有重复数据
        Set<String> duplicateBindUser = new HashSet<>();
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);

        for (AccountVo accountVo : accountVoLists.getAccountList()) {
            String oneBindUser = String.format("%s::%s", accountVo.getBindUserId(), accountVo.getEngineType());
            if (!duplicateBindUser.add(oneBindUser)) {
                throw new RdosDefineException("exist duplicate bind user:" + accountVo.getBindUserId());
            }
            accountVo.setModifyUserName(dtUserName);
            accountVo.setUserId(userId);

            Long bindTenantId = accountVo.getBindTenantId();
            if (!isAllAuth) {
                List<Long> tenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();
                if (!tenantIds.contains(bindTenantId)) {
                    // 说明不相交
                    throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
                }
            }
        }
        accountService.bindAccountList(accountVoLists.getAccountList());
    }

    @RequestMapping(value = "/unbindAccount", method = {RequestMethod.POST})
    @ApiOperation(value = "解绑数据库账号")
    @Authenticate(all = "console_resource_bind_account_all",
            tenant = "console_resource_bind_account_tenant")
    public void unbindAccount(@RequestBody AccountTenantVo accountTenantVo, HttpServletRequest request,
                              @DtParamOrHeader(value = "dtuicTenantId", header = "cookie", cookie = "dt_tenant_id") Long dtuicTenantId,
                              @RequestParam("dtToken") String dtToken,
                              @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) throws Exception {
        accountTenantVo.setModifyUserName(CookieUtil.getDtUserName(request.getCookies()));
        accountTenantVo.setModifyDtUicUserId(CookieUtil.getUserId(request.getCookies()));
        if (isAllAuth) {
            UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
            List<Long> tenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();

            if (!tenantIds.contains(dtuicTenantId)) {
                throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
            }
        }
        accountService.unbindAccount(accountTenantVo);
    }

    @RequestMapping(value = "/updateBindAccount", method = {RequestMethod.POST})
    @ApiOperation(value = "更改数据库账号")
    @Authenticate(all = "console_resource_bind_account_all",
            tenant = "console_resource_bind_account_tenant")
    public void updateBindAccount(@RequestBody AccountTenantVo accountTenantVo,
                                  HttpServletRequest request,
                                  @DtParamOrHeader(value = "dtuicTenantId", header = "cookie", cookie = "dt_tenant_id") Long dtuicTenantId,
                                  @RequestParam("dtToken") String dtToken,
                                  @RequestParam(value = "isAllAuth", defaultValue = "true") Boolean isAllAuth) throws Exception {
        if (isAllAuth) {
            UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
            List<Long> tenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();

            if (!tenantIds.contains(dtuicTenantId)) {
                throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
            }
        }

        accountTenantVo.setModifyUserName(CookieUtil.getDtUserName(request.getCookies()));
        accountTenantVo.setModifyDtUicUserId(CookieUtil.getUserId(request.getCookies()));
        accountService.updateBindAccount(accountTenantVo);
    }

    @RequestMapping(value = "/pageQuery", method = {RequestMethod.POST})
    @ApiOperation(value = "分页查询")
    public PageResult<List<AccountVo>> pageQuery(@RequestParam("dtuicTenantId") Long dtuicTenantId, @RequestParam("username") String username, @RequestParam("currentPage") Integer currentPage,
                                                 @RequestParam("pageSize") Integer pageSize, @RequestParam("engineType") Integer engineType, @RequestParam("dtuicUserId") Long dtuicUserId) {

        return accountService.pageQuery(dtuicTenantId, username, currentPage, pageSize, engineType, dtuicUserId);
    }

    @RequestMapping(value="/getTenantUnBandList", method = {RequestMethod.POST})
    @ApiOperation(value = "获取租户未绑定用户列表")
    @Deprecated
    public List<UicUserVo> getTenantUnBandList(@RequestParam("dtuicTenantId") Long dtuicTenantId,@RequestParam("engineType")Integer engineType) {
        return accountService.getTenantUnBandList(dtuicTenantId, engineType);
    }


}
