package com.dtstack.engine.master.distributor;

import com.dtstack.engine.common.enums.ClientTypeEnum;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.model.cluster.system.Context;
import com.dtstack.engine.master.worker.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2022/3/10 9:57 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class OperatorDistributor {

    @Autowired
    private DataSourceXOperator dataSourceXOperator;

    @Autowired
    private EnginePluginsOperator enginePluginsOperator;

    @Autowired
    private Context context;

    public TaskOperator getOperator(ClientTypeEnum clientType, String engineType) {
        if (clientType == null) {
            clientType = context.getClientType(engineType);
        }

        if (ClientTypeEnum.WORKER_STATUS.equals(clientType)) {
            return enginePluginsOperator;
        } else if (ClientTypeEnum.DATASOURCEX_STATUS.equals(clientType) || ClientTypeEnum.DATASOURCEX_NO_STATUS.equals(clientType)) {
            return dataSourceXOperator;
        }

        throw new RdosDefineException("no find operator");
    }
}
