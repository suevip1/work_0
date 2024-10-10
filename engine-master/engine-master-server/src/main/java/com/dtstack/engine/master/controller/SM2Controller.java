package com.dtstack.engine.master.controller;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.utils.SM2Util;
import com.dtstack.engine.master.vo.SM2CheckVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther: shuxing
 * @date: 2022/3/21 13:54 周一
 * @email: shuxing@dtstack.com
 * @description:
 */
@RestController
@RequestMapping("/node/sm2")
@Api(value = "/node/sm2", tags = {"sm2加密接口"})
public class SM2Controller {

    @Autowired
    private EnvironmentContext environmentContext;

    /**
     * 获取SM2加密的公钥
     *
     * @return
     */
    @PostMapping(value = "/getSM2PublicKey")
    @ApiOperation(value = "获取SM2加密公钥")
    public String getSM2PublicKey() {
        return environmentContext.getSM2PublicKey();
    }

    /**
     * 判断sm2加密后的参数是否一致
     *
     * @return
     */
    @PostMapping(value = "/checkSm2EncryptStrEquals")
    @ApiOperation(value = "判断sm2加密后的参数是否一致")
    public Boolean checkSm2EncryptStrEquals(@RequestBody SM2CheckVO vo) {
        if (null == vo || StringUtils.isBlank(vo.getModifyStr()) || StringUtils.isBlank(vo.getOriginStr())) {
            return false;
        }
        String originStrDecrypt = SM2Util.decrypt(vo.getOriginStr(), environmentContext.getSM2PrivateKey(), environmentContext.getSM2PublicKey());
        String modifyStrDecrypt = SM2Util.decrypt(vo.getModifyStr(), environmentContext.getSM2PrivateKey(), environmentContext.getSM2PublicKey());
        return originStrDecrypt.equals(modifyStrDecrypt);
    }

}
