package com.dtstack.engine.common.util;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class JavaPolicyUtilsTest {

    private static boolean flag = true;

    @Before
    public void setUp() throws Exception {
        if (System.getProperty("java.security.policy") == null) {
            System.setProperty("java.security.policy", this.getClass().getClassLoader().getResource("java.policy").getPath());
        } else {
            flag = false;
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (flag) {
            System.clearProperty("java.security.policy");
        }
    }

    @Test
    public void checkJavaPolicy() {
        JavaPolicyUtils.checkJavaPolicy();
    }
}