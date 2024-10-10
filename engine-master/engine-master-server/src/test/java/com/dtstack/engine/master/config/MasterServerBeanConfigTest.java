package com.dtstack.engine.master.config;

import cn.hutool.core.lang.Assert;
import com.dtstack.engine.common.sftp.SftpFileManage;
import org.junit.Test;

import static org.junit.Assert.*;

public class MasterServerBeanConfigTest {

    @Test
    public void sftpFileManage() {
        SftpFileManage sftpFileManage = new MasterServerBeanConfig().sftpFileManage();
        Assert.notNull(sftpFileManage);
    }
}