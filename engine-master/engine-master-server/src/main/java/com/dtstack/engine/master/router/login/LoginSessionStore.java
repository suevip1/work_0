package com.dtstack.engine.master.router.login;


import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.router.login.domain.DtUicUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/1/18
 */
@Component
public class LoginSessionStore {
    
    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private SessionUtil sessionUtil;

    @Autowired
    private DtUicUserConnect dtUicUserConnect;

    public  <T> void createSession(String token, Class<T> clazz, Consumer<DtUicUser> dtUicUserHandler) {
        T session = sessionUtil.getUser(token, clazz);
        if (session == null) {
            token = token.intern();
            synchronized (token) {
                session = sessionUtil.getUser(token, clazz);
                if (session == null) {
                    dtUicUserConnect.getInfo(token, environmentContext.getPublicServiceNode(), dtUicUserHandler);
                }
            }
        }
    }

    public void removeSession(String token) {
        if (dtUicUserConnect.removeUicInfo(token, environmentContext.getPublicServiceNode())) {
            sessionUtil.pulish(token);
        }
    }

    public void removeSession(String token, boolean uicLogout) {
        if (uicLogout) {
            if (dtUicUserConnect.removeUicInfo(token, environmentContext.getPublicServiceNode())) {
                sessionUtil.pulish(token);
            }
        } else {
            sessionUtil.pulish(token);
        }
    }
}
