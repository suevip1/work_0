package com.dtstack.engine.api.vo.alert;

import com.dtstack.engine.api.param.PageParam;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/10 11:06 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class BaselineJobConditionVO extends PageParam {

    private String baselineName;

    private Long ownerUserId;

    private Long startBusinessDate;

    private Long endBusinessDate;

    /**
     * 1 单批次 2 多批次
     */
    private Integer batchType;

    private Long projectId;

    private Long tenantId;

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

    public Long getStartBusinessDate() {
        return startBusinessDate;
    }

    public void setStartBusinessDate(Long startBusinessDate) {
        this.startBusinessDate = startBusinessDate;
    }

    public Long getEndBusinessDate() {
        return endBusinessDate;
    }

    public void setEndBusinessDate(Long endBusinessDate) {
        this.endBusinessDate = endBusinessDate;
    }

    public Integer getBatchType() {
        return batchType;
    }

    public void setBatchType(Integer batchType) {
        this.batchType = batchType;
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
