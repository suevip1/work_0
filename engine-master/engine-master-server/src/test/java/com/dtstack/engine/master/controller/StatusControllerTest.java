package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.master.impl.RoleService;
import com.dtstack.engine.master.router.login.SessionUtil;
import org.junit.Test;

@MockWith(StatusControllerTest.StatusControllerMock.class)
public class StatusControllerTest {

    private static final StatusController statusController = new StatusController();

    static class StatusControllerMock {

        @MockInvoke(targetClass = SessionUtil.class)
        public <T> T getUser(String token, Class<T> clazz) {
            return (T) new UserDTO();
        }

        @MockInvoke(targetClass = RoleService.class)
        public boolean checkIsSysAdmin(UserDTO user) {
            return true;
        }

    }

    @Test
    public void status() {
        statusController.status("");
    }

    @Test
    public void value() {
        statusController.value("");
    }
}