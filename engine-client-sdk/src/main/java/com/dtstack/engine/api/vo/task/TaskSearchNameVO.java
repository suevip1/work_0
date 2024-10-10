package com.dtstack.engine.api.vo.task;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName TaskSearchNameVO
 * @date 2022/7/18 5:01 PM
 */
public class TaskSearchNameVO {

    /**
     * 名称
     */
    private String taskName;

    /**
     * 检索类型：
     * 1： 左模糊匹配。
     * 2：右模糊匹配。
     * 3：全匹配。
     * 4.
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
