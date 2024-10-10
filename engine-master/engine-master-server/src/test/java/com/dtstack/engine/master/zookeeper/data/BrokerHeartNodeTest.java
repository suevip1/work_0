package com.dtstack.engine.master.zookeeper.data;

import org.junit.Test;

public class BrokerHeartNodeTest {

    private static final BrokerHeartNode node = new BrokerHeartNode();

    @Test
    public void getSeq() {
        node.getSeq();
    }

    @Test
    public void setSeq() {
        node.setSeq(1L);
    }

    @Test
    public void getAlive() {
        node.getAlive();
    }

    @Test
    public void setAlive() {
        node.setAlive(true);
    }

    @Test
    public void initBrokerHeartNode() {
        BrokerHeartNode.initBrokerHeartNode();
    }

    @Test
    public void initNullBrokerHeartNode() {
        BrokerHeartNode.initNullBrokerHeartNode();
    }

    @Test
    public void copy() {
        BrokerHeartNode.copy(new BrokerHeartNode(),node,true);
    }
}