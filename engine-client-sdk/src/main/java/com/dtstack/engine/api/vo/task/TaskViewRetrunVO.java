package com.dtstack.engine.api.vo.task;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/16 5:47 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class TaskViewRetrunVO {

    /**
     * 实体对象
     */
    private List<TaskViewElementVO> taskViewElementVOS;

    /**
     * 边
     */
    private List<TaskViewSideVO> taskViewSideVOS;

    public List<TaskViewElementVO> getTaskViewElementVOS() {
        return taskViewElementVOS;
    }

    public void setTaskViewElementVOS(List<TaskViewElementVO> taskViewElementVOS) {
        this.taskViewElementVOS = taskViewElementVOS;
    }

    public List<TaskViewSideVO> getTaskViewSideVOS() {
        return taskViewSideVOS;
    }

    public void setTaskViewSideVOS(List<TaskViewSideVO> taskViewSideVOS) {
        this.taskViewSideVOS = taskViewSideVOS;
    }
}
