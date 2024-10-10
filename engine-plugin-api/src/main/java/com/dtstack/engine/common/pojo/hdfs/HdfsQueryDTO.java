package com.dtstack.engine.common.pojo.hdfs;

/**
 * hdfs接口查询参数
 *
 * @author luming
 * @date 2022/3/21
 */
public class HdfsQueryDTO {
    /**
     * 文件类型，支持orc,text,parquet
     */
    private String fileType;
    /**
     * hdfs文件/文件夹路径，绝对路径
     */
    private String hdfsPath;
    /**
     * 限制读取行数，实际数据行数超过此行数则抛出异常
     */
    private Integer limit;
    /**
     * text类型文件需提供分隔符
     */
    private String separator;
    /**
     * 当path为文件夹时，是否递归查询
     * default : false
     */
    private Boolean isRecursion;
    /**
     * 指定查询数据内列索引，可组合使用
     * required : true
     */
    private Integer colIndex;
    /**
     * 指定查询数据内行索引，可组合使用
     * required : false
     */
    private Integer rowIndex;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getHdfsPath() {
        return hdfsPath;
    }

    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public Boolean getRecursion() {
        return isRecursion;
    }

    public void setRecursion(Boolean recursion) {
        isRecursion = recursion;
    }

    public Integer getColIndex() {
        return colIndex;
    }

    public void setColIndex(Integer colIndex) {
        this.colIndex = colIndex;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HdfsQueryDTO{");
        sb.append("fileType='").append(fileType).append('\'');
        sb.append(", hdfsPath='").append(hdfsPath).append('\'');
        sb.append(", limit=").append(limit);
        sb.append(", separator='").append(separator).append('\'');
        sb.append(", isRecursion=").append(isRecursion);
        sb.append(", colIndex=").append(colIndex);
        sb.append(", rowIndex=").append(rowIndex);
        sb.append('}');
        return sb.toString();
    }
}
