package com.dtstack.engine.api.vo.stream;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2023-05-10 11:46
 */
public class FlinkQueryResultVO implements Serializable {
    private static final long serialVersionUID = 7489523560992743967L;
    /**
     * 是否成功
     */
    private boolean success = false;

    /**
     * 异常信息
     */
    private String msg;

    /**
     * 任务实例 id
     */
    private String taskId;

    /**
     * flink id
     */
    private String engineJobId;

    /**
     * 查询结果
     */
    private List<Map<String, Object>> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getEngineJobId() {
        return engineJobId;
    }

    public void setEngineJobId(String engineJobId) {
        this.engineJobId = engineJobId;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public FlinkQueryResultVO() {}

    private FlinkQueryResultVO(String msg) {
        this.msg = msg;
        this.success = false;
    }

    private FlinkQueryResultVO(List<Map<String, Object>> data) {
        this.data = data;
        this.success = true;
    }

    public static FlinkQueryResultVO success(List<Map<String, Object>> data) {
        return new FlinkQueryResultVO(data);
    }

    public static FlinkQueryResultVO error(String msg) {
        return new FlinkQueryResultVO(msg);
    }
}