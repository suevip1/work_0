package com.dtstack.engine.master.router.login;

import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.master.router.cache.SessionCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author toutian
 */
@Component
public class SessionUtil {

    private final static String USER_KEY = "user";

    public final static String AUTH_KEY = "authCode";

    @Autowired
    private SessionCache sessionCache;

    public <T> T getValue(String token, String key, Class<T> clazz) {
        return sessionCache.get(getSessionId(token), key, clazz);
    }

    public void setValue(String token, String key, Object value) {
        sessionCache.set(getSessionId(token), key, value);
    }

    public <T> T getUser(String token, Class<T> clazz) {
        return sessionCache.get(getSessionId(token), USER_KEY, clazz);
    }

    public List<String> getAuthKey(String token) {
        return sessionCache.get(getSessionId(token), AUTH_KEY, List.class);
    }

    public void setUser(String token, Object value) {
        sessionCache.set(getSessionId(token), USER_KEY, value);
    }

    public void pulish(String token) {
        sessionCache.publishRemoveMessage(getSessionId(token));
    }

    public void remove(String token) {
        sessionCache.remove(getSessionId(token));
    }

    public void setContext(ApplicationContext context) {
        sessionCache = context.getBean("sessionCache", SessionCache.class);
    }

    public String getSessionId(String token) {
        return MD5Util.getMd5String(token);
    }

}
