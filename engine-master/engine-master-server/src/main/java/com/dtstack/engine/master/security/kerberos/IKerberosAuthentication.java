package com.dtstack.engine.master.security.kerberos;

import com.dtstack.engine.api.domain.KerberosConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public interface IKerberosAuthentication {

    Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().getClass());

    KerberosConfig getKerberosAuthenticationConfig(KerberosReq kerberosReq);

    /**
     * kerberos 等级名
     * @return
     */
    String name();
}
