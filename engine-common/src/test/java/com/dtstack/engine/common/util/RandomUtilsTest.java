package com.dtstack.engine.common.util;

import com.google.common.collect.Lists;
import org.junit.Test;

import static org.junit.Assert.*;

public class RandomUtilsTest {

    @Test
    public void getRandom() {
        RandomUtils.getRandom();
    }

    @Test
    public void getRandomInt() {
        RandomUtils.getRandomInt(10);
    }

    @Test
    public void getRandomValueFromMap() {
        RandomUtils.getRandomValueFromMap(Lists.newArrayList(1,32,3));
    }
}