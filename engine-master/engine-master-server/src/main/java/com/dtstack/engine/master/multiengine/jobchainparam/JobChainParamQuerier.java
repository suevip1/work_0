package com.dtstack.engine.master.multiengine.jobchainparam;

import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IHdfsFile;
import com.dtstack.dtcenter.loader.dto.HDFSContentSummary;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-03-23 17:39
 */
@Component
public class JobChainParamQuerier {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobChainParamQuerier.class);

    public boolean isAnyOneTooLarge(Long thresholdInBytes, Integer dataSourceCode, Long dtuicTenantId, String pluginInfo, List<String> hdfsDirPaths) throws Exception {
        IHdfsFile hdfs = ClientCache.getHdfs(dataSourceCode);
        ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo, dtuicTenantId);
        List<HDFSContentSummary> contentSummary = hdfs.getContentSummary(sourceDTO, hdfsDirPaths);
        if (CollectionUtils.isEmpty(contentSummary)) {
            throw new RdosDefineException("acquire contentSummary is empty");
        }
        for (HDFSContentSummary summary : contentSummary) {
            if (summary.getSpaceConsumed().compareTo(thresholdInBytes) > 0) {
                LOGGER.info("files:{} contains size:{} larger than thresholdInBytes:{}", hdfsDirPaths, summary.getSpaceConsumed(), thresholdInBytes);
                return true;
            }
        }
        return false;
    }

    public boolean existHdfsPath(Long dtuicTenantId, Integer dataSourceCode, String pluginInfo, String hdfsPath) throws Exception {
        IHdfsFile hdfs = ClientCache.getHdfs(dataSourceCode);
        ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo, dtuicTenantId);
        HDFSContentSummary summary = hdfs.getContentSummary(sourceDTO, hdfsPath);
        if (summary == null || summary.getIsExists() == null) {
            return false;
        }
        return summary.getIsExists();
    }

    public void deleteHdfsFiles(Long dtuicTenantId, Integer dataSourceCode, String pluginInfo, Set<String> deleteHdfsFiles) {
        for (String deleteHdfsFile : deleteHdfsFiles) {
            deleteHdfsFile(dtuicTenantId, dataSourceCode, pluginInfo, deleteHdfsFile);
        }
    }

    public boolean deleteHdfsFile(Long dtuicTenantId, Integer dataSourceCode, String pluginInfo, String deleteHdfsFile) {
        if (StringUtils.isEmpty(deleteHdfsFile)) {
            return true;
        }
        Interner<String> interner = Interners.newWeakInterner();
        synchronized (interner.intern(deleteHdfsFile)) {
            try {
                boolean existPath = existHdfsPath(dtuicTenantId, dataSourceCode, pluginInfo, deleteHdfsFile);
                if (!existPath) {
                    LOGGER.info("path:{} not exists, deleteHdfsFile over", deleteHdfsFile);
                    return false;
                }
                IHdfsFile hdfs = ClientCache.getHdfs(dataSourceCode);
                ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo, dtuicTenantId);
                boolean result = hdfs.delete(sourceDTO, deleteHdfsFile, true);
                LOGGER.info("deleteHdfsFile ok, deleteHdfsFile:{}", deleteHdfsFile);
                return result;
            } catch (Exception e) {
                LOGGER.warn("deleteHdfsFile error, deleteHdfsFile:{}", deleteHdfsFile, e);
                return false;
            }
        }
    }



}