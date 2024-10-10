package com.dtstack.engine.master.security.chooser;

import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.master.security.kerberos.IKerberosAuthentication;
import com.dtstack.engine.master.security.kerberos.KerberosReq;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-11-06 16:36
 */
public interface IKerberosChooser {
    /**
     * 根据 kerberosReq 遍历迭代器，获取 KerberosConfig
     * @param kerberosReq
     * @return
     */
    Pair<IKerberosAuthentication, KerberosConfig> authentication(KerberosReq kerberosReq);

    /**
     * 获取单个认证器
     * @param name
     * @return
     */
    IKerberosAuthentication getByName(String name);
}
