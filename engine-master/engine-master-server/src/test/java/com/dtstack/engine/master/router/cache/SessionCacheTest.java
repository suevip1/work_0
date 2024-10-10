package com.dtstack.engine.master.router.cache;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;

@MockWith(BaseMock.class)
public class SessionCacheTest<K,V> {

    private static final SessionCache sessionCache = new SessionCache();

    static {
        sessionCache.setAppType(AppType.RDOS);
    }

    @Test
    public void set() {
        sessionCache.set("1","key","value");
    }

    @Test
    public void get() {
        sessionCache.get("1","key",String.class);
    }

    @Test
    public void publishRemoveMessage() {
        sessionCache.publishRemoveMessage("1");
    }

    @Test
    public void remove() {
        sessionCache.remove("1");
    }

    @Test
    public void setExpire() {
        sessionCache.setExpire(1);
    }

    @Test
    public void setRedisTemplate() {
        sessionCache.setRedisTemplate(new RedisTemplate<>());
    }


    @Test(expected = Exception.class)
    public void afterPropertiesSet() throws Exception {
        sessionCache.setAppType(null);
        sessionCache.afterPropertiesSet();
    }
}