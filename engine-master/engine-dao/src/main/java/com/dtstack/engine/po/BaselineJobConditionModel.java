package com.dtstack.engine.po;

import com.dtstack.engine.api.param.PageParam;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/10 11:06 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class BaselineJobConditionModel extends PageParam {

    private String baselineName;

    private Long ownerUserId;

    private Timestamp startBusinessTime;

    private Timestamp endBusinessTime;

    private Long projectId;

    private Long tenantId;

    /**
     * 1 单批次 2 多批次
     */
    private Integer batchType;

    public Integer getBatchType() {
        return batchType;
    }

    public void setBatchType(Integer batchType) {
        this.batchType = batchType;
    }

    private List<Integer> baselineStatusList;

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

    public Timestamp getStartBusinessTime() {
        return startBusinessTime;
    }

    public void setStartBusinessTime(Timestamp startBusinessTime) {
        this.startBusinessTime = startBusinessTime;
    }

    public Timestamp getEndBusinessTime() {
        return endBusinessTime;
    }

    public void setEndBusinessTime(Timestamp endBusinessTime) {
        this.endBusinessTime = endBusinessTime;
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

    public List<Integer> getBaselineStatusList() {
        return baselineStatusList;
    }

    public void setBaselineStatusList(List<Integer> baselineStatusList) {
        this.baselineStatusList = baselineStatusList;
    }
}
