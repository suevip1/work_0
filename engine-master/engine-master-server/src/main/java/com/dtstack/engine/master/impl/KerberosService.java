package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.dtstack.engine.common.util.KerberosUtils;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.KerberosDao;
import com.dtstack.engine.master.enums.CrudEnum;
import com.dtstack.dtcenter.common.enums.AppType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Timestamp;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-12-01 15:34
 */
@Service
public class KerberosService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KerberosService.class);

    @Autowired
    private KerberosDao kerberosDao;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private SftpFileManage sftpFileManageBean;

    /**
     * 刷新 kerberos 信息
     * @param crudEnum
     * @param kerberosConfig
     * @param clusterId
     * @param componentCode
     */
    public void refreshKerberos(CrudEnum crudEnum, KerberosConfig kerberosConfig,
                                Long clusterId, Integer componentCode, String componentHadoopVersion) {
        if (crudEnum == CrudEnum.ADD) {
            kerberosDao.insert(kerberosConfig);
        } else if (crudEnum == CrudEnum.UPDATE) {
            kerberosDao.update(kerberosConfig);
        } else {
            throw new RdosDefineException(String.format("refreshKerberos error:no operation, clusterId:%s, componentCode:%s", clusterId, componentCode), ErrorCode.NOT_SUPPORT);
        }

        // 以 DB 中的时间为准
        KerberosConfig refreshedKerberos = kerberosDao.findById(kerberosConfig.getId());
        Timestamp gmtModified = refreshedKerberos.getGmtModified();
        refreshRemoteKerberosLockFile(gmtModified, clusterId, componentCode, componentHadoopVersion);
    }

    /**
     * 只要变更了 console_kerberos 记录，就要同步更新远程「.lock」文件，保持时间戳一致
     * @param gmtModified
     * @param clusterId
     * @param componentCode
     */
    private void refreshRemoteKerberosLockFile(Timestamp gmtModified, Long clusterId, Integer componentCode, String componentHadoopVersion) {
        String kerberosPath = this.getLocalKerberosPath(clusterId, componentCode);
        synchronized (kerberosPath.intern()) {
            String sftpConfigStr = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, String.class,null);
            SftpConfig sftpConfig = ComponentService.getSFTPConfig(sftpConfigStr, componentCode, StringUtils.EMPTY);
            String remoteDir = sftpConfig.getPath() + File.separator + componentService.buildSftpPath(clusterId, componentCode, componentHadoopVersion);
            String remoteDirKerberos = remoteDir + File.separator + GlobalConst.KERBEROS_PATH;
            SftpFileManage sftpFileManage = sftpFileManageBean.retrieveSftpManager(sftpConfig);
            KerberosUtils.touchAndUploadKerberosLockFile2Sftp(gmtModified, kerberosPath, remoteDirKerberos, sftpFileManage);
        }
    }

    /**
     * 获取本地kerberos配置地址
     *
     * @param clusterId
     * @param componentCode
     * @return
     */
    public String getLocalKerberosPath(Long clusterId, Integer componentCode) {
        Cluster one = clusterDao.getOne(clusterId);
        if (null == one) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        return env.getLocalKerberosDir() + File.separator + AppType.CONSOLE + "_" + one.getClusterName() + File.separator + EComponentType.getByCode(componentCode).name() + File.separator + GlobalConst.KERBEROS_PATH;
    }
}
