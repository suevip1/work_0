package com.dtstack.engine.master.security.kerberos;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.enums.OpenKerberosEnum;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.exception.RdosDefineException;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.Optional;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-11-06 20:47
 */
public class SyncPartitionKerberosAuthentication implements IKerberosAuthentication {

    public static final String SYNC_PARTITION = "SyncPartition";

    @Override
    public KerberosConfig getKerberosAuthenticationConfig(KerberosReq kerberosReq) {
        JSONObject hadoopConfig = kerberosReq.getHadoopConfig();
        boolean isOpenKerberos = ConfigConstant.KERBEROS.equalsIgnoreCase(hadoopConfig.getString("hadoop.security.authentication"))
                || ConfigConstant.KERBEROS.equalsIgnoreCase(hadoopConfig.getString("hive.server2.authentication"))
                || ConfigConstant.KERBEROS.equalsIgnoreCase(hadoopConfig.getString("hive.server.authentication"));
        if (!isOpenKerberos) {
            return null;
        }
        KerberosConfig kerberosConfig = new KerberosConfig();

        // 开启了 kerberos 用数据同步中 job 中配置项
        kerberosConfig.setOpenKerberos(OpenKerberosEnum.OPEN.getCode());
        String remoteDir = hadoopConfig.getString(ConfigConstant.REMOTE_DIR);
        if (StringUtils.isBlank(remoteDir)) {
            throw new RdosDefineException("data synchronization task hadoopConfig remoteDir field cannot be empty");
        }
        kerberosConfig.setRemotePath(remoteDir);

        String principalFile = hadoopConfig.getString(ConfigConstant.PRINCIPAL_FILE);
        if (StringUtils.isBlank(principalFile)) {
            throw new RdosDefineException("data synchronization hadoopConfig principalFile field cannot be empty");
        }
        kerberosConfig.setName(principalFile);
        kerberosConfig.setPrincipal(hadoopConfig.getString(ConfigConstant.PRINCIPAL));
        Long kerberosFileTimestamp = hadoopConfig.getLong(ConfigConstant.KERBEROS_FILE_TIMESTAMP);
        Optional.ofNullable(kerberosFileTimestamp)
                .ifPresent(t -> kerberosConfig.setGmtModified(new Timestamp(kerberosFileTimestamp)));

        // krb5.conf 的文件名
        String krb5Conf = hadoopConfig.getString(ConfigConstant.JAVA_SECURITY_KRB5_CONF);
        if (StringUtils.isBlank(krb5Conf)) {
            //平台不传 暂时设置默认值
            krb5Conf = ConfigConstant.KRB5_CONF;
        }
        kerberosConfig.setKrbName(krb5Conf);
        return kerberosConfig;
    }

    @Override
    public String name() {
        return SYNC_PARTITION;
    }
}