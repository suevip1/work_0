package com.dtstack.engine.common.util;

import org.junit.Assert;
import org.junit.Test;

import javax.security.auth.login.AppConfigurationEntry;

import static org.junit.Assert.*;

public class KerberosUtilsTest {

    @Test
    public void getKrb5LoginModuleName() {
        String krb5LoginModuleName = KerberosUtils.getKrb5LoginModuleName();
        Assert.assertNotNull(krb5LoginModuleName);
    }

    @Test
    public void keytabEntry() {
        String testKeytabPath = getClass().getClassLoader().getResource("test.keytab").getPath();
        AppConfigurationEntry xs = KerberosUtils.keytabEntry(testKeytabPath, "xs");
        Assert.assertNotNull(xs);
    }

}