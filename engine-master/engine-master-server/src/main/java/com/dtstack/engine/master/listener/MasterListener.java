package com.dtstack.engine.master.listener;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.master.handler.IMasterHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * company: www.dtstack.com
 * @author toutian
 * create: 2019/10/22
 */
public class MasterListener implements LeaderLatchListener, Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterListener.class);

    private final static int CHECK_INTERVAL = 10000;

    private final AtomicBoolean isMaster = new AtomicBoolean(false);
    private final List<IMasterHandler> masterHandlers = new ArrayList<>();

    private final ScheduledExecutorService scheduledService;
    private LeaderLatch latch;

    public MasterListener(CuratorFramework curatorFramework,
                          String latchPath,
                          String localAddress) throws Exception {
        this.latch = new LeaderLatch(curatorFramework, latchPath, localAddress);
        this.latch.addListener(this);
        this.latch.start();

        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName()));
        scheduledService.scheduleWithFixedDelay(
                this,
                35000,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);// todo
    }

    public boolean isMaster() {
        return isMaster.get();
    }

    @Override
    public void isLeader() {
        isMaster.set(Boolean.TRUE);
    }

    @Override
    public void notLeader() {
        isMaster.set(Boolean.FALSE);
    }

    @Override
    public void close() throws Exception {
        this.latch.close();
        notLeader();
        scheduledService.shutdownNow();
    }

    public MasterListener addMasterHandler(IMasterHandler masterHandler, IMasterHandler... otherMasterHandlers) {
        this.masterHandlers.add(masterHandler);
        if (ArrayUtils.isNotEmpty(otherMasterHandlers)) {
            for (IMasterHandler handler : otherMasterHandlers) {
                this.masterHandlers.add(handler);
            }
        }
        return this;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("i am master:{} ...", isMaster.get());

            for (IMasterHandler masterHandler : this.masterHandlers) {
                masterHandler.setIsMaster(isMaster.get());
            }
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }
}
