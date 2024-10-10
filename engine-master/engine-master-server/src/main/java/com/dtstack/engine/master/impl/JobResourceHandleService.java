package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ResourceFile;
import com.dtstack.engine.po.ScheduleJobExpand;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author leon
 * @date 2022-07-28 13:54
 **/
@Service
public class JobResourceHandleService {


    @Autowired
    public ScheduleJobExpandDao scheduleJobExpandDao;

    public void handleResourcesInfo(ParamActionExt paramActionExt, String jobId) {
        recordResourcesInfo(paramActionExt,jobId);
        handleRefTask(paramActionExt);
    }

    private void handleRefTask(ParamActionExt paramActionExt) {
        // 把依赖的资源信息拼到 taskParams 的 dtscript.ship-files 后
    }

    public void recordResourcesInfo(ParamActionExt paramActionExt, String jobId) {
        Integer taskType = paramActionExt.getTaskType();
        // 只处理 python 和 shell 任务
        if (!com.dtstack.dtcenter.common.enums.EJobType.PYTHON.getType().equals(taskType)
                && !com.dtstack.dtcenter.common.enums.EJobType.SHELL.getType().equals(taskType)) {
            return;
        }

        List<ResourceFile> resourceFiles =  getResourceFiles(paramActionExt);

        List<ScheduleJobExpand> jobExpands = scheduleJobExpandDao.getExpandByJobIds(Lists.newArrayList(jobId));
        if (CollectionUtils.isNotEmpty(jobExpands)) {
            // todo 更新数据库中 jobExpand 的 resourcesFiles
            updateResourceInfo(jobExpands.get(0),resourceFiles);
        }

    }

    private List<ResourceFile> getResourceFiles(ParamActionExt paramActionExt) {
        return null;
    }

    private void handleResourcesInfo(ParamActionExt paramActionExt, String jobId, ScheduleJobExpand scheduleJobExpand) {

    }

    private void updateResourceInfo(ScheduleJobExpand updateJobExpand,List<ResourceFile> resourceFiles) {
        String resourceFilesJsonStr = JSONObject.toJSONString(resourceFiles);
//        updateJobExpand.setResourceFiles();
//        scheduleJobExpandDao.updateResourceFiles(updateJobExpand.getJobId(),resourceFilesJsonStr)
    }




}
