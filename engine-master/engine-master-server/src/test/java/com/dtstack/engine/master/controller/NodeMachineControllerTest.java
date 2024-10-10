package com.dtstack.engine.master.controller;

import org.junit.Test;

public class NodeMachineControllerTest {

    private NodeMachineController controller = new NodeMachineController();


    @Test
    public void listByAppType() {
        controller.listByAppType("1");
    }

    @Test
    public void getByAppTypeAndMachineType() {
        controller.getByAppTypeAndMachineType("1",1);
    }
}