package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;

/**
 * schedule job param po
 *
 * @author ：wangchuan
 * date：Created in 14:09 2022/7/26
 * company: www.dtstack.com
 */
public class ScheduleJobParam extends BaseEntity {

    /**
     * 任务实例 id
     */
    private String jobId;

    /**
     * 任务参数名称
     */
    private String paramName;

    /**
     * 任务参数值
     */
    private String paramValue;

    /**
     * 任务参数描述
     */
    private String paramDesc;

    /**
     * 任务参数类型
     */
    private Integer paramType;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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

    public Integer getParamType() {
        return paramType;
    }

    public void setParamType(Integer paramType) {
        this.paramType = paramType;
    }
}
