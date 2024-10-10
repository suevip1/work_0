package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;

/**
 * @author yuebai
 * @date 2020-02-14
 */
@ApiModel
public class AccountTenant extends BaseEntity{


    private Long dtuicUserId;

    private Long dtuicTenantId;

    private Long accountId;

    private Long createUserId;

    private Long modifyUserId;


    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }

    public Long getDtuicUserId() {
        return dtuicUserId;
    }

    public void setDtuicUserId(Long dtuicUserId) {
        this.dtuicUserId = dtuicUserId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
