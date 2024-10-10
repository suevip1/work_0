package com.dtstack.engine.master.security.kerberos;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.engine.SourceConstant;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.enums.OpenKerberosEnum;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.Optional;

import static com.dtstack.dtcenter.common.convert.Consistent.*;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-11-06 20:47
 */
public class DsKerberosAuthentication implements IKerberosAuthentication {

    public static final String DS = "Ds";

    @Override
    public KerberosConfig getKerberosAuthenticationConfig(KerberosReq kerberosReq) {
        JSONObject dataJson = kerberosReq.getDataJson();
        JSONObject dataJsonKerberosConfig = dataJson.getJSONObject(GlobalConst.KERBEROS_CONFIG);
        if (dataJsonKerberosConfig == null) {
            return null;
        }
        if (!dataJsonKerberosConfig.containsKey(SourceConstant.OPEN_KERBEROS)) {
            return null;
        }

        String kerberosDir = dataJsonKerberosConfig.getString(KERBEROS_PATH_KEY);
        if (StringUtils.isEmpty(kerberosDir)) {
            return null;
        }
        String remoteDir = dataJsonKerberosConfig.getString("remotePath");

        KerberosConfig kerberosConfig = new KerberosConfig();
        kerberosConfig.setOpenKerberos(OpenKerberosEnum.OPEN.getCode());
        kerberosConfig.setRemotePath(remoteDir);
        kerberosConfig.setName(dataJsonKerberosConfig.getString(ConfigConstant.PRINCIPAL_FILE));

        kerberosConfig.setPrincipal(dataJsonKerberosConfig.getString(ConfigConstant.PRINCIPAL));
        Long kerberosFileTimestamp = dataJsonKerberosConfig.getLong(ConfigConstant.KERBEROS_FILE_TIMESTAMP);
        Optional.ofNullable(kerberosFileTimestamp)
                .ifPresent(t -> kerberosConfig.setGmtModified(new Timestamp(kerberosFileTimestamp)));
        kerberosConfig.setKrbName(dataJsonKerberosConfig.getString(ConfigConstant.JAVA_SECURITY_KRB5_CONF));
        return kerberosConfig;
    }

    @Override
    public String name() {
        return DS;
    }
}