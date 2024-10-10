package com.dtstack.engine.master.listener;

import com.dtstack.engine.master.handler.IMasterHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

import static org.junit.Assert.*;

public class MasterListenerTest {

    private static MasterListener masterListener;

    static {
        try {
            CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                    .connectString("127.0.0.1").retryPolicy(new ExponentialBackoffRetry(1000, 3))
                    .connectionTimeoutMs(3000)
                    .sessionTimeoutMs(30000).build();
            masterListener = new MasterListener(zkClient,"/","127.0.0.1");
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    @Test
    public void isMaster() {
        masterListener.isMaster();
    }


    @Test
    public void addMasterHandler() {
        IMasterHandler emptyHandler = new IMasterHandler() {
            @Override
            public void setIsMaster(boolean isMaster) {
            }
            @Override
            public boolean isMaster() {return false;}
            @Override
            public void handle() {}
        };
        masterListener.addMasterHandler(emptyHandler,emptyHandler);
    }

    @Test
    public void run() {
        masterListener.run();
    }

    @Test
    public void isLeader() {
        masterListener.isLeader();
    }

    @Test
    public void notLeader() {
        masterListener.notLeader();
    }

    @Test
    public void testRun() {
        IMasterHandler emptyHandler = new IMasterHandler() {
            @Override
            public void setIsMaster(boolean isMaster) {
            }
            @Override
            public boolean isMaster() {return false;}
            @Override
            public void handle() {}
        };
        masterListener.addMasterHandler(emptyHandler,emptyHandler);
        masterListener.run();
    }

    @Test
    public void close() throws Exception {
        masterListener.close();
    }
}