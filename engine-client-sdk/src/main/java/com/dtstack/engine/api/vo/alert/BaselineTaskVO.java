package com.dtstack.engine.api.vo.alert;

import com.dtstack.engine.api.vo.BaselineBatchVO;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/10 2:02 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class BaselineTaskVO {

    private Long id;

    private String name;

    private Long projectId;

    private Long tenantId;

    private Integer appType;

    private Long ownerUserId;

    @Deprecated
    private String replyTime;

    private Integer earlyWarnMargin;

    private Integer openStatus;

    /**
     * 1 单批次 2 多批次
     */
    private Integer batchType;

    private String cycTime;

    /**
     * 优先级
     */
    private Integer priority;

    private List<AlarmChooseTaskVO> taskVOS;

    private List<BaselineBatchVO> baselineBatchDTOS;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Integer getEarlyWarnMargin() {
        return earlyWarnMargin;
    }

    public void setEarlyWarnMargin(Integer earlyWarnMargin) {
        this.earlyWarnMargin = earlyWarnMargin;
    }

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }

    public Integer getBatchType() {
        return batchType;
    }

    public void setBatchType(Integer batchType) {
        this.batchType = batchType;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<AlarmChooseTaskVO> getTaskVOS() {
        return taskVOS;
    }

    public void setTaskVOS(List<AlarmChooseTaskVO> taskVOS) {
        this.taskVOS = taskVOS;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public List<BaselineBatchVO> getBaselineBatchDTOS() {
        return baselineBatchDTOS;
    }

    public void setBaselineBatchDTOS(List<BaselineBatchVO> baselineBatchDTOS) {
        this.baselineBatchDTOS = baselineBatchDTOS;
    }
}
