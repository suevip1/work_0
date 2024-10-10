package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.impl.NodeRecoverService;
import org.junit.Test;

@MockWith(NodeRecoverControllerTest.Mock.class)
public class NodeRecoverControllerTest {

    private NodeRecoverController controller = new NodeRecoverController();

    public static class Mock {
        @MockInvoke(targetClass = NodeRecoverService.class)
        public void masterTriggerNode() {
        }
    }

    @Test
    public void masterTriggerNode() {
        controller.masterTriggerNode();
    }
}