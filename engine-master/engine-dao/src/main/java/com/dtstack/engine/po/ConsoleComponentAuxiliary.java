package com.dtstack.engine.po;

import java.util.Date;

/**
 * 
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-04-14 20:13
 */

/**
 * 组件附属信息
 */
public class ConsoleComponentAuxiliary {
    private Integer id;

    /**
     * 集群id
     */
    private Long clusterId;

    /**
     * 组件类型
     */
    private Integer componentTypeCode;

    /**
     * 配置类型,如 KNOX
     */
    private String type;

    /**
     * 是否开启，默认为关
     */
    private Integer open;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 0正常 1逻辑删除
     */
    private Integer isDeleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getOpen() {
        return open;
    }

    public void setOpen(Integer open) {
        this.open = open;
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

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public ConsoleComponentAuxiliary() {
    }

    public ConsoleComponentAuxiliary(Long clusterId, Integer componentTypeCode) {
        this.clusterId = clusterId;
        this.componentTypeCode = componentTypeCode;
    }

    public ConsoleComponentAuxiliary(Long clusterId, Integer componentTypeCode, String type) {
        this.clusterId = clusterId;
        this.componentTypeCode = componentTypeCode;
        this.type = type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConsoleComponentAuxiliary{");
        sb.append("id=").append(id);
        sb.append(", clusterId=").append(clusterId);
        sb.append(", componentTypeCode=").append(componentTypeCode);
        sb.append(", type='").append(type).append('\'');
        sb.append(", open=").append(open);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append('}');
        return sb.toString();
    }
}