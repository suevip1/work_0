package com.dtstack.engine.master.diagnosis.enums;

public enum DiagnosisResultEnum {
    NOT_REACH("未到此流程", 1),
    UNDER_DIAGNOSIS("任务诊断检查中", 2),
    DIAGNOSIS_NORMAL("任务诊断检查正常", 3),
    DIAGNOSIS_FAIL("任务诊断检查异常", 4),
    NOT_CONTAINS("不包含此流程", 5),
    ;

    public static DiagnosisResultEnum getByVal(int val) {
        for (DiagnosisResultEnum anEnum : DiagnosisResultEnum.values()) {
            if (anEnum.getVal() == val) {
                return anEnum;
            }
        }
        return null;
    }


    private String name;

    private Integer val;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    DiagnosisResultEnum(String name, Integer val) {
        this.name = name;
        this.val = val;
    }
}
