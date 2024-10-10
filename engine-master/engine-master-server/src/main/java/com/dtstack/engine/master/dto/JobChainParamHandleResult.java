package com.dtstack.engine.master.dto;

/**
 * 任务上下游参数处理器执行结果
 * @author qiuyun
 * @version 1.0
 * @date 2022-03-17 10:50
 */
public class JobChainParamHandleResult {
    /**
     * 拼接完成的整个 sql
     */
    private String sql;

    /**
     * 拼接完成的整个 taskParams
     */
    private String taskParams;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(String taskParams) {
        this.taskParams = taskParams;
    }

    public JobChainParamHandleResult() {
    }

    public JobChainParamHandleResult(String sql, String taskParams) {
        this.sql = sql;
        this.taskParams = taskParams;
    }

    @Override
    public String toString() {
        return "JobChainParamHandleResult{" +
                "sql='" + sql + '\'' +
                ", taskParams='" + taskParams + '\'' +
                '}';
    }
}
