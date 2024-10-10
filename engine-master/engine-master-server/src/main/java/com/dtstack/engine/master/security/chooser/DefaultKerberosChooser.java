package com.dtstack.engine.master.security.chooser;

import com.dtstack.engine.master.security.kerberos.IKerberosAuthentication;
import com.dtstack.engine.master.security.KerberosHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-11-06 16:47
 */
@Component
public class DefaultKerberosChooser extends AbstractKerberosChooser implements InitializingBean {

    @Autowired
    protected KerberosHandler kerberosHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        for (IKerberosAuthentication kerberosAuthentication : kerberosHandler.allKerberosAuthentications()) {
            add(kerberosAuthentication);
        }
    }
}
