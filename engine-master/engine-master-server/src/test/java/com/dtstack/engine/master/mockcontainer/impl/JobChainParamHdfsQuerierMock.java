package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.common.pojo.hdfs.HdfsContentSummary;
import com.dtstack.engine.common.pojo.hdfs.HdfsQueryDTO;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import org.assertj.core.util.Lists;

import java.util.List;

public class JobChainParamHdfsQuerierMock {

    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public Boolean deleteHdfsFile(String engineType, String pluginInfo, String deleteHdfsFile, boolean isRecursion) throws Exception {
    return true;
    }
        @MockInvoke(targetClass = EnginePluginsOperator.class)
    public String getHdfsWithScript(String engineType, String pluginInfo, String hdfsPath) throws Exception {
        return "test";
    }
    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public List<String> getHdfsWithJob(String engineType, String pluginInfo, HdfsQueryDTO hdfsQueryDTO) throws Exception {
        return Lists.newArrayList("test");

    }

    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public List<HdfsContentSummary> getContentSummary(String engineType, String pluginInfo, List<String> hdfsDirPaths) throws Exception {
        HdfsContentSummary hdfsContentSummary = new HdfsContentSummary(1L, 1L, 3L, System.currentTimeMillis(), true, "/data/job/1/11/output_number_4ifmk5f3f5p0_20220408000000");
        return Lists.newArrayList(hdfsContentSummary);

    }
}
