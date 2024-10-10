package com.dtstack.engine.master.zookeeper;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.ZkServiceMock;
import org.junit.Test;

@MockWith(ZkServiceMock.class)
public class ZkServiceTest {

    private static final ZkService zkService = new ZkService();

    @Test
    public void afterPropertiesSet() throws Exception {
        zkService.afterPropertiesSet();
    }

    @Test
    public void kerberosLogin() {
    }

    @Test
    public void updateSynchronizedLocalBrokerHeartNode() {
    }

    @Test
    public void createNodeIfNotExists() {
    }

    @Test
    public void getBrokerHeartNode() {
        zkService.getBrokerHeartNode("127.0.0.1");
    }

    @Test
    public void getBrokersChildren() {
        zkService.getBrokersChildren();
    }

    @Test
    public void getAliveBrokersChildren() {
        zkService.getAliveBrokersChildren();
    }

    @Test
    public void getAllBrokerWorkersNode() {
        zkService.getAllBrokerWorkersNode();
    }

    @Test
    public void getLocalAddress() {
        zkService.getLocalAddress();
    }

    @Test
    public void disableBrokerHeartNode() {
    }

    @Test
    public void destroy() throws Exception {
        zkService.destroy();
    }

    @Test
    public void setEnvironmentContext() {
    }
}