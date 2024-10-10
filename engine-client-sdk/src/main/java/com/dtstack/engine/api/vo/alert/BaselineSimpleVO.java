package com.dtstack.engine.api.vo.alert;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/7/4 3:26 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class BaselineSimpleVO {

    private Long id;

    private String baselineName;

    private Integer appType;

    private List<Long> taskIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaselineName() {
        return baselineName;
    }

    public void setBaselineName(String baselineName) {
        this.baselineName = baselineName;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }
}
