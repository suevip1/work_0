package com.dtstack.engine.api.enums;

/**
 * 任务实例生成状态
 *
 * @author ：wangchuan
 * date：Created in 15:09 2022/8/31
 * company: www.dtstack.com
 */
public enum JobBuildStatus {

    /**
     * 新建
     */
    CREATE(1),

    /**
     * 生成中
     */
    RUNNING(2),

    /**
     * 已完成
     */
    FINISHED(3),

    /**
     * 生成失败
     */
    FAILED(4),

    /**
     * 自动取消
     */
    AUTO_CANCELED(5);

    /**
     * 构建状态
     */
    private final Integer status;

    JobBuildStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
