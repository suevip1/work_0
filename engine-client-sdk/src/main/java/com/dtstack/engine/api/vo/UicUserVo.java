package com.dtstack.engine.api.vo;

import java.util.Date;

/**
 * @auther: shuxing
 * @date: 2022/3/14 15:24 周一
 * @email: shuxing@dtstack.com
 * @description:
 */
public class UicUserVo {


    private Long userId;
    private String userName;
    private String fullName;
    private Boolean active;
    private String email;
    private String phone;
    private String company;
    private Date joinTime;
    private Date createTime;
    private Boolean tenantAdmin;
    private Boolean root;
    private Boolean tenantOwner;
    private Long externalId;
    private Date userTime;
    private String secretName;
    private Long secretId;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUserTime(Date userTime) {
        this.userTime = userTime;
    }

    public Boolean getTenantAdmin() {
        return tenantAdmin;
    }

    public void setTenantAdmin(Boolean tenantAdmin) {
        this.tenantAdmin = tenantAdmin;
    }

    public Boolean getRoot() {
        return root;
    }

    public void setRoot(Boolean root) {
        this.root = root;
    }

    public Boolean getTenantOwner() {
        return tenantOwner;
    }

    public void setTenantOwner(Boolean tenantOwner) {
        this.tenantOwner = tenantOwner;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public Date getUserTime() {
        return userTime;
    }

    public String getSecretName() {
        return secretName;
    }

    public void setSecretName(String secretName) {
        this.secretName = secretName;
    }

    public Long getSecretId() {
        return secretId;
    }

    public void setSecretId(Long secretId) {
        this.secretId = secretId;
    }

    @Override
    public String toString() {
        return "UicUserVo{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", active=" + active +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", company='" + company + '\'' +
                ", joinTime=" + joinTime +
                ", createTime=" + createTime +
                ", tenantAdmin=" + tenantAdmin +
                ", root=" + root +
                ", tenantOwner=" + tenantOwner +
                ", externalId=" + externalId +
                ", userTime=" + userTime +
                ", secretName='" + secretName + '\'' +
                ", secretId=" + secretId +
                '}';
    }
}
