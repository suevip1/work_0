package com.dtstack.engine.master.config;

import cn.hutool.core.lang.Assert;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import org.junit.Test;
import org.springframework.data.redis.connection.RedisNode;

import java.util.List;

@MockWith(BaseMock.class)
public class CacheConfigTest {

    private static final CacheConfig cacheConfig = new CacheConfig();

    @Test
    public void getRedisNodes() {
        List<RedisNode> redisNodes = cacheConfig.getRedisNodes();
        Assert.notEmpty(redisNodes);
    }

    @Test
    public void getSentinelAddress() {
        Assert.notEmpty(cacheConfig.getSentinelAddress());
    }

    @Test
    public void sentinelConfiguration() {
       Assert.notNull(cacheConfig.sentinelConfiguration());
    }

    @Test
    public void stringRedisTemplate() {
        Assert.notNull(cacheConfig.stringRedisTemplate(cacheConfig.jedisConnectionFactory(cacheConfig.jedisPoolConfig())));
    }

    @Test
    public void sessionCache() {
        Assert.notNull(cacheConfig.sessionCache(cacheConfig.redisTemplate(cacheConfig.jedisConnectionFactory(cacheConfig.jedisPoolConfig()))));
    }

    @Test
    public void consoleCache() {
        Assert.notNull(cacheConfig.consoleCache(cacheConfig.redisTemplate(cacheConfig.jedisConnectionFactory(cacheConfig.jedisPoolConfig()))));
    }


    @Test
    public void messageContainer() {
        Assert.notNull(cacheConfig.messageContainer(cacheConfig.jedisConnectionFactory(cacheConfig.jedisPoolConfig()),cacheConfig.rdosSubscribe(cacheConfig.redisTemplate(cacheConfig.jedisConnectionFactory(cacheConfig.jedisPoolConfig())),cacheConfig.sessionCache(cacheConfig.redisTemplate(cacheConfig.jedisConnectionFactory(cacheConfig.jedisPoolConfig()))))));
    }

    @Test
    public void sessionTopic() {
        Assert.notNull(cacheConfig.sessionTopic());
    }

    @Test
    public void consoleTopic() {
        Assert.notNull(cacheConfig.consoleTopic());
    }


    @Test
    public void errorHandler() {
        cacheConfig.errorHandler();
    }
}