package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.domain.KerberosConfig;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.BeanUtils;

import java.sql.Timestamp;
import java.util.Map;

@ApiModel
public class KerberosConfigVO extends KerberosConfig {
    private String keytabPath;

    @Deprecated
    private Map hdfsConfig;

    private Timestamp kerberosFileTimestamp;

    /**
     * keytab 名称 平台使用
     */
    private String principalFile;

    public static KerberosConfigVO toVO(KerberosConfig config) {
        KerberosConfigVO kerberosConfigVO = new KerberosConfigVO();
        BeanUtils.copyProperties(config, kerberosConfigVO);
        kerberosConfigVO.setPrincipalFile(config.getName());
        //平台有用到
        kerberosConfigVO.setKerberosFileTimestamp(config.getGmtModified());
        return kerberosConfigVO;
    }

    public String getPrincipalFile() {
        return principalFile;
    }

    public void setPrincipalFile(String principalFile) {
        this.principalFile = principalFile;
    }

    public String getKeytabPath() {
        return keytabPath;
    }

    public void setKeytabPath(String keytabPath) {
        this.keytabPath = keytabPath;
    }

    public Map getHdfsConfig() {
        return hdfsConfig;
    }

    public void setHdfsConfig(Map hdfsConfig) {
        this.hdfsConfig = hdfsConfig;
    }

    public Timestamp getKerberosFileTimestamp() {
        return kerberosFileTimestamp;
    }

    public void setKerberosFileTimestamp(Timestamp kerberosFileTimestamp) {
        this.kerberosFileTimestamp = kerberosFileTimestamp;
    }
}

