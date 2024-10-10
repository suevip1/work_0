package com.dtstack.engine.master.diagnosis.enums;

public enum DiagnosisEnum {

    CYC_TIME_JOB_DIAGNOSIS("起调时间检查", 1),
    TENANT_RESOURCE_DIAGNOSIS("资源限制检查", 2),
    STATUS_DIAGNOSIS("实例状态检查", 3),
    PARENT_DEPEND_JOB_DIAGNOSIS("上下依赖检查", 4),
    JOB_SUBMIT_DIAGNOSIS("实例提交", 5),
    RESOURCE_MATH_DIAGNOSIS("资源匹配", 6),
    RUNNING_JOB_DIAGNOSIS("实例运行", 7),
    VALID_JOB_DIAGNOSIS("质量校验", 8);

    private String name;

    private Integer val;

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    DiagnosisEnum(String name, Integer val) {
        this.name = name;
        this.val = val;
    }

    public static DiagnosisEnum getByVal(int val) {
        for (DiagnosisEnum anEnum : DiagnosisEnum.values()) {
            if (anEnum.getVal() == val) {
                return anEnum;
            }
        }
        return null;
    }
}
