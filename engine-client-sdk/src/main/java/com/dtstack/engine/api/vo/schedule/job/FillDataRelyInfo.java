package com.dtstack.engine.api.vo.schedule.job;

import com.dtstack.engine.api.vo.task.TaskKeyVO;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/10/28 11:19 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class FillDataRelyInfo {

    /**
     * 补数据及其下游的任务
     */
    private List<TaskKeyVO> rootTasks;

    /**
     * 是否过滤冻结任务 0 不过滤 默认
     * 1. 过滤
     */
    private Integer filterFrozen;

    public List<TaskKeyVO> getRootTasks() {
        return rootTasks;
    }

    public void setRootTasks(List<TaskKeyVO> rootTasks) {
        this.rootTasks = rootTasks;
    }

    public Integer getFilterFrozen() {
        return filterFrozen;
    }

    public void setFilterFrozen(Integer filterFrozen) {
        this.filterFrozen = filterFrozen;
    }
}
