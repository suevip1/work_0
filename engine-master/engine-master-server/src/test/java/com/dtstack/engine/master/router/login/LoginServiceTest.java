package com.dtstack.engine.master.router.login;

import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.master.router.login.domain.DtUicUser;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.*;

public class LoginServiceTest {

    private static final LoginService loginService = new LoginService();

    @Test
    public void login() {
        loginService.login(new DtUicUser(), "sds", new Consumer<UserDTO>() {
            @Override
            public void accept(UserDTO userDTO) {

            }
        });
    }
}