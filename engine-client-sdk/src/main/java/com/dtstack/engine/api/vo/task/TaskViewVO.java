package com.dtstack.engine.api.vo.task;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/23 4:57 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class TaskViewVO {

    private List<TaskKeyVO> taskKeyVOS;

    private Integer level;

    private Integer directType;

    public List<TaskKeyVO> getTaskKeyVOS() {
        return taskKeyVOS;
    }

    public void setTaskKeyVOS(List<TaskKeyVO> taskKeyVOS) {
        this.taskKeyVOS = taskKeyVOS;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getDirectType() {
        return directType;
    }

    public void setDirectType(Integer directType) {
        this.directType = directType;
    }
}
