package com.dtstack.engine.api.vo.alert;

import com.dtstack.engine.api.param.PageParam;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/6 2:47 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class AlertAlarmPageConditionVO extends PageParam {

    private List<Integer> alarmTypes;

    private String taskName;

    private String alarmName;

    private Long createUserId;

    private Integer appType;

    private Long projectId;

    private Long tenantId;

    private Integer openStatus;

    public List<Integer> getAlarmTypes() {
        return alarmTypes;
    }

    public void setAlarmTypes(List<Integer> alarmTypes) {
        this.alarmTypes = alarmTypes;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
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

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }
}
