package com.dtstack.engine.common;

import java.io.Serializable;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2023-05-12 19:16
 */
public class FsyncSqlParamInfo implements Serializable {
    private static final long serialVersionUID = 6128386744391663446L;
    /**
     * 离线落盘 sql
     */
    private String fsyncSql;

    /**
     * 展示最大条数
     */
    private Integer maxNum;

    public String getFsyncSql() {
        return fsyncSql;
    }

    public void setFsyncSql(String fsyncSql) {
        this.fsyncSql = fsyncSql;
    }

    public Integer getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FsyncSqlParamInfo{");
        sb.append("fsyncSql='").append(fsyncSql).append('\'');
        sb.append(", maxNum=").append(maxNum);
        sb.append('}');
        return sb.toString();
    }
}