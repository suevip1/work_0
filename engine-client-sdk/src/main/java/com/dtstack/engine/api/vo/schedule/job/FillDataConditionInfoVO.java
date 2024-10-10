package com.dtstack.engine.api.vo.schedule.job;

import com.dtstack.engine.api.vo.project.FillDataChooseProjectVO;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/10/28 11:12 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataConditionInfoVO {

    /**
     * 选择的项目
     */
    private List<FillDataChooseProjectVO> projects;

    /**
     * 需要补的数类型，如果集合是空的，则不做任务类型过滤
     */
    private List<Integer> taskTypes;

    /**
     * 选择的标签
     */
    private List<Long> tagIds;

    /**
     * 是否过滤冻结任务
     * 0 不过滤 默认
     * 1. 过滤
     */
    private Integer filterFrozen;

    public List<FillDataChooseProjectVO> getProjects() {
        return projects;
    }

    public void setProjects(List<FillDataChooseProjectVO> projects) {
        this.projects = projects;
    }

    public List<Integer> getTaskTypes() {
        return taskTypes;
    }

    public void setTaskTypes(List<Integer> taskTypes) {
        this.taskTypes = taskTypes;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public Integer getFilterFrozen() {
        return filterFrozen;
    }

    public void setFilterFrozen(Integer filterFrozen) {
        this.filterFrozen = filterFrozen;
    }
}
