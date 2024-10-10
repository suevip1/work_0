package com.dtstack.engine.api.vo.schedule.job;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * @Auther: qiuyun
 * @Date: 2021/12/09 11:17 上午
 * @Email:qiuyun@dtstack.com
 * @Description: topN job query vo
 */
public class ScheduleJobTopRunTimeVO {
    public static final Integer TOP_MAX_SIZE = 100;
    public static final Integer TOP_DEFAULT_MAX_SIZE = 10;

    /**
     * 项目Id
     */
    private Long projectId;

    /**
     * 执行时间起,时间戳(秒)
     */
    @NotNull(message = "startTime is not null")
    private Long startTime;

    /**
     * 执行时间止,时间戳(秒)
     */
    @NotNull(message = "endTime is not null")
    private Long endTime;

    /**
     * 应用类型
     */
    @NotNull(message = "appType is not null")
    private Integer appType;

    /**
     * uic租户id
     */
    @NotNull(message = "dtuicTenantId is not null")
    private Long dtuicTenantId;

    /**
     * topSize 大小，默认 10
     */
    @Max(value = 100, message = "top max size: 100")
    private Integer topSize;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }

    public Integer getTopSize() {
        return topSize;
    }

    public void setTopSize(Integer topSize) {
        this.topSize = topSize;
    }
}
