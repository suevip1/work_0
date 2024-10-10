package com.dtstack.engine.master.config;

import cn.hutool.core.lang.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class FileConfigTest {

    @Test
    public void multipartConfigElement() {
        Assert.notNull(new FileConfig().multipartConfigElement());
    }
}