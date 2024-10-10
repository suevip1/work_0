package com.dtstack.engine.api.vo.user;

import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2021/5/11 7:54 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class UserVO {

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("手机号")
    private String phoneNumber;

    @ApiModelProperty("用户id")
    private Long dtuicUserId;

    @ApiModelProperty("用户邮箱")
    private String email;

    private Integer status;

    private Long defaultProjectId;

    private Integer rootUser;

    @ApiModelProperty(notes = "设置为admin时间")
    private Timestamp joinAdminTime;


    public Timestamp getJoinAdminTime() {
        return joinAdminTime;
    }

    public void setJoinAdminTime(Timestamp joinAdminTime) {
        this.joinAdminTime = joinAdminTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getDtuicUserId() {
        return dtuicUserId;
    }

    public void setDtuicUserId(Long dtuicUserId) {
        this.dtuicUserId = dtuicUserId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getDefaultProjectId() {
        return defaultProjectId;
    }

    public void setDefaultProjectId(Long defaultProjectId) {
        this.defaultProjectId = defaultProjectId;
    }

    public Integer getRootUser() {
        return rootUser;
    }

    public void setRootUser(Integer rootUser) {
        this.rootUser = rootUser;
    }
}
