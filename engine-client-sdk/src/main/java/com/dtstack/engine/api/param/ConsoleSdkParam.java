package com.dtstack.engine.api.param;

import java.util.List;

public class ConsoleSdkParam {

    private int currentPage;
    private int pageSize;

    private List<Long> taskIds;
    private int appType;
    private long paramId;
    /**
     * 参数名称
     */
    private String paramName;
    /**
     * 参数值
     */
    private String paramValue;
    /**
     * 参数描述
     */
    private String paramDesc;


    public long getParamId() {
        return paramId;
    }

    public void setParamId(long paramId) {
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

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

}
