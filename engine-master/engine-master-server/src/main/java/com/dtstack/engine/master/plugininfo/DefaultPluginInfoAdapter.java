package com.dtstack.engine.master.plugininfo;

import com.dtstack.engine.common.enums.EComponentType;
import org.springframework.stereotype.Component;

/**
 * default engine plugin adapter
 *
 * @author ：wangchuan
 * date：Created in 15:33 2022/10/10
 * company: www.dtstack.com
 */
@Component
public class DefaultPluginInfoAdapter extends AbstractPluginAdapter {

    @Override
    public EComponentType getEComponentType() {
        return null;
    }
}