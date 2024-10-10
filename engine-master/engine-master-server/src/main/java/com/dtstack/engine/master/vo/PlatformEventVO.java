package com.dtstack.engine.master.vo;

/**
 * @author yuebai
 * @date 2020-08-13
 */
public class PlatformEventVO {

    private Long id;

    /**
     * 由于接口不需要登陆，添加加密验证
     */
    private String sign;

    private String eventCode;

    private Long userId;
    private String userName;
    /**
     * 姓名
     */
    private String fullName;
    /**
     *
     */
    private String company;
    /**
     * 邮箱
     */
    private String email;

    private String token;
    private String phone;

    private Long tenantId;
    private Long dtUicTenantId;

    /**
     * 租户名字
     */
    private String tenantName;


    /**
     * 租户描述信息
     */
    private String tenantDesc;

    private Long projectId;

    private Integer appType;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantDesc() {
        return tenantDesc;
    }

    public void setTenantDesc(String tenantDesc) {
        this.tenantDesc = tenantDesc;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Long dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PlatformEventVO{");
        sb.append("id=").append(id);
        sb.append(", sign='").append(sign).append('\'');
        sb.append(", eventCode='").append(eventCode).append('\'');
        sb.append(", userId=").append(userId);
        sb.append(", userName='").append(userName).append('\'');
        sb.append(", fullName='").append(fullName).append('\'');
        sb.append(", company='").append(company).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", token='").append(token).append('\'');
        sb.append(", phone='").append(phone).append('\'');
        sb.append(", tenantId=").append(tenantId);
        sb.append(", dtUicTenantId=").append(dtUicTenantId);
        sb.append(", tenantName='").append(tenantName).append('\'');
        sb.append(", tenantDesc='").append(tenantDesc).append('\'');
        sb.append(", projectId=").append(projectId);
        sb.append(", appType=").append(appType);
        sb.append('}');
        return sb.toString();
    }
}
