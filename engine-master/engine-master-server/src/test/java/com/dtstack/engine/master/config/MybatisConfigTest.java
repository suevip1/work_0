package com.dtstack.engine.master.config;

import cn.hutool.core.lang.Assert;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import org.junit.Test;

import static org.junit.Assert.*;

@MockWith(BaseMock.class)
public class MybatisConfigTest {

    private static final MybatisConfig mybatisConfig = new MybatisConfig();

    @Test
    public void transactionManager() throws Exception {
        Assert.notNull(mybatisConfig.transactionManager());
    }

    @Test
    public void sqlSessionTemplate() throws Exception {
        Assert.notNull(mybatisConfig.sqlSessionTemplate());
    }

    @Test
    public void sqlSessionFactory() throws Exception {
        mybatisConfig.sqlSessionFactory();
    }
}