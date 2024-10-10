package com.dtstack.engine.api.vo.project;

/**
 * @Auther: dazhi
 * @Date: 2021/9/9 5:45 下午
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class FillDataChooseProjectVO {

    /**
     * 工程id
     * 必填
     */
    private Long projectId;

    /**
     * 应用类型
     * 必填
     */
    private Integer appType;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }
}
