package com.dtstack.engine.master.router.login;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.router.login.domain.DtUicUser;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.*;

@MockWith(BaseMock.class)
public class LoginSessionStoreTest {

    private static final LoginSessionStore loginSessionStore = new LoginSessionStore();

    @Test
    public void createSession() {
        loginSessionStore.createSession("sd", UserDTO.class, new Consumer<DtUicUser>() {
            @Override
            public void accept(DtUicUser dtUicUser) {

            }
        });
    }

}