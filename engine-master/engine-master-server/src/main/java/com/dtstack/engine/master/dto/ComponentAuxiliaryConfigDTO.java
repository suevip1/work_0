package com.dtstack.engine.master.dto;

import com.dtstack.engine.po.ConsoleComponentAuxiliary;
import com.dtstack.engine.po.ConsoleComponentAuxiliaryConfig;

import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-04-13 20:13
 */
public class ComponentAuxiliaryConfigDTO {
    private ConsoleComponentAuxiliary auxiliary;

    private List<ConsoleComponentAuxiliaryConfig> auxiliaryConfigs;

    public ConsoleComponentAuxiliary getAuxiliary() {
        return auxiliary;
    }

    public void setAuxiliary(ConsoleComponentAuxiliary auxiliary) {
        this.auxiliary = auxiliary;
    }

    public List<ConsoleComponentAuxiliaryConfig> getAuxiliaryConfigs() {
        return auxiliaryConfigs;
    }

    public void setAuxiliaryConfigs(List<ConsoleComponentAuxiliaryConfig> auxiliaryConfigs) {
        this.auxiliaryConfigs = auxiliaryConfigs;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ComponentAuxiliaryConfigDTO{");
        sb.append("auxiliary=").append(auxiliary);
        sb.append(", auxiliaryConfigs=").append(auxiliaryConfigs);
        sb.append('}');
        return sb.toString();
    }
}
