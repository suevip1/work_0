package com.dtstack.engine.master.vo;

/**
 * 实例限制提交配置
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-02 17:03
 */
public class ConsoleJobRestrictVO {
    private Long id;

    /**
     * 限制时间起始
     */
    private String restrictStartTime;

    /**
     * 限制时间终止
     */
    private String restrictEndTime;

    /**
     * 规则生效时间
     */
    private String effectiveTime;


    /**
     * 是否有效，0否，1是
     */
    private Integer isEffective;

    /**
     * 应用状态:0等待执行、1执行中、2关闭、3无效、4过期
     */
    private Integer status;

    /**
     * 新增时间
     */
    private String gmtCreate;

    /**
     * 修改时间
     */
    private String gmtModified;

    /**
     * 发起操作的用户
     */
    private Long createUserId;

    /**
     * 发起操作的用户名称
     */
    private String createUserName;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRestrictStartTime() {
        return restrictStartTime;
    }

    public void setRestrictStartTime(String restrictStartTime) {
        this.restrictStartTime = restrictStartTime;
    }

    public String getRestrictEndTime() {
        return restrictEndTime;
    }

    public void setRestrictEndTime(String restrictEndTime) {
        this.restrictEndTime = restrictEndTime;
    }

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(Integer isEffective) {
        this.isEffective = isEffective;
    }
}