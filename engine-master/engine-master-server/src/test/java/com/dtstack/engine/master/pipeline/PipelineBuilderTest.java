package com.dtstack.engine.master.pipeline;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.master.pipeline.params.UploadParamPipeline;
import com.dtstack.engine.master.worker.RdosWrapper;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PipelineBuilderTest {

    @Test
    public void testBuildPipeline() throws Exception {
        IPipeline result = PipelineBuilder.buildPipeline("{\"params\":[\"uploadPath\",\"file\"],\"operator\":[{\"modelParam\":[\"jobparam\",\"url\"]},{\"launchCmd\":[\"replace\",\"base64\"]},{\"exeArgs\":[\"replace\"]}]}");
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetPipelineInitMap() throws Exception {
        String config = "{\"params\":[\"uploadPath\",\"file\"],\"operator\":[{\"modelParam\":[\"jobparam\",\"url\"]},{\"launchCmd\":[\"replace\",\"base64\"]},{\"exeArgs\":[\"replace\"]}]}";
        Map<String, Object> result = PipelineBuilder.getPipelineInitMap(config, new ScheduleJob(), new ScheduleTaskShade(), Arrays.asList(new ScheduleTaskParamShade()), (a) -> {

        });
        Assert.assertNotNull(config);
    }

    @Test
    public void testBuildDefaultSqlPipeline() throws Exception {
        IPipeline result = PipelineBuilder.buildDefaultSqlPipeline();
        Assert.assertNotNull(result);
    }


    @Test
    public void testModelParam() {
        String pipelineConfig = "{\n" +
                "    \"params\":[\n" +
                "        \"uploadPath\",\n" +
                "        \"file\",\n" +
                "        \"jobId\"\n" +
                "    ],\n" +
                "    \"operator\":[\n" +
                "        {\n" +
                "            \"modelParam\":[\n" +
                "                \"jobparam\",\n" +
                "                \"url\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"launch-cmd\":[\n" +
                "                \"replace\",\n" +
                "                \"base64\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"exeArgs\":[\n" +
                "                \"replace\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        IPipeline pipeline = PipelineBuilder.buildPipeline(pipelineConfig);
        Assert.assertNotNull(pipeline);
        ScheduleTaskShade sqlTaskShade = new ScheduleTaskShade();
        ScheduleJob job = new ScheduleJob();
        job.setCycTime("20220228020000");
        String extraInfo = "{\"info\":\"{\\\"launch-cmd\\\":\\\"python ${file} ${modelParam} \\\",\\\"isFailRetry\\\":true,\\\"taskParamsToReplace\\\":\\\"[{\\\\\\\"id\\\\\\\":0,\\\\\\\"isDeleted\\\\\\\":0,\\\\\\\"paramCommand\\\\\\\":\\\\\\\"yyyyMMddHHmmss\\\\\\\",\\\\\\\"paramName\\\\\\\":\\\\\\\"bdp.system.cyctime\\\\\\\",\\\\\\\"taskId\\\\\\\":3631,\\\\\\\"type\\\\\\\":0}]\\\",\\\"computeType\\\":1,\\\"exeArgs\\\":\\\"--files ${uploadPath} --python-version 3.x --launch-cmd ${launch-cmd} --app-type Python3 --app-name LR\\\",\\\"engineType\\\":\\\"dtscript\\\",\\\"taskParams\\\":\\\"worker.memory=512m\\\\nworker.cores=1\\\\nexclusive=false\\\\nworker.num=1\\\\njob.priority=10\\\",\\\"maxRetryNum\\\":3,\\\"taskType\\\":3,\\\"multiEngineType\\\":1,\\\"name\\\":\\\"LR\\\",\\\"tenantId\\\":1,\\\"modelParam\\\":\\\"{\\\\\\\"remotePath\\\\\\\":\\\\\\\"/dtInsight/science/notebook_model/normal/3631/${bdp.system.cyctime}\\\\\\\",\\\\\\\"hadoop_hosts\\\\\\\":\\\\\\\"kudu2:50070,kudu1:50070\\\\\\\",\\\\\\\"localPath\\\\\\\":\\\\\\\"/home/admin/app/dt-center-dataScience/upload/3631/${bdp.system.cyctime}\\\\\\\",\\\\\\\"hadoop_username\\\\\\\":\\\\\\\"admin\\\\\\\"}\\\",\\\"taskId\\\":3631}\"}";
        Map<String, Object> actionParam = JSONObject.parseObject(extraInfo).getJSONObject("info").toJavaObject(Map.class);
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get("taskParamsToReplace"), ScheduleTaskParamShade.class);


        Map<String, Object> pipelineParam = PipelineBuilder.getPipelineInitMap(pipelineConfig, job, sqlTaskShade, taskParamsToReplace, (pipelineMap) -> {
            pipelineMap.put("uploadPath", "hdfs://ns1/dtInsight/task/python_119_555_11264_s_1621252206383.py");
        });

        try {
            pipeline.execute(actionParam, pipelineParam);
            Assert.assertNotNull(actionParam);

            UploadParamPipeline uploadParamPipeline = new UploadParamPipeline();
            pipelineParam.remove(UploadParamPipeline.pipelineKey);
            pipelineParam.put(UploadParamPipeline.scheduleJobKey, job);
            sqlTaskShade.setTaskType(EScheduleJobType.SHELL.getType());
            sqlTaskShade.setSqlText("echo 'hello'");

            pipelineParam.put(UploadParamPipeline.taskShadeKey, sqlTaskShade);
            pipelineParam.put(UploadParamPipeline.fileUploadPathKey, "dtInsight");
            pipelineParam.put(UploadParamPipeline.rdosWrapperKey, new RdosWrapper());
            pipelineParam.put(UploadParamPipeline.tenantIdKey, 1L);
            pipelineParam.put(UploadParamPipeline.pluginInfoKey, new JSONObject());
            uploadParamPipeline.execute(actionParam, pipelineParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}