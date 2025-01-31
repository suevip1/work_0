package com.dtstack.engine.common.client.bean;


/**
 * 字段描述类
 *
 * @author jiangbo
 */
public class ColumnDTO {

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 描述
     */
    private String comment;

    /**
     * 索引
     */
    private Integer index;

    /**
     * 别名
     */
    private String alias;

    /**
     * 所在的表
     */
    private String table;

    public ColumnDTO(String name, Integer index) {
        this.name = name;
        this.index = index;
    }

    public ColumnDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", comment='" + comment + '\'' +
                ", index=" + index +
                ", alias='" + alias + '\'' +
                ", table='" + table + '\'' +
                '}';
    }

}
