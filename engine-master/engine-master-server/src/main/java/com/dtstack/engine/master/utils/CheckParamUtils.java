package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.dtcenter.common.enums.AppType;

/**
 * @author yuebai
 * @date 2021-09-08
 */
public class CheckParamUtils {

    /**
     * check appType
     *
     * @param appType
     */
    public static void checkAppType(Integer appType) {
        if (null == appType) {
            throw new RdosDefineException(ErrorCode.INVALID_APP_TYPE);
        }
        AppType app = AppType.getValue(appType);
        if (null == app) {
            throw new RdosDefineException(ErrorCode.INVALID_APP_TYPE);
        }
    }

    /**
     * check dtUicTenantId
     *
     * @param dtUicTenantId
     */
    public static void checkDtUicTenantId(Long dtUicTenantId) {
        if (null == dtUicTenantId) {
            throw new RdosDefineException(ErrorCode.INVALID_UIC_TENANT_ID);
        }
        if (dtUicTenantId <= 0L) {
            throw new RdosDefineException(ErrorCode.INVALID_UIC_TENANT_ID);
        }
    }


    /**
     * check page
     *
     * @param pageNo
     * @param pageSize
     */
    public static void checkPageSize(Integer pageNo, Integer pageSize) {
        if (null == pageNo || null == pageSize ||  pageNo <= 0 || pageSize <= 0) {
            throw new RdosDefineException(ErrorCode.INVALID_PAGE_PARAM);
        }
    }
}
