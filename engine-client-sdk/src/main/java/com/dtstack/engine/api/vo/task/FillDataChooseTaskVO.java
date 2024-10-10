package com.dtstack.engine.api.vo.task;

import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2021/9/9 5:48 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataChooseTaskVO {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 应用类型
     */
    private Integer appType;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof FillDataChooseTaskVO) {
            FillDataChooseTaskVO that = (FillDataChooseTaskVO) o;
            return Objects.equals(taskId, that.taskId) &&
                    Objects.equals(appType, that.appType);
        } else if (o instanceof String) {
            String taskKey  = taskId+"-"+appType;
            return Objects.equals(taskKey,o.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, appType);
    }
}
