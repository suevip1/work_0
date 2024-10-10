package com.dtstack.engine.po;

import com.dtstack.engine.api.param.PageParam;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/10 11:06 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class BaselineTaskConditionModel extends PageParam {

    private String baselineName;

    private Long ownerUserId;

    private Long projectId;

    private Long tenantId;

    private Long batchType;

    private List<Integer> priorityList;

    public String getBaselineName() {
        return baselineName;
    }

    public void setBaselineName(String baselineName) {
        this.baselineName = baselineName;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getBatchType() {
        return batchType;
    }

    public void setBatchType(Long batchType) {
        this.batchType = batchType;
    }

    public List<Integer> getPriorityList() {
        return priorityList;
    }

    public void setPriorityList(List<Integer> priorityList) {
        this.priorityList = priorityList;
    }
}
