package com.dtstack.engine.master.handler;

/**
 * master handler interface
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-01-01 20:10
 */
public interface IMasterHandler {
    void setIsMaster(boolean isMaster);

    boolean isMaster();

    /**
     * 主节点执行业务逻辑
     */
    void handle();
}
