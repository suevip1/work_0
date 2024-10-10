package com.dtstack.engine.master.dto;

import com.dtstack.engine.po.ScheduleJobChainOutputParam;

import java.io.Serializable;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2023-05-04 19:50
 * 输出参数的处理结果
 */
public class JobChainParamOutputResult implements Serializable {
    private static final long serialVersionUID = 6858389375207029781L;
    /**
     * 输出参数的处理主体
     */
    private ScheduleJobChainOutputParam scheduleJobChainOutputParam;

    /**
     * 输出参数对应的任务参数
     */
    private String taskParamAppender;

    /**
     * 输出参数对应的处理脚本片段
     */
    private String scriptFragment;

    public ScheduleJobChainOutputParam getScheduleJobChainOutputParam() {
        return scheduleJobChainOutputParam;
    }

    public void setScheduleJobChainOutputParam(ScheduleJobChainOutputParam scheduleJobChainOutputParam) {
        this.scheduleJobChainOutputParam = scheduleJobChainOutputParam;
    }

    public String getTaskParamAppender() {
        return taskParamAppender;
    }

    public void setTaskParamAppender(String taskParamAppender) {
        this.taskParamAppender = taskParamAppender;
    }

    public String getScriptFragment() {
        return scriptFragment;
    }

    public void setScriptFragment(String scriptFragment) {
        this.scriptFragment = scriptFragment;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JobChainParamOutputResult{");
        sb.append("scheduleJobChainOutputParam=").append(scheduleJobChainOutputParam);
        sb.append(", taskParamAppender='").append(taskParamAppender).append('\'');
        sb.append(", scriptFragment='").append(scriptFragment).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
