package com.dtstack.engine.common.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class AESUtilTest {

    @Test
    public void encrypt() throws Exception {
        Assert.assertNotNull(AESUtil.encrypt("dag"));
    }

    @Test
    public void decrypt() throws Exception {
        Assert.assertNotNull(AESUtil.decrypt(AESUtil.encrypt("dag")));
    }

    @Test
    public void parseByte2HexStr() {
        Assert.assertNotNull(AESUtil.parseByte2HexStr(new byte[]{'a', 'b', 'c'}));
    }

    @Test
    public void parseHexStr2Byte() {
        Assert.assertNotNull(AESUtil.parseHexStr2Byte("abc"));
    }
}