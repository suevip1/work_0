package com.dtstack.engine.master.model.cluster.part;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.common.enums.EComponentScheduleType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.model.cluster.DataSource;
import com.dtstack.engine.master.model.cluster.system.Context;
import dt.insight.plat.lang.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 计算组件(依赖的资源调度组件不带版本，如 k8s)
 * @author qiuyun
 * @version 1.0
 * @date 2022-02-27 09:57
 */
public class DependNoVersionComputePart extends DependComputePart {
    private static final Logger LOGGER = LoggerFactory.getLogger(DependNoVersionComputePart.class);

    public DependNoVersionComputePart(EComponentType componentType, String versionName, EComponentType storeType, Map<EComponentScheduleType, List<Component>> componentScheduleGroup, Context context, DataSource dataSource, EDeployType deployType) {
        super(componentType, versionName, storeType, componentScheduleGroup, context, dataSource, deployType);
    }

    @Override
    public String getPluginName() {
        validDeployType(deployType);
        if (null == storageType) {
            throw new RdosDefineException(ErrorCode.STORE_COMPONENT_NOT_CONFIG);
        }

        Component resourceComponent = tryGetResourceComponent();
        if (resourceComponent == null) {
            throw new RdosDefineException(ErrorCode.RESOURCE_COMPONENT_NOT_CONFIG);
        }
        String resourceVersion = resourceComponent.getVersionName();
        EComponentType resourceType = EComponentType.getByCode(resourceComponent.getComponentTypeCode());

        Optional<JSONObject> resourceModelConfig = context.getModelConfig(resourceType, resourceVersion);
        if (!resourceModelConfig.isPresent()) {
            throw new RdosDefineException(Strings.format(ErrorCode.RESOURCE_NOT_SUPPORT_COMPONENT_VERSION.getMsg(), resourceType, type, versionName));
        }
        //唯一的pluginName
        return getValueInConfigWithResourceStore(resourceModelConfig.get(), resourceComponent, this::getPluginNameInModelOrByConfigVersion);
    }

    /**
     * 解析出计算组件
     * {
     *   "KUBERNETES": "kubernetes",
     *   "S3": {
     *     "CSP": {
     *       "FLINK": [
     *         {
     *           "1.12": "k8s-s3-flink112"
     *         }
     *       ],
     *       "S3": "s3"
     *     }
     *   },
     *   "NFS": {
     *     "": {
     *       "FLINK": [
     *         {
     *           "1.10": "k8s-nfs-flink110"
     *         }
     *       ],
     *       "NFS": "nfs"
     *     }
     *   },
     *   "HDFS": {
     *     "hdfs2": {
     *       "FLINK": [
     *         {
     *           "1.10": "k8s-hdfs2-flink110"
     *         },
     *         {
     *           "1.12": "k8s-hdfs2-flink112"
     *         }
     *       ],
     *       "SPARK": [
     *         {
     *           "2.4": "k8s-hdfs2-spark240"
     *         }
     *       ],
     *       "DT_SCRIPT": "k8s-hdfs2-dtscript",
     *       "HDFS": "k8s-hdfs2-hadoop2"
     *     },
     *     "hdfs3": {
     *       "FLINK": [
     *         {
     *           "1.10": "k8s-hdfs3-flink110"
     *         },
     *         {
     *           "1.12": "k8s-hdfs3-flink112"
     *         }
     *       ],
     *       "SPARK": [
     *         {
     *           "2.4": "k8s-hdfs3-spark240"
     *         }
     *       ],
     *       "DT_SCRIPT": "k8s-hdfs3-dtscript",
     *       "HDFS": "k8s-hdfs3-hadoop3"
     *     }
     *   }
     * }
     * @param resourceConfig
     * @param resourceComponent
     * @param specialSupplier
     * @return
     */
    protected String getValueInConfigWithResourceStore(JSONObject resourceConfig, Component resourceComponent, Supplier<String> specialSupplier) {
        JSONObject storageConfig = resourceConfig.getJSONObject(storageType.name());
        if (storageConfig == null) {
            throw new RdosDefineException(ErrorCode.STORE_COMPONENT_CONFIG_NULL);
        }
        // 获取当前配置的存储组件
        Component storageComponent = super.tryGetStorageComponent(storageType.getTypeCode());
        if (storageComponent == null) {
            throw new RdosDefineException(ErrorCode.PRE_COMPONENT_NOT_EXISTS);
        }
        String specifyStorageVersionName;
        if (storageType == EComponentType.HDFS) {
            // 针对 HDFS，需要借助 versionValue 归类出 specifyStorageVersionName
            String storageVersion = storageComponent.getHadoopVersion();
            if (StringUtils.isEmpty(storageVersion) || storageVersion.length() < 1) {
                throw new RdosDefineException(ErrorCode.CONFIG_COMPONENT_ERROR);
            }
            LOGGER.info("DependNoVersionComputePart, type:HDFS, versionValue:{}", storageVersion);
            specifyStorageVersionName = super.hdfsPrefix + storageVersion.substring(0, 1);
        } else {
            // 其他存储组件，直接使用 version
            specifyStorageVersionName = storageComponent.getVersionName();
        }
        JSONObject storageVersionConfigObj = storageConfig.getJSONObject(specifyStorageVersionName);
        if (storageVersionConfigObj != null) {
            // 获取计算组件，利用 containsKey 方法，而非 getValue 方法，如果获取的值为空，则为不适用该组件
            if (storageVersionConfigObj.containsKey(type.name())) {
                if (storageVersionConfigObj.get(type.name()) instanceof List) {
                    return getValueWithKey(storageVersionConfigObj.getJSONArray(type.name()))
                            .orElseThrow(() -> new RdosDefineException(Strings.format(ErrorCode.RESOURCE_NOT_SUPPORT_STORAGE_COMPUTE_COMPONENT_VERSION.getMsg(),
                                    resourceComponent.getComponentName(), storageType.name(), specifyStorageVersionName, type.name(), versionName)));
                } else {
                    return storageVersionConfigObj.getString(type.name().toUpperCase());
                }
            } else {
                if (null != specialSupplier) {
                    return specialSupplier.get();
                }
            }
        }  else if (null != specialSupplier) {
            return specialSupplier.get();
        }
        return null;
    }

    /**
     * 解析计算组件的自定义参数
     * {
     *   "HDFS": {
     *     "hdfs2": {
     *       "SPARK": [
     *         {
     *           "2.1": "-1003",
     *           "2.4": "-1003"
     *         }
     *       ],
     *       "DT_SCRIPT": "-1005",
     *       "HDFS": -101
     *     },
     *     "hdfs3": {
     *       "SPARK": [
     *         {
     *           "2.1": "-1004",
     *           "2.4": "-1004"
     *         }
     *       ],
     *       "DT_SCRIPT": "-1006",
     *       "HDFS": -103
     *     }
     *   },
     *   "S3":{
     *     "CSP":{
     *       "S3":-102
     *     }
     *
     *   },
     *   "NFS":{
     *     "":{
     *       "NFS":-101
     *     }
     *   }
     * }
     * @return
     */
    @Override
    public Long getExtraVersionParameters() {
        return super.getExtraVersionParameters();
    }
}
