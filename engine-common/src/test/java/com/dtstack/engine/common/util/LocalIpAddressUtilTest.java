package com.dtstack.engine.common.util;

import org.junit.Assert;
import org.junit.Test;

public class LocalIpAddressUtilTest {

    @Test
    public void resolveLocalAddresses() {
        Assert.assertNotNull(LocalIpAddressUtil.resolveLocalAddresses());
    }

    @Test
    public void resolveLocalIps() {
        Assert.assertNotNull(LocalIpAddressUtil.resolveLocalIps());
    }

    @Test
    public void getLocalHostName() {
        Assert.assertNotNull( LocalIpAddressUtil.getLocalHostName());
    }

    @Test
    public void getLocalAddress() {
        Assert.assertNotNull(LocalIpAddressUtil.getLocalAddress());
    }
}