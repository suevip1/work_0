package com.dtstack.engine.dto;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName TaskSearchNameDTO
 * @date 2022/7/20 10:38 AM
 */
public class TaskSearchNameDTO {

    /**
     * 名称
     */
    private String taskName;

    /**
     * 检索类型：
     * 1： 左模糊匹配。
     * 2：右模糊匹配。
     * 3：全匹配。
     */
    private Integer searchType;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
    }
}
