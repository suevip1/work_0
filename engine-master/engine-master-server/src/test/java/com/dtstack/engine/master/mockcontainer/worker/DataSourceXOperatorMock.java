package com.dtstack.engine.master.mockcontainer.worker;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.dtcenter.loader.cache.client.job.JobProxy;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IJob;
import com.dtstack.dtcenter.loader.dto.JobParam;
import com.dtstack.dtcenter.loader.dto.JobResult;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.mapstruct.OperatorStruct;
import com.dtstack.engine.master.worker.RdosWrapper;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-19 20:17
 */
public class DataSourceXOperatorMock {
    @MockInvoke(targetClass = ClientCache.class)
    public static IJob getJob(Integer dataSourceType) {
        return new JobProxy(null);
    }

    @MockInvoke(targetClass = RdosWrapper.class)
    public Integer getDataSourceType(String engineType, Long clusterId, EComponentType eComponentType) {
        return DataSourceType.KylinRestful.getVal();
    }

    @MockInvoke(targetClass = IJob.class)
    JobResult submitJob(ISourceDTO var1, JobParam var2) {
        return new JobResult();
    }

    @MockInvoke(targetClass = OperatorStruct.class)
    com.dtstack.engine.common.pojo.JobResult toJobResult(com.dtstack.dtcenter.loader.dto.JobResult jobResult) {
        return new com.dtstack.engine.common.pojo.JobResult();
    }

    @MockInvoke(targetClass = IJob.class)
    JobResult cancelJob(ISourceDTO source, JobParam jobParam) {
        return new JobResult();
    }

    @MockInvoke(targetClass = IJob.class)
    String getJobLog(ISourceDTO source, JobParam jobParam) {
        return "";
    }

    @MockInvoke(targetClass = IJob.class)
    String getJobStatus(ISourceDTO source, JobParam jobParam) {
        return "FINISHED";
    }
    @MockInvoke(targetClass = IJob.class)
    Boolean judgeSlots(ISourceDTO var1, JobParam var2) {
        return true;
    }

}
