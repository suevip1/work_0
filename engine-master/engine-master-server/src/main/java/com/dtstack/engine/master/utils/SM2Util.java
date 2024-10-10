package com.dtstack.engine.master.utils;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: yuzhengxin
 * @Date: 2022/3/4 15:51 周五
 * @Email: shuxing@dtstack.com
 * @Description: sm2加解密工具
 */
public class SM2Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientOperator.class);


    /**
     * 加密
     *
     * @param s
     * @return
     */
    public static String encrypt(String s, String privateKey, String publicKey) {
        try {
            if (!StringUtils.isBlank(privateKey) && StringUtils.isNotEmpty(s)) {
                SM2 sm2 = new SM2(privateKey, publicKey);
                return sm2.encryptBcd(s, KeyType.PublicKey);
            }
            return s;
        } catch (Exception e) {
            LOGGER.error("sm2加密失败：" + e);
            throw new RuntimeException("sm2加密失败" + e);
        }
    }

    /**
     * 解密
     *
     * @param s
     * @return
     */
    public static String decryptIgnoreException(String s, String privateKey, String publicKey) {
        try {
            if (!StringUtils.isBlank(privateKey)  && StringUtils.isNotEmpty(s)) {
                SM2 sm2 = new SM2(privateKey, publicKey);
                return sm2.decryptStr(s, KeyType.PrivateKey);
            }
            return s;
        } catch (Exception ignoreException) {
        }
        return s;
    }

    /**
     * 解密
     *
     * @param s
     * @return
     */
    public static String decrypt(String s, String privateKey, String publicKey) {
        try {
            if (!StringUtils.isBlank(privateKey)  && StringUtils.isNotEmpty(s)) {
                SM2 sm2 = new SM2(privateKey, publicKey);
                return sm2.decryptStr(s, KeyType.PrivateKey);
            }
            return s;
        } catch (Exception e) {
            LOGGER.error("sm2解密失败：" + e);
            throw new RuntimeException("sm2解密失败" + e);
        }
    }

}
