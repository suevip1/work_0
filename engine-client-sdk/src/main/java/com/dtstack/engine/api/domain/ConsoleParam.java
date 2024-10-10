package com.dtstack.engine.api.domain;

import com.dtstack.engine.api.domain.BaseEntity;

public class ConsoleParam extends BaseEntity {

    private String paramName;
    private String paramValue;
    private String paramDesc;
    private Long paramId;
    private Long taskId;
    private Integer appType;
    private Integer paramType;

    private Long createUserId;

    /**
     * 日期基准：1 自然日，2 自定义日期
     */
    private Integer dateBenchmark;

    /**
     * 日期格式
     */
    private String dateFormat;

    /**
     * 调度日历 id
     */
    private Long calenderId;

    public Integer getDateBenchmark() {
        return dateBenchmark;
    }

    public void setDateBenchmark(Integer dateBenchmark) {
        this.dateBenchmark = dateBenchmark;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Long getCalenderId() {
        return calenderId;
    }

    public void setCalenderId(Long calenderId) {
        this.calenderId = calenderId;
    }

    public Integer getParamType() {
        return paramType;
    }

    public void setParamType(Integer paramType) {
        this.paramType = paramType;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getParamId() {
        return paramId;
    }

    public void setParamId(Long paramId) {
        this.paramId = paramId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }
}
