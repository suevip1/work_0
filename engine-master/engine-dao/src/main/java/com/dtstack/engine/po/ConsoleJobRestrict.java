package com.dtstack.engine.po;

import java.io.Serializable;
import java.util.Date;

/**
 * 实例限制提交配置
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-02 17:03
 */
public class ConsoleJobRestrict implements Serializable {
    private static final long serialVersionUID = 4685508545735439535L;

    private Long id;

    /**
     * 限制时间起始
     */
    private Date restrictStartTime;

    /**
     * 限制时间终止
     */
    private Date restrictEndTime;

    /**
     * 规则生效时间
     */
    private Date effectiveTime;

    /**
     * 应用状态:0等待执行、1执行中、2关闭、3无效、4过期
     * @see com.dtstack.engine.master.enums.JobRestrictStatusEnum
     */
    private Integer status;

    /**
     * 新增时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 发起操作的用户
     */
    private Long createUserId;

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

    public Date getRestrictStartTime() {
        return restrictStartTime;
    }

    public void setRestrictStartTime(Date restrictStartTime) {
        this.restrictStartTime = restrictStartTime;
    }

    public Date getRestrictEndTime() {
        return restrictEndTime;
    }

    public void setRestrictEndTime(Date restrictEndTime) {
        this.restrictEndTime = restrictEndTime;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}