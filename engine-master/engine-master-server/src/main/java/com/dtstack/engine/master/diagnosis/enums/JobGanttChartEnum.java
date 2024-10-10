package com.dtstack.engine.master.diagnosis.enums;

/**
 * @author yuebai
 * @date 2022/8/22
 */
public enum JobGanttChartEnum {
    CYC_TIME("起调时间", 1),
    TENANT_RESOURCE_TIME("资源限制时间", 2),
    STATUS_TIME("实例状态时间", 3),
    PARENT_DEPEND_TIME("上下依赖时间", 4),
    JOB_SUBMIT_TIME("实例提交时间", 5),
    RESOURCE_MATCH_TIME("资源匹配时间", 6),
    RUN_JOB_TIME("实例运行时间", 7),
    VALID_JOB_TIME("质量校验时间", 8);
    private String name;

    private Integer val;

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    JobGanttChartEnum(String name, Integer val) {
        this.name = name;
        this.val = val;
    }
}
