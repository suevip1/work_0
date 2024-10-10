package com.dtstack.engine.common.util;

import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.AppConfigurationEntry;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

public class KerberosUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(KerberosUtils.class);


    private static final String JAVA_VENDOR_NAME = System.getProperty("java.vendor");

    public static final boolean IBM_JAVA;

    private static final Map<String, String> debugOptions = new HashMap<>();

    private static final Map<String, String> kerberosCacheOptions = new HashMap<>();

    private static final AppConfigurationEntry userKerberosAce;

    static {

        IBM_JAVA = JAVA_VENDOR_NAME.contains("IBM");

        if (LOGGER.isDebugEnabled()) {
            debugOptions.put("debug", "true");
        }

        if (IBM_JAVA) {
            kerberosCacheOptions.put("useDefaultCcache", "true");
        } else {
            kerberosCacheOptions.put("doNotPrompt", "true");
            kerberosCacheOptions.put("useTicketCache", "true");
        }

        String ticketCache = System.getenv("KRB5CCNAME");
        if (ticketCache != null) {
            if (IBM_JAVA) {
                System.setProperty("KRB5CCNAME", ticketCache);
            } else {
                kerberosCacheOptions.put("ticketCache", ticketCache);
            }
        }

        kerberosCacheOptions.put("renewTGT", "true");
        kerberosCacheOptions.putAll(debugOptions);

        userKerberosAce = new AppConfigurationEntry(
                getKrb5LoginModuleName(),
                AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL,
                kerberosCacheOptions);

    }

    public static String getKrb5LoginModuleName() {
        return System.getProperty("java.vendor").contains("IBM")
                ? "com.ibm.security.auth.module.Krb5LoginModule"
                : "com.sun.security.auth.module.Krb5LoginModule";
    }

    public static AppConfigurationEntry keytabEntry(String keytab, String principal) {

        Map<String, String> keytabKerberosOptions = new HashMap<>();

        if (IBM_JAVA) {
            keytabKerberosOptions.put("useKeytab", prependFileUri(keytab));
            keytabKerberosOptions.put("credsType", "both");
        } else {
            keytabKerberosOptions.put("keyTab", keytab);
            keytabKerberosOptions.put("doNotPrompt", "true");
            keytabKerberosOptions.put("useKeyTab", "true");
            keytabKerberosOptions.put("storeKey", "true");
        }

        keytabKerberosOptions.put("principal", principal);
        keytabKerberosOptions.put("refreshKrb5Config", "true");
        keytabKerberosOptions.putAll(debugOptions);

        AppConfigurationEntry keytabKerberosAce = new AppConfigurationEntry(
                getKrb5LoginModuleName(),
                AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                keytabKerberosOptions);

        return keytabKerberosAce;
    }


    private static String prependFileUri(String keytabPath) {
        File f = new File(keytabPath);
        return f.toURI().toString();
    }

    /**
     * 在本地路径创建并上传 .lock 文件到 sftp
     * @param gmtModified
     * @param localKerberosDir
     * @param remoteDirKerberos
     * @param sftpFileManage
     */
    public static void touchAndUploadKerberosLockFile2Sftp(Timestamp gmtModified, String localKerberosDir, String remoteDirKerberos, SftpFileManage sftpFileManage) {
        File localKerberosConfDir = new File(localKerberosDir);
        boolean createLocalKerberosConfDir = false;
        if (!localKerberosConfDir.exists()) {
            try {
                FileUtils.forceMkdir(localKerberosConfDir);
                createLocalKerberosConfDir = true;
            } catch (IOException e) {
                throw new RdosDefineException(String.format("mkdir %s error", localKerberosConfDir), e);
            }
        }
        String localKerberosLockFileName;
        File localKerberosLockFile;
        // 标记是否新建
        boolean doTouch = false;
        String[] list = localKerberosConfDir.list();
        // 过滤出 .lock 文件 -- 处理用户上传了 .lock 文件的情况
        Optional<String> lockFileOptional = Arrays.stream(list).filter(str -> str.endsWith(ConfigConstant.LOCK_SUFFIX)).findFirst();
        if (lockFileOptional.isPresent()) {
            localKerberosLockFileName = lockFileOptional.get();
            localKerberosLockFile = new File(localKerberosDir + File.separator + localKerberosLockFileName);
        } else {
            // 创建 .lock 文件
            localKerberosLockFileName = gmtModified.getTime() + ConfigConstant.LOCK_SUFFIX;
            String lockFile = localKerberosDir + File.separator + localKerberosLockFileName;
            try {
                localKerberosLockFile = new File(lockFile);
                localKerberosLockFile.createNewFile();
            } catch (IOException e) {
                throw new RdosDefineException(String.format("touch %s file fail", lockFile), e);
            }
            doTouch = true;
        }
        removeOldLockFileIfExists(remoteDirKerberos, sftpFileManage);
        sftpFileManage.uploadFile(remoteDirKerberos, localKerberosLockFile.getPath());
        // 清理临时文件
        if (doTouch) {
            FileUtils.deleteQuietly(localKerberosLockFile);
        }
        if (createLocalKerberosConfDir) {
            FileUtils.deleteQuietly(localKerberosConfDir);
        }
    }

    /**
     * 删掉旧的 .lock 文件
     * @param remoteDirKerberos
     * @param sftpFileManage
     */
    private static void removeOldLockFileIfExists(String remoteDirKerberos, SftpFileManage sftpFileManage) {
        if (!sftpFileManage.isFileExist(remoteDirKerberos)) {
            return;
        }
        Vector files = sftpFileManage.listFile(remoteDirKerberos);
        for (Iterator<ChannelSftp.LsEntry> iterator = files.iterator(); iterator.hasNext(); ) {
            ChannelSftp.LsEntry str = iterator.next();
            String filename = str.getFilename();
            if (StringUtils.endsWithIgnoreCase(filename, ConfigConstant.LOCK_SUFFIX)) {
                String lockFilePath = remoteDirKerberos + File.separator + filename;
                sftpFileManage.deleteFile(lockFilePath);
            }
        }
    }
}
