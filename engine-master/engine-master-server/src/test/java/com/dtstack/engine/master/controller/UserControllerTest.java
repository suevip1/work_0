package com.dtstack.engine.master.controller;

import org.junit.Test;

public class UserControllerTest {

    private static final UserController userController = new UserController();

    @Test
    public void findUser() {
        userController.findUser();
    }



    static class UserControllerMock {

    }
}