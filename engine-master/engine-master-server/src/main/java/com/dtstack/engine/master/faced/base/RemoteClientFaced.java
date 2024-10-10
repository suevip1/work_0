package com.dtstack.engine.master.faced.base;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.sdk.core.common.ApiResponse;

/**
 * @author leon
 * @date 2022-10-09 17:45
 **/
public interface RemoteClientFaced {

    Integer CONSOLE_APP_TYPE = AppType.CONSOLE.getType();

    default <T> T getData(ApiResponse<T> response) {
        T data = response.getData();
        if(response.getCode() != null && response.getCode() != ErrorCode.SUCCESS.getCode()) {
            throw new RdosDefineException(response.getMessage());
        }
        return data;
    }

}
