package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.enums.EComponentType;

/**
 * pluginInfo 适配器
 *
 * @author ：wangchuan
 * date：Created in 15:32 2022/10/10
 * company: www.dtstack.com
 */
public interface IPluginInfoAdapter {

    /**
     * 获取 PluginInfoContext
     *
     * @return PluginInfoContext
     */
    PluginInfoContext getPluginInfoContext();

    /**
     * 设置 PluginInfoContext
     *
     * @param pluginInfoContext PluginInfoContext
     */
    void setPluginInfoContext(PluginInfoContext pluginInfoContext);

    /**
     * 根据任务构建 pluginInfo
     *
     * @param isConsole 是否只填充控制台配置
     * @return pluginInfo
     */
    JSONObject buildPluginInfo(boolean isConsole);

    /**
     * 获取组件枚举类型
     *
     * @return EComponentType
     */
    EComponentType getEComponentType();
}
