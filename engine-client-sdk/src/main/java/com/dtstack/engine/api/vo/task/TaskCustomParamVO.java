package com.dtstack.engine.api.vo.task;


/**
 * task 自定义参数集合
 *
 * @author ：wangchuan
 * date：Created in 14:09 2022/7/26
 * company: www.dtstack.com
 */
public class TaskCustomParamVO {

    /**
     * 任务 id
     */
    private Long taskId;

    /**
     * 任务参数信息
     */
    private String taskParamsToReplace;

    /**
     * 参数类型 0 自定义参数 1 项目参数 2 全局参数
     */
    private String paramType;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskParamsToReplace() {
        return taskParamsToReplace;
    }

    public void setTaskParamsToReplace(String taskParamsToReplace) {
        this.taskParamsToReplace = taskParamsToReplace;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }
}
