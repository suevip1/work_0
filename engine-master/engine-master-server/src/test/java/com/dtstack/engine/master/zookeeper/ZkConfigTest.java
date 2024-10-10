package com.dtstack.engine.master.zookeeper;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.HashMap;

public class ZkConfigTest {

    @Test
    public void getNodeZkAddress() {
        String nodeZkAddress = new ZkConfig().getNodeZkAddress();
    }

    @Test
    public void setNodeZkAddress() {
        new ZkConfig().setNodeZkAddress("172.16.20.195:2181,172.16.20.238:2181");
    }

    @Test
    public void getLocalAddress() {
        new ZkConfig().getLocalAddress();
    }

    @Test
    public void setLocalAddress() {
        new ZkConfig().setLocalAddress("127.0.0.1:2181");
    }

    @Test
    public void getSecurity() {
        new ZkConfig().getSecurity();
    }

    @Test
    public void setSecurity() {
        new ZkConfig().setSecurity(new HashMap<>());
    }

    @Test
    public void testSetSecurity() {
        new ZkConfig().setSecurity(JSONObject.toJSONString(new HashMap<>()));
    }
}