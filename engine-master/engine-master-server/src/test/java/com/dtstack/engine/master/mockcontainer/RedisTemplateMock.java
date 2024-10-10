package com.dtstack.engine.master.mockcontainer;

import com.alibaba.testable.core.annotation.MockInvoke;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * @author leon
 * @date 2022-07-01 09:46
 **/
public class RedisTemplateMock {

    @MockInvoke(targetClass = RedisTemplate.class)
    public void convertAndSend(String channel, Object message) {}

    @MockInvoke(targetClass = RedisTemplate.class)
    public RedisSerializer<?> getValueSerializer() {
        return new RedisSerializer<Object>() {
            @Override
            public byte[] serialize(Object o) throws SerializationException {
                return new byte[0];
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                return null;
            }
        };
    }
}
