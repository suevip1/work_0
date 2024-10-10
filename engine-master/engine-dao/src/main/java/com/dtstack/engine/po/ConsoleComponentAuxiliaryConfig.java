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
 * 组件附属信息配置
 */
public class ConsoleComponentAuxiliaryConfig {
    private Integer id;

    /**
     * 附属信息id
     */
    private Integer auxiliaryId;

    /**
     * 配置键
     */
    private String key;

    /**
     * 配置项
     */
    private String value;

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

    public Integer getAuxiliaryId() {
        return auxiliaryId;
    }

    public void setAuxiliaryId(Integer auxiliaryId) {
        this.auxiliaryId = auxiliaryId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key == null ? null : key.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
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
}