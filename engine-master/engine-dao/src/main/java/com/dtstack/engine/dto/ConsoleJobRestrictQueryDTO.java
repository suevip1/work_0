package com.dtstack.engine.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-02 17:31
 */
public class ConsoleJobRestrictQueryDTO {
    /**
     * 限制时间起始 yyyy-MM-dd HH:mm:ss
     */
    private String restrictStartTime;

    /**
     * 限制时间终止 yyyy-MM-dd HH:mm:ss
     */
    private String restrictEndTime;

    /**
     * 创建人 id
     */
    private Long createUserId;

    /**
     * 规则状态
     */
    private List<Integer> status;

    /**
     * 是否有效，0否，1是
     */
    private List<Integer> isEffective;

    private Integer currentPage;

    private Integer pageSize;

    public String getRestrictStartTime() {
        return restrictStartTime;
    }

    public void setRestrictStartTime(String restrictStartTime) {
        this.restrictStartTime = restrictStartTime;
    }

    public String getRestrictEndTime() {
        return restrictEndTime;
    }

    public void setRestrictEndTime(String restrictEndTime) {
        this.restrictEndTime = restrictEndTime;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<Integer> getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(List<Integer> isEffective) {
        this.isEffective = isEffective;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("restrictStartTime", restrictStartTime)
                .append("restrictEndTime", restrictEndTime)
                .append("createUserId", createUserId)
                .append("status", status)
                .append("isEffective", isEffective)
                .append("currentPage", currentPage)
                .append("pageSize", pageSize)
                .toString();
    }
}
