package com.dtstack.engine.common.pojo.hdfs;

/**
 * hdfs读取数据返回vo
 *
 * @author luming
 * @date 2022/3/21
 */
public class HdfsDataVO {
    /**
     * 行
     */
    private Integer rowIndex;
    /**
     * 列
     */
    private Integer colIndex;
    /**
     * 数据
     */
    private String data;

    public HdfsDataVO(Integer rowIndex, Integer colIndex, String data) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.data = data;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Integer getColIndex() {
        return colIndex;
    }

    public void setColIndex(Integer colIndex) {
        this.colIndex = colIndex;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
