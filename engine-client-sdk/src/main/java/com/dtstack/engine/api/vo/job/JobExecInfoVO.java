package com.dtstack.engine.api.vo.job;

/**
 * sql job 执行信息记录
 *
 * @author ：wangchuan
 * date：Created in 01:45 2023/1/16
 * company: www.dtstack.com
 */
public class JobExecInfoVO {

    /**
     * 任务 id
     */
    private String jobId;

    /**
     * 开始时间
     */
    private Long execStartTime;

    /**
     * 结束时间
     */
    private Long execEndTime;

    /**
     * 任务状态
     */
    private String status;

    /**
     * sql 总条数
     */
    private Integer sqlNum;

    /**
     * 当前执行的 sql 是第几条
     */
    private Integer currentExecSqlNum;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Long getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Long execStartTime) {
        this.execStartTime = execStartTime;
    }

    public Long getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(Long execEndTime) {
        this.execEndTime = execEndTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSqlNum() {
        return sqlNum;
    }

    public void setSqlNum(Integer sqlNum) {
        this.sqlNum = sqlNum;
    }

    public Integer getCurrentExecSqlNum() {
        return currentExecSqlNum;
    }

    public void setCurrentExecSqlNum(Integer currentExecSqlNum) {
        this.currentExecSqlNum = currentExecSqlNum;
    }
}