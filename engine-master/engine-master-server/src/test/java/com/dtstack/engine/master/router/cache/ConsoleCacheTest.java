package com.dtstack.engine.master.router.cache;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.mockcontainer.RedisTemplateMock;
import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;

@MockWith(RedisTemplateMock.class)
public class ConsoleCacheTest {

    private static final ConsoleCache consoleCache = new ConsoleCache();

    @Test
    public void publishRemoveMessage() {
        consoleCache.publishRemoveMessage("1");
    }

    @Test
    public void setRedisTemplate() {
        consoleCache.setRedisTemplate(new RedisTemplate<>());
    }

    @Test
    public void afterPropertiesSet() throws Exception {
        consoleCache.setRedisTemplate(new RedisTemplate<>());
        consoleCache.afterPropertiesSet();
    }

    @Test(expected = RdosDefineException.class)
    public void afterPropertiesSet1() throws Exception {
        consoleCache.setRedisTemplate(null);
        consoleCache.afterPropertiesSet();
    }


}