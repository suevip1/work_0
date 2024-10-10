package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.AddressUtil;
import com.dtstack.engine.common.util.UrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;

/**
 * 描述一个URL地址是否有效
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-02-17 00:58
 */
public class UrlAvailabilityUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlAvailabilityUtil.class);

    private static final int maxRetryNum = 3;

    /**
     * 功能：检测当前URL是否可连接或是否有效,
     * 描述：最多连接网络 3 次, 如果 3 次都不成功，视为该地址不可用
     *
     * @param urlStr
     * @return
     */
    public static boolean canConnect(String urlStr) {
        if (urlStr == null || urlStr.length() <= 0) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        Matcher matcher = UrlUtil.URLPattern.matcher(urlStr);
        if(!matcher.find()) {
            throw new RdosDefineException(String.format("url:%s is not regular HTTP_URL", urlStr));
        }

        String hostName = matcher.group(2);
        String port = matcher.group(3) == null ? "80" : matcher.group(3);
        Integer integerPort = Integer.parseInt(port);

        int count = 0;
        while (count < maxRetryNum) {
            // 校验连通性
            if (AddressUtil.telnet(hostName, integerPort)) {
                LOGGER.info("URL:{} 可用", urlStr);
                return true;
            } else {
                count++;
                LOGGER.info("URL:{} 不可用，连接第 {} 次", urlStr, count);
                try {
                    Thread.sleep(100 * count);
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        canConnect("https://blog.csdn.net:8090/baidu_18607183/article/details/53667480");
        canConnect("http://uic.custom.com/console/#/console/resourceManage");
        canConnect("http://11.com");
    }
}
