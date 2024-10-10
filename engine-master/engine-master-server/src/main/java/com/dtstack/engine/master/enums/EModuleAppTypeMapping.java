package com.dtstack.engine.master.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 模块(public service)和产品(engine)的映射关系，为了兼容历史 {@link com.dtstack.dtcenter.common.enums.AppType} 才新增该枚举类
 * @author qiuyun
 * @version 1.0
 * @since 2023-04-17 21:43
 */
public enum EModuleAppTypeMapping {
    RDOS("rdos", 1, "离线开发"),
    STREAM("stream", 7, "实时开发"),
    DATAASSETS("dataAssets", 9, "数据资产"),
    API("dataApi", 3, "数据服务"),
    TAG("tagEngine", 4, "客户数据洞察"),
    EASYINDEX("easyIndex", 10, "指标管理分析"),
    DATALAKE("dataLake", 15, "数据湖"),
    CONSOLE("console", 6, "控制台"),
    BIZCENTER("publicService", 14, "公共管理");

    private static final Map<String, EModuleAppTypeMapping> moduleMap = new HashMap(16);

    private String moduleCode;
    private Integer appCode;
    private String appName;

    EModuleAppTypeMapping(String moduleCode, Integer appCode, String appName) {
        this.moduleCode = moduleCode;
        this.appCode = appCode;
        this.appName = appName;
    }

    public Integer getAppCode() {
        return appCode;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public String getAppName() {
        return appName;
    }

    public static EModuleAppTypeMapping getEnumByModuleCode(String moduleCode) {
        return moduleMap.get(moduleCode);
    }

    static {
        for (EModuleAppTypeMapping e : values()) {
            moduleMap.put(e.getModuleCode(), e);
        }
    }
}
