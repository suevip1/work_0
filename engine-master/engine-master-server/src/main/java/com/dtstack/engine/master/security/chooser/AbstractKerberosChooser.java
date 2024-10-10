package com.dtstack.engine.master.security.chooser;

import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.security.kerberos.IKerberosAuthentication;
import com.dtstack.engine.master.security.kerberos.KerberosReq;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-11-06 15:07
 * kerberos 选择器，主要是为了区分不同的选择顺序
 */
public abstract class AbstractKerberosChooser implements IKerberosChooser {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().getClass());

    private List<IKerberosAuthentication> kerberosAuthentications = new LinkedList<>();

    @Override
    public Pair<IKerberosAuthentication, KerberosConfig> authentication(KerberosReq kerberosReq) {
        Iterator<IKerberosAuthentication> iterator = kerberosAuthentications.iterator();
        StringBuilder chain = new StringBuilder();
        while (iterator.hasNext()) {
            IKerberosAuthentication kerberosAuthentication = iterator.next();
            KerberosConfig kerberosAuthenticationConfig = kerberosAuthentication.getKerberosAuthenticationConfig(kerberosReq);
            chain.append("-->").append(kerberosAuthentication.name());
            if (kerberosAuthenticationConfig != null) {
                LOGGER.info("kerberosReq:{}, chain:{}, handler class:{}", kerberosReq.prettyLog(), chain,
                        kerberosAuthentication.getClass().getSimpleName());
                return Pair.of(kerberosAuthentication, kerberosAuthenticationConfig);
            }
        }
        return Pair.of(null, null);
    }

    @Override
    public IKerberosAuthentication getByName(String name) {
        for (IKerberosAuthentication kerberosAuthentication : kerberosAuthentications) {
            if (kerberosAuthentication.name().equals(name)) {
                return kerberosAuthentication;
            }
        }
        throw new RdosDefineException("can't find IKerberosAuthentication implement:%s", name);
    }

    protected AbstractKerberosChooser add(IKerberosAuthentication kerberosAuthentication, IKerberosAuthentication... others) {
        kerberosAuthentications.add(kerberosAuthentication);
        if (ArrayUtils.isNotEmpty(others)) {
            for (IKerberosAuthentication other : others) {
                kerberosAuthentications.add(other);
            }
        }
        return this;
    }
}
