package com.dtstack.engine.api.vo.task;

import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2022/5/17 10:31 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class TaskViewSideVO {

    private String taskKey;

    private String parentTaskKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskViewSideVO that = (TaskViewSideVO) o;
        return Objects.equals(taskKey, that.taskKey) && Objects.equals(parentTaskKey, that.parentTaskKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskKey, parentTaskKey);
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getParentTaskKey() {
        return parentTaskKey;
    }

    public void setParentTaskKey(String parentTaskKey) {
        this.parentTaskKey = parentTaskKey;
    }
}
