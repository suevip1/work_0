package com.dtstack.engine.master.security;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.KerberosConfig;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-11-06 20:19
 */
public class DtKerberosUtil {

    public static JSONObject parse2Json(KerberosConfig kerberosConfig) {
        JSONObject pluginJson = new JSONObject();
        if (kerberosConfig == null) {
            return pluginJson;
        }
        pluginJson.fluentPut("openKerberos", null != kerberosConfig.getOpenKerberos() && kerberosConfig.getOpenKerberos() > 0)
                .fluentPut("remoteDir", kerberosConfig.getRemotePath())
                .fluentPut("principalFile", kerberosConfig.getName())
                .fluentPut("principal", kerberosConfig.getPrincipal())
                .fluentPut("krbName", kerberosConfig.getKrbName())
                .fluentPut("kerberosFileTimestamp", kerberosConfig.getGmtModified())
                .fluentPut("mergeKrbContent", kerberosConfig.getMergeKrbContent());
        return pluginJson;
    }

    /**
     * 数据同步脚本中的 kerberos 信息替换
     * @param originKerberosConfig
     * @param kerberosConfig
     */
    public static void replaceSyncJobKerberos(JSONObject originKerberosConfig, KerberosConfig kerberosConfig) {
        if (originKerberosConfig == null || kerberosConfig == null) {
            return;
        }
        originKerberosConfig.put("openKerberos", kerberosConfig.getOpenKerberos());
        originKerberosConfig.put("remoteDir", kerberosConfig.getRemotePath());
        originKerberosConfig.put("remotePath", kerberosConfig.getRemotePath());
        originKerberosConfig.put("principalFile", kerberosConfig.getName());
        originKerberosConfig.put("principal", kerberosConfig.getPrincipal());
        originKerberosConfig.put("krbName", kerberosConfig.getKrbName());
        originKerberosConfig.put("kerberosFileTimestamp", kerberosConfig.getGmtModified());
        originKerberosConfig.put("gmtModified", kerberosConfig.getGmtModified());
        originKerberosConfig.put("mergeKrbContent", kerberosConfig.getMergeKrbContent());
        originKerberosConfig.put("keytabPath", kerberosConfig.getPrincipal());

        // 移除有歧义的「name」
        originKerberosConfig.remove("name");
        // 移除有歧义的「principals」
        originKerberosConfig.remove("principals");
    }
}
