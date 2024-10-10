package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import org.junit.Test;

@MockWith(BaseMock.class)
public class SM2ControllerTest {

    @Test
    public void getSM2PublicKey() {
        new SM2Controller().getSM2PublicKey();
    }
}