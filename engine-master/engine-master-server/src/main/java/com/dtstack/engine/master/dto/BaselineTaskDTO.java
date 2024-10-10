package com.dtstack.engine.master.dto;

import com.dtstack.engine.api.dto.AlarmChooseTaskDTO;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/10 2:02 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class BaselineTaskDTO {

    private Long id;

    private String name;

    private Long projectId;

    private Long tenantId;

    private Integer appType;

    private Long ownerUserId;

    private String replyTime;

    private Integer earlyWarnMargin;

    private Integer openStatus;

    private Integer batchType;

    private Integer priority;

    private List<AlarmChooseTaskDTO> taskVOS;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<AlarmChooseTaskDTO> getTaskVOS() {
        return taskVOS;
    }

    public void setTaskVOS(List<AlarmChooseTaskDTO> taskVOS) {
        this.taskVOS = taskVOS;
    }
}
