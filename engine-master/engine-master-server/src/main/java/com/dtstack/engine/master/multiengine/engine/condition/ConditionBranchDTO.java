package com.dtstack.engine.master.multiengine.engine.condition;

import java.util.List;

/**
 * condition branch
 *
 * @author ：wangchuan
 * date：Created in 下午3:55 2022/6/29
 * company: www.dtstack.com
 */
public class ConditionBranchDTO {

    /**
     * 条件分支名称
     */
    private String name;

    /**
     * 分支条件
     */
    private String condition;

    /**
     * 满足条件的子任务列表
     */
    private List<Long> childTaskIdList;

    /**
     * 条件说明
     */
    private String remark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<Long> getChildTaskIdList() {
        return childTaskIdList;
    }

    public void setChildTaskIdList(List<Long> childTaskIdList) {
        this.childTaskIdList = childTaskIdList;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
