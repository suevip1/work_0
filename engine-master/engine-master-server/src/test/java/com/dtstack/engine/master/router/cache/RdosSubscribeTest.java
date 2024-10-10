package com.dtstack.engine.master.router.cache;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import org.junit.Test;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;


@MockWith(BaseMock.class)
public class RdosSubscribeTest {

    private static final RdosSubscribe rdosSubscribe = new RdosSubscribe();


    @Test
    public void onMessage() {

        rdosSubscribe.setCallBack(stringStringPair -> {});

        rdosSubscribe.onMessage(new Message() {
            @Override
            public byte[] getBody() {
                return new byte[0];
            }

            @Override
            public byte[] getChannel() {
                return new byte[0];
            }
        },new byte[]{});
    }

    @Test
    public void setRedisTemplate() {
        rdosSubscribe.setRedisTemplate(new RedisTemplate<>());
    }

    @Test
    public void setSessionCache() {
        rdosSubscribe.setSessionCache(new SessionCache());
    }

}