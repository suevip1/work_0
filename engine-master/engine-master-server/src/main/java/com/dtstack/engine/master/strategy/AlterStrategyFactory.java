package com.dtstack.engine.master.strategy;

import com.dtstack.engine.master.enums.AlterKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2022/5/23 2:37 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class AlterStrategyFactory {

    @Autowired
    private BaselineAlterStrategy baselineAlterStrategy;

    @Autowired
    private ScanningAlterStrategy scanningAlterStrategy;

    @Autowired
    private StatusChangeStrategy statusChangeStrategy;

    @Autowired
    private ResourceOverLimitAlterStrategy resourceOverLimitAlterStrategy;


    public AlterStrategy getAlterStrategy(AlterKey key) {
        if (AlterKey.baseline.equals(key)) {
            return baselineAlterStrategy;
        } else if (AlterKey.scanning.equals(key)) {
            return scanningAlterStrategy;
        } else if (AlterKey.status_change.equals(key)) {
            return statusChangeStrategy;
        } else if (AlterKey.resource_over_limit.equals(key)) {
            return resourceOverLimitAlterStrategy;
        }
        return null;
    }


}
