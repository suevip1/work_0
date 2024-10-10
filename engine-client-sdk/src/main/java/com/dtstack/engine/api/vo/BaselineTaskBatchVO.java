package com.dtstack.engine.api.vo;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2023/2/6 10:24 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class BaselineTaskBatchVO {

    private List<Long> taskIds;

    private Integer appType;

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }
}
