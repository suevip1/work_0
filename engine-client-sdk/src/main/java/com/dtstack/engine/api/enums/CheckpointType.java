package com.dtstack.engine.api.enums;

/**
 * 检查点类型
 *
 * @author ：wangchuan
 * date：Created in 14:10 2022/11/16
 * company: www.dtstack.com
 */
public enum CheckpointType {

    /**
     * checkpoint
     */
    CHECKPOINT(1),

    /**
     * savepoint
     */
    SAVEPOINT(2);

    private final Integer type;

    CheckpointType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
