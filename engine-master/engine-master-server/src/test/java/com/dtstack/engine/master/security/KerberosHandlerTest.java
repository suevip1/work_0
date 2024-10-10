package com.dtstack.engine.master.security;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.master.security.kerberos.UserKerberosAuthentication;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.KerberosConfigVo;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.KerberosDataVo;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.Krb5MergeVo;
import com.dtstack.sdk.core.common.ApiResponse;
import org.junit.Test;

public class KerberosHandlerTest {

    KerberosHandler kerberosHandler = new KerberosHandler();

    @Test
    public void testAuthentication() throws Exception {
        kerberosHandler.afterPropertiesSet();
        try {
            // kerberosHandler.authentication(1L, 0, "componentVersion", 0, 1L, 0, 1L, 1L, null);
        } catch (Exception e) {
        }
    }

    @Test
    public void testSetKerberos() throws Exception {
        UserKerberosAuthentication userKerberosAuthentication = new UserKerberosAuthentication(kerberosParam -> {
            KerberosDataVo kerberosDataVo = new KerberosDataVo();
            KerberosConfigVo kerberosConfigVo = new KerberosConfigVo();
            kerberosConfigVo.setPath("/home/admin/");
            kerberosDataVo.setKerberosConfigVo(kerberosConfigVo);
            kerberosDataVo.setKrb5MergeVo(new Krb5MergeVo());
            ApiResponse<KerberosDataVo> response = new ApiResponse<>();
            response.setData(kerberosDataVo);
            return response;
        });
        JSONObject sftp = new JSONObject();
        sftp.put(GlobalConst.PATH,"/data/sftp");
        KerberosConfig kerberosConfig = userKerberosAuthentication.getKerberosAuthenticationConfig(1L, 0, "componentVersion", 0, 1L, 0, 1L, 1L, sftp);
        Assert.notNull(kerberosConfig);
    }

    @Test
    public void testSetKerberosInfo() throws Exception {
        kerberosHandler.setKerberos(new JSONObject(),new KerberosConfig());
    }


}