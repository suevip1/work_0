package com.dtstack.engine.api.vo.alert;

import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2022/5/19 2:48 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlertAlarmMapBusinessVO {

    private Long id;

    private Long tenantId;

    private Integer appType;

    private Long projectId;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 告警类型 1：任务  2：基线
     */
    private Integer alarmType;

    /**
     * 创建人用户id
     */
    private Long createUserId;

    /**
     * 规则状态 0 开始 1 关闭
     */
    private Integer openStatus;

    /**
     * 额外参数 用于传入规则所需要的字段
     */
    private Map<String,Object> extraParamMap;

    /**
     * 业务数据
     */
    private List<AlertAlarmBusinessVO> alertAlarmBusinessVOS;

    /**
     * 是否支持补数据告警
     */
    private boolean supportFillData;

    public boolean isSupportFillData() {
        return supportFillData;
    }

    public void setSupportFillData(boolean supportFillData) {
        this.supportFillData = supportFillData;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(Integer alarmType) {
        this.alarmType = alarmType;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }

    public Map<String, Object> getExtraParamMap() {
        return extraParamMap;
    }

    public void setExtraParamMap(Map<String, Object> extraParamMap) {
        this.extraParamMap = extraParamMap;
    }

    public List<AlertAlarmBusinessVO> getAlertAlarmBusinessVOS() {
        return alertAlarmBusinessVOS;
    }

    public void setAlertAlarmBusinessVOS(List<AlertAlarmBusinessVO> alertAlarmBusinessVOS) {
        this.alertAlarmBusinessVOS = alertAlarmBusinessVOS;
    }
}
