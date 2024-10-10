package com.dtstack.engine.common.util;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

public class AddressUtilTest {

    @Test
    public void resolveLocalAddresses() {
        AddressUtil.resolveLocalAddresses(false);
    }

    @Test
    public void resolveLocalIps() {
        AddressUtil.resolveLocalIps(false);
    }

    @Test
    public void getOneIp() {
        AddressUtil.getOneIp();
    }

    @Test
    public void ping() {
        AddressUtil.ping("www.baidu.com");
    }

    @Test
    public void checkAddrIsLocal() {
        AddressUtil.checkAddrIsLocal("");
    }

    @Test
    public void checkServiceIsSame() throws Exception {
        AddressUtil.checkServiceIsSame("127.0.0.1",8090,"192.168.96.203",8090);
    }

    @Test
    public void telnet() {
        AddressUtil.telnet("127.0.0.1",8090);
    }

    @Test
    public void testInetAddressGetByName() throws UnknownHostException {
        String host = "rdos.dtstack.dttest.net";
        // 172.16.84.222
        System.out.println(InetAddress.getByName(host).getHostAddress());

        host = "172-16-84-222";
        // 172.16.84.222
        System.out.println(InetAddress.getByName(host).getHostAddress());

        host = "cdp01";
        // 172.16.84.222
        System.out.println(InetAddress.getByName(host).getHostAddress());

        host = "172.16.84.222";
        System.out.println(InetAddress.getByName(host).getHostAddress());
    }

    @Test
    public void testIsDomain() {
        assertEquals(true, AddressUtil.isDomain("rdos.dtstack.dttest.net"));
        assertEquals(true, AddressUtil.isDomain("172-16-84-222"));
        assertEquals(true, AddressUtil.isDomain("cdp01"));
        assertEquals(true, AddressUtil.isDomain("www.baidu.com"));
        assertEquals(true, AddressUtil.isDomain("127.0.0.1"));
        assertEquals(true, AddressUtil.isDomain("baidu.com"));
        assertEquals(true, AddressUtil.isDomain("hades-web01.devl.jxlife.com.cn"));

        assertEquals(true, AddressUtil.isDomain("1w.126.com"));
        assertEquals(true, AddressUtil.isDomain("abc.abc.12-"));
        assertEquals(false, AddressUtil.isDomain("-abc.abc.12-"));
        assertEquals(false, AddressUtil.isDomain("a-a..a"));
    }

    @Test
    public void testIsCorrectIp() {
        assertEquals(false, AddressUtil.isCorrectIp("rdos.dtstack.dttest.net"));
        assertEquals(false, AddressUtil.isCorrectIp("172-16-84-222"));
        assertEquals(false, AddressUtil.isCorrectIp("cdp01"));
        assertEquals(false, AddressUtil.isCorrectIp("www.baidu.com"));
        assertEquals(true, AddressUtil.isCorrectIp("127.0.0.1"));
        assertEquals(false, AddressUtil.isCorrectIp("baidu.com"));

        assertEquals(false, AddressUtil.isCorrectIp("1w.126.com"));
        assertEquals(false, AddressUtil.isCorrectIp("abc.abc.12-"));
        assertEquals(false, AddressUtil.isCorrectIp("-abc.abc.12-"));
        assertEquals(false, AddressUtil.isCorrectIp("a-a..a"));
    }
}