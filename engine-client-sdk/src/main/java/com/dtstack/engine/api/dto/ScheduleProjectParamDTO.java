package com.dtstack.engine.api.dto;

import com.dtstack.engine.api.domain.BaseEntity;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-12-05 21:01
 */
public class ScheduleProjectParamDTO extends BaseEntity {

    /**
     * 参数名称
     */
    private String paramName;

    /**
     * 参数值
     */
    private String paramValue;

    /**
     * 描述
     */
    private String paramDesc;

    /**
     * 参数类型: 9 常量 todo
     */
    private Integer paramType;

    /**
     * 项目 id
     */
    private Long projectId;

    /**
     * 创建用户
     */
    private Long createUserId;

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

    public Integer getParamType() {
        return paramType;
    }

    public void setParamType(Integer paramType) {
        this.paramType = paramType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }
}