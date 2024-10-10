package com.dtstack.engine.master.impl;


import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.enums.PlatformEventType;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.engine.master.vo.PlatformEventVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author yuebai
 * @date 2020-03-11
 */
@Service
public class PlatformService {

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private DtUicUserConnect dtUicUserConnect;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ResourceGroupService resourceGroupService;

    @Autowired
    private SessionUtil sessionUtil;

    private void registerEvent(PlatformEventType eventType, boolean active) {
        String datasourceNode = environmentContext.getPublicServiceNode();
        dtUicUserConnect.registerEvent(dtUicUserConnect.getDatasourceRandomNode(datasourceNode), eventType, "/node/platform/callBack", active);
    }

    private static final Logger logger = LoggerFactory.getLogger(PlatformService.class);

    /**
     * 由于路由填充参数时失去了动态可能性，参数只能定义成map,或者注册多个callback
     */
    public void callback(PlatformEventVO eventVO) {
        try {
            logger.info("call back parameter {}", eventVO);
            if (null == eventVO) {
                return;
            }
            PlatformEventType eventType = PlatformEventType.getByCode(eventVO.getEventCode());
            if (null == eventType) {
                throw new RdosDefineException("Unsupported event type");
            }
            Long tenantId = eventVO.getDtUicTenantId();
            if (tenantId == null) {
                tenantId = eventVO.getTenantId();
            }
            Long userId = eventVO.getUserId();
            if (userId == null) {
                userId = eventVO.getId();
            }
            switch (eventType) {
                case DELETE_TENANT:
                    if (null == tenantId) {
                        logger.info("callback {} tenantId is null ", eventVO);
                        return;
                    }
                    tenantService.deleteTenantId(tenantId);
                    break;
                case DELETE_USER:
                    if (null == userId) {
                        logger.info("callback {} userId is null ", eventVO);
                        return;
                    }
                    accountService.deleteUser(userId);
                    break;
                case DELETE_PROJECT:
                    Long projectId = eventVO.getProjectId();
                    Integer appType = eventVO.getAppType();
                    if (projectId == null || appType == null) {
                        logger.info("callback {} projectId or appType is null ", eventVO);
                        return;
                    }
                    resourceGroupService.removeGrantByProject(projectId, appType);
                    break;
                case LOG_OUT:
                    logger.info("LOG_OUT token {}", eventVO.getToken());
                    sessionUtil.remove(eventVO.getToken());
                default:
                    break;
            }
        } catch (Exception e) {
            logger.info("call back parameter error", e);
        }
    }

    @PostConstruct
    public void init() {
        registerEvent(PlatformEventType.LOG_OUT, true);
        registerEvent(PlatformEventType.DELETE_TENANT, true);
        registerEvent(PlatformEventType.DELETE_USER, true);
        registerEvent(PlatformEventType.DELETE_PROJECT, true);
    }
}
