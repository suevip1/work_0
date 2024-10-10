package com.dtstack.engine.master.model.cluster;

import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.common.enums.EComponentType;

import java.util.List;

public interface Part {
    EComponentType getType();

    /**
     * 获取组件显示版本 对应的 具体版本值
     *
     * @return
     */
    String getVersionValue();

    /**
     * 加载组件模版
     *
     * @return
     */
    List<ComponentConfig> loadTemplate();

    /**
     * 获取组件对应pluginName
     *
     * @return
     */
    String getPluginName();

    /**
     * 获取组件依赖的资源组件
     *
     * @return
     */
    EComponentType getResourceType();

    /**
     * 获取组件版本适配参数
     * @return
     */
    Long getExtraVersionParameters();

    /**
     * 组件是否开启数据安全
     * @return
     */
    boolean openSecurity();

    /**
     * 获取组件数据安全参数配置模板，配置方式： {version : templateId}
     * 举例：
     * hdfs 组件，{"hdfs2":-1005,"hdfs3":-1005}
     * @return templateId
     */
    Long getSecurityParamTemplate();
}
