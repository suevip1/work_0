package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.QueueVO;
import com.dtstack.engine.api.vo.engine.EngineSupportVO;
import com.dtstack.engine.master.impl.EngineService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@MockWith(EngineControllerTest.Mock.class)
public class EngineControllerTest {

    private EngineController controller = new EngineController();

    public static class Mock{
        @MockInvoke(targetClass = EngineService.class)
        public List<QueueVO> getQueue(Long engineId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = EngineService.class)
        public List<EngineSupportVO> listSupportEngine(Long dtUicTenantId, Boolean needCommon){
            return new ArrayList<>();
        }

        }

    @Test
    public void getQueue() {
        controller.getQueue(1L);
    }

    @Test
    public void listSupportEngine() {
        controller.listSupportEngine(1L);
    }

    @Test
    public void listSupportEngineWithCommon() {
        controller.listSupportEngineWithCommon(1L,false);
    }
}