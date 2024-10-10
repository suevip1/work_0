package com.dtstack.engine.master.enums;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-02 19:41
 */
public enum JobRestrictStatusEnum {
    WAIT(0, "等待执行", "开启但未实际生效的规则"),
    RUNNING(1, "执行中", "开启并且已经实际生效的规则"),
    CLOSED(2, "关闭", "没到「计划结束时间」的关闭应用的规则"),
    INVALID(3, "无效", "超出「计划结束时间」的无实际生效时间的规则（没有实际生效过）"),
    EXPIRE(4, "过期", "超过「计划结束时间」的有实际生效时间的规则（实际生效过）"),
    ;

    private Integer status;

    private String title;

    private String describe;

    JobRestrictStatusEnum(Integer status, String title, String describe) {
        this.status = status;
        this.title = title;
        this.describe = describe;
    }

    public Integer getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescribe() {
        return describe;
    }

}