package com.dtstack.engine.master.dto;

import com.dtstack.engine.api.enums.SqlType;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-10-31 20:43
 */
public class FsyncSql {
    /**
     * sqlId, 离线传入的 uuid
     */
    private String sqlId;

    /**
     * sql 类型, {@link SqlType}
     */
    private Integer sqlType;

    /**
     * sql 文本
     */
    private String sql;

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public Integer getSqlType() {
        return sqlType;
    }

    public void setSqlType(Integer sqlType) {
        this.sqlType = sqlType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RdosSqlDTO{");
        sb.append("sqlId='").append(sqlId).append('\'');
        sb.append(", sqlType=").append(sqlType);
        sb.append(", sql='").append(sql).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
