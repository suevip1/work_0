package com.dtstack.engine.master.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * abstract master handler
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-01-01 20:23
 */
public abstract class AbstractMasterHandler implements IMasterHandler {
    /**
     * 当前节点是否主节点，默认 false 否
     */
    private final AtomicBoolean isMaster = new AtomicBoolean(false);

    protected static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().getClass());

    /**
     * @param isMaster 是否切换到主节点，true:是，false:否
     */
    @Override
    public void setIsMaster(boolean isMaster) {
        this.isMaster.set(isMaster);
    }

    /**
     * 当前是否主节点
     * @return true:是, false:否
     */
    @Override
    public boolean isMaster() {
        return isMaster.get();
    }
}
