package com.dtstack.engine.master.worker;

import com.dtstack.dtcenter.loader.dto.JobExecInfo;
import com.dtstack.dtcenter.loader.dto.yarn.YarnApplicationInfoDTO;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;

import java.util.List;
import java.util.function.Consumer;

/**
 * 任务相关操作, 区分 engine-plugins 和 DatasourceX
 *
 * @author : dazhi
 * date: 2022/3/10 10:00 AM
 * email:dazhi@dtstack.com
 * description:
 */
public interface TaskOperator {

    JudgeResult judgeSlots(JobClient jobClient) throws Exception;

    JobResult submitJob(JobClient jobClient) throws Exception;

    String getEngineLog(JobIdentifier jobIdentifier);

    JobResult stopJob(JobClient jobClient) throws Exception;

    RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier, Consumer<String> originStatusConsumer);

    ComponentTestResult testConnect(String engineType, String pluginInfo, Long clusterId, Long tenantId, String versionName);

    List<YarnApplicationInfoDTO> listApplicationByTag(JobClient jobClient, String tag);

    JobExecInfo getJobExecInfo(JobIdentifier jobIdentifier, boolean removeResult, boolean removeOutput);

    List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier);
}
