package com.dtstack.engine.common.security;

import org.junit.Test;

import static org.junit.Assert.*;

public class NoExitSecurityManagerTest {

    NoExitSecurityManager noExitSecurityManager = new NoExitSecurityManager();

    @Test
    public void checkPermission() {
        noExitSecurityManager.checkPermission(null);
    }

    @Test
    public void testCheckPermission() {
        noExitSecurityManager.checkPermission(null,null);
    }

    @Test(expected = Exception.class)
    public void checkExit() {
        noExitSecurityManager.checkExit(1);
    }
}