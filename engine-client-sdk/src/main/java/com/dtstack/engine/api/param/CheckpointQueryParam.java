package com.dtstack.engine.api.param;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 检查点查询条件
 *
 * @author ：wangchuan
 * date：Created in 14:10 2022/11/16
 * company: www.dtstack.com
 */
public class CheckpointQueryParam {

    /**
     * 任务实例 id, 必填非空
     */
    @NotNull
    private String jobId;

    /**
     * flink 组件版本, 必填非空
     */
    @NotNull
    private String componentVersion;

    /**
     * 检查点类型, 为空则 checkpoint 和 savepoint 都会获取
     * 类型参考 {@link com.dtstack.engine.api.enums.CheckpointType}
     */
    private List<Integer> checkpointTypeList;

    /**
     * appId 集合, 为空则获取当前 job 下的所有 applicationId
     */
    private List<String> applicationIdList;

    /**
     * 查询触发时间范围开始时间, 单位 ms, 非必填, 为空条件不生效
     */
    private Long startTime;

    /**
     * 查询触发时间范围结束时间, 单位 ms, 非必填, 为空条件不生效
     */
    private Long endTime;

    /**
     * 获取持续时间大于等于该值的 cp, 单位 ms, 非必填, 为空条件不生效
     */
    private Long durationMin;

    /**
     * 获取持续时间小于等于该值的 cp, 单位 ms, 非必填, 为空条件不生效
     */
    private Long durationMax;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 当前页码
     */
    private Integer currentPage;

    /**
     * 最多查询历史任务数量限制, 不能超过 100
     */
    private Integer appIdLimit;

    /**
     * 任务参数
     */
    private String taskParams;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public List<Integer> getCheckpointTypeList() {
        return checkpointTypeList;
    }

    public void setCheckpointTypeList(List<Integer> checkpointTypeList) {
        this.checkpointTypeList = checkpointTypeList;
    }

    public List<String> getApplicationIdList() {
        return applicationIdList;
    }

    public void setApplicationIdList(List<String> applicationIdList) {
        this.applicationIdList = applicationIdList;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(Long durationMin) {
        this.durationMin = durationMin;
    }

    public Long getDurationMax() {
        return durationMax;
    }

    public void setDurationMax(Long durationMax) {
        this.durationMax = durationMax;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getAppIdLimit() {
        return appIdLimit;
    }

    public void setAppIdLimit(Integer appIdLimit) {
        this.appIdLimit = appIdLimit;
    }

    public String getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(String taskParams) {
        this.taskParams = taskParams;
    }
}
