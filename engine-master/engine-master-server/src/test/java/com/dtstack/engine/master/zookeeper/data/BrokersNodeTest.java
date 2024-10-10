package com.dtstack.engine.master.zookeeper.data;

import org.junit.Test;

import static org.junit.Assert.*;

public class BrokersNodeTest {

    @Test
    public void getMaster() {
        new BrokersNode().getMaster();
    }

    @Test
    public void setMaster() {
        new BrokersNode().setMaster("");
    }

    @Test
    public void initBrokersNode() {
        BrokersNode.initBrokersNode();
    }
}