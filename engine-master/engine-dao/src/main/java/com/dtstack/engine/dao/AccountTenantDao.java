package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.AccountTenant;
import com.dtstack.engine.api.dto.AccountDTO;
import com.dtstack.engine.api.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yuebai
 * @date 2020-02-14
 */
public interface AccountTenantDao {

    Integer insert(AccountTenant accountTenant);

    AccountTenant getByAccount(@Param("userId") Long dtuicUserId, @Param("tenantId") Long dtUicTenantId, @Param("accountId") Long accountId, @Param("isDeleted") Integer isDeleted);

    AccountTenant getByUserIdAndTenantIdAndEngineType(@Param("userId") Long userId, @Param("tenantId") Long tenantId, @Param("engineType") Integer engineType);

    Integer update(AccountTenant accountTenant);

    Integer generalCount(@Param("model") AccountDTO accountDTO);

    List<AccountDTO> generalQuery(PageQuery<AccountDTO> pageQuery);

    List<AccountDTO> getTenantUser(@Param("tenantId") Long tenantId,@Param("type") Integer type);

    AccountTenant getById(@Param("id") Long id);

    void deleteByUserId(@Param("userId") Long dtuicUserId);
}
