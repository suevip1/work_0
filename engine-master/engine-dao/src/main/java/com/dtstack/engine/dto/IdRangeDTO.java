package com.dtstack.engine.dto;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-04-20 19:53
 */
public class IdRangeDTO {
    /**
     * 起始 id
     */
    private long startId;
    /**
     * 截止 id
     */
    private long endId;

    public long getStartId() {
        return startId;
    }

    public void setStartId(long startId) {
        this.startId = startId;
    }

    public long getEndId() {
        return endId;
    }

    public void setEndId(long endId) {
        this.endId = endId;
    }

    public IdRangeDTO() {
    }

    public IdRangeDTO(long startId, long endId) {
        this.startId = startId;
        this.endId = endId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IdRangeDTO{");
        sb.append("startId=").append(startId);
        sb.append(", endId=").append(endId);
        sb.append('}');
        return sb.toString();
    }
}
