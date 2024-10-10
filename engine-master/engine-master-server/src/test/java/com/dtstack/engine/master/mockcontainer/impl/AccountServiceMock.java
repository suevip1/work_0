package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.Account;
import com.dtstack.engine.api.domain.AccountTenant;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.dto.AccountDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.UicUserVo;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.AccountDao;
import com.dtstack.engine.dao.AccountTenantDao;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.UserService;
import com.dtstack.engine.master.mapstruct.UserStruct;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.master.worker.DataSourceXOperator;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UserDTO;
import com.dtstack.pubsvc.sdk.usercenter.domain.param.FindUserParam;
import com.dtstack.pubsvc.sdk.usercenter.domain.result.UICUserListResult;
import com.dtstack.sdk.core.common.ApiResponse;
import org.apache.ibatis.annotations.Param;
import org.assertj.core.util.Lists;
import org.checkerframework.checker.units.qual.A;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AccountServiceMock {

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getSM2PublicKey() {
        return "";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getSM2PrivateKey() {
        return "";
    }

    @MockInvoke(targetClass = ClusterService.class)
    public String tiDBInfo(Long dtUicTenantId, Long dtUicUserId, Map<Integer, String> componentVersionMap) {
        return "{}";
    }


    @MockInvoke(targetClass = DataSourceXOperator.class)
    public ComponentTestResult testConnect(String engineType, String pluginInfo, Long clusterId, Long tenantId, String versionName) {
        return new ComponentTestResult();
    }

    @MockInvoke(targetClass = AccountTenantDao.class)
    Integer update(AccountTenant accountTenant) {
        return 1;
    }

    @MockInvoke(targetClass = AccountDao.class)
    Integer insert(Account account) {
        return 1;
    }


    @MockInvoke(targetClass = AccountDao.class, targetMethod = "getById")
    Account getAccountById(@Param("id") Long id) {
        Account account = new Account();
        account.setModifyUserId(1L);
        account.setPassword("");
        account.setName("test");
        return account;
    }

    @MockInvoke(targetClass = UserService.class)
    public List<User> findUserWithFill(Set<Long> userIds) {
        User user = new User();
        user.setUserName("test");
        user.setEmail("test@dtstack.com");
        return Lists.newArrayList(user);
    }

    @MockInvoke(targetClass = UserService.class)
    public User findByUser(Long userId) {
        User user = new User();
        user.setDtuicUserId(1l);
        user.setUserName("test");
        return user;
    }

    @MockInvoke(targetClass = AccountTenantDao.class)
    List<AccountDTO> getTenantUser(@Param("tenantId") Long tenantId, @Param("type") Integer type) {
        AccountDTO account = new AccountDTO();
        account.setModifyUserId(1L);
        account.setPassword("");
        account.setName("test");
        return Lists.newArrayList(account);
    }

    @MockInvoke(targetClass = AccountTenantDao.class)
    List<AccountDTO> generalQuery(PageQuery<AccountDTO> pageQuery) {
        AccountDTO account = new AccountDTO();
        account.setModifyUserId(1L);
        account.setPassword("");
        account.setName("test");
        return Lists.newArrayList(account);
    }

    @MockInvoke(targetClass = AccountDao.class)
    Integer update(Account account) {
        return 1;
    }

    @MockInvoke(targetClass = AccountTenantDao.class)
    AccountTenant getById(@Param("id") Long id) {
        AccountTenant accountTenant = new AccountTenant();
        accountTenant.setAccountId(1l);
        accountTenant.setDtuicTenantId(1L);
        accountTenant.setDtuicUserId(1L);
        accountTenant.setId(id);
        return accountTenant;
    }


    @MockInvoke(targetClass = AccountTenantDao.class)
    Integer generalCount(@Param("model") AccountDTO accountDTO) {
        return 1;
    }

    @MockInvoke(targetClass = AccountTenantDao.class)
    void deleteByUserId(@Param("userId") Long dtuicUserId) {
        return;
    }

    @MockInvoke(targetClass = AccountTenantDao.class)
    Integer insert(AccountTenant accountTenant) {
        return 1;
    }

    @MockInvoke(targetClass = AccountTenantDao.class)
    AccountTenant getByAccount(@Param("userId") Long dtuicUserId, @Param("tenantId") Long dtUicTenantId, @Param("accountId") Long accountId, @Param("isDeleted") Integer isDeleted) {
        return null;
    }

    @MockInvoke(targetClass = ConsoleCache.class)
    public void publishRemoveMessage(String tenantId) {
        return;
    }

    @MockInvoke(targetClass = UicUserApiClient.class)
    ApiResponse<List<UICUserListResult>> findAllUsers(@com.dtstack.sdk.core.feign.Param("tenantId") Long var1, @com.dtstack.sdk.core.feign.Param("productCode") String var2) {
        UICUserListResult userDTO = new UICUserListResult();
        userDTO.setUserName("test");
        userDTO.setUserId(1L);
        ApiResponse<List<UICUserListResult>> response = new ApiResponse<>();
        response.setData(Lists.newArrayList(userDTO));
        return response;
    }


    @MockInvoke(targetClass = UicUserApiClient.class)
    ApiResponse<UserDTO> findByUsername(FindUserParam var1) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setId(1L);
        ApiResponse<UserDTO> response = new ApiResponse<>();
        response.setData(userDTO);
        return response;
    }

    @MockInvoke(targetClass = UserStruct.class)
    List<UicUserVo> toUicUserVO(List<UICUserListResult> uicUserListResult){
        UicUserVo userDTO = new UicUserVo();
        userDTO.setUserName("test");
        userDTO.setUserId(1L);
        return Lists.newArrayList(userDTO);
    }
}
