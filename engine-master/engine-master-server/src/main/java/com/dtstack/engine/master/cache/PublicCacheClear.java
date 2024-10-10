package com.dtstack.engine.master.cache;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.pubsvc.sdk.authcenter.AuthCenterAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.AuthAllPermissionParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2022/8/30 4:42 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class PublicCacheClear implements InitializingBean {

    private final Logger LOGGER = LoggerFactory.getLogger(PublicCacheClear.class);

    @Autowired
    private AuthCenterAPIClient authCenterAPIClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            AuthAllPermissionParam authAllPermissionParam = new AuthAllPermissionParam();
            authAllPermissionParam.setAppType(AppType.CONSOLE.getType());
            authCenterAPIClient.clearCacheByAppType(authAllPermissionParam);
        } catch (Exception e) {
            LOGGER.error("clearCacheByAppType error:{}", e.getMessage(), e);
        }
    }
}
