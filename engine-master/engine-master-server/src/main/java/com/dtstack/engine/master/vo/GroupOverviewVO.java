package com.dtstack.engine.master.vo;

import com.dtstack.engine.common.enums.EJobCacheStage;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author leon
 * @date 2022-09-09 15:43
 **/
@JsonSerialize(using = GroupOverviewVO.GroupOverviewVOSerialize.class)
public class GroupOverviewVO {

    private String jobResource;

    private List<OverviewContent> overviewContents;

    // 未杀死的任务
    private Integer notYetKilledJobCount;

    public static class OverviewContent {
        private EJobCacheStage stage;
        private Integer jobSize;
        private String waitTime;

        public EJobCacheStage getStage() {
            return stage;
        }

        public void setStage(EJobCacheStage stage) {
            this.stage = stage;
        }

        public Integer getJobSize() {
            return jobSize;
        }

        public void setJobSize(Integer jobSize) {
            this.jobSize = jobSize;
        }

        public String getWaitTime() {
            return waitTime;
        }

        public void setWaitTime(String waitTime) {
            this.waitTime = waitTime;
        }
    }

    public static class GroupOverviewVOSerialize extends JsonSerializer<GroupOverviewVO> {

        private static final String JOB_SIZE_SUFFIX = "JobSize";

        private static final String WAIT_TIME_SUFFIX = "WaitTime";

        private static final String JOB_RESOURCE_VO_KEY = "jobResource";

        private static final String NOT_YET_KILLED_JOB_COUNT = "notYetKilledJobCount";

        @Override
        public void serialize(GroupOverviewVO groupOverviewVO,
                              JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();

            jsonGenerator.writeStringField(JOB_RESOURCE_VO_KEY, groupOverviewVO.getJobResource());
            jsonGenerator.writeNumberField(NOT_YET_KILLED_JOB_COUNT, Optional.ofNullable(groupOverviewVO.getNotYetKilledJobCount()).orElse(0));

            List<OverviewContent> overviewContents = groupOverviewVO.getOverviewContents();
            Map<EJobCacheStage,OverviewContent> helper = new HashMap<>();
            overviewContents.forEach(e -> helper.put(e.getStage(), e));
            for (EJobCacheStage stage: EJobCacheStage.values()) {
                String stagePrefix = stage.name().toLowerCase();
                OverviewContent content = helper.get(stage);
                jsonGenerator.writeNumberField(stagePrefix, stage.getStage());
                jsonGenerator.writeNumberField(stagePrefix + JOB_SIZE_SUFFIX, Optional.ofNullable(content).map(OverviewContent::getJobSize).orElse(0));
                jsonGenerator.writeStringField(stagePrefix + WAIT_TIME_SUFFIX, Optional.ofNullable(content).map(OverviewContent::getWaitTime).orElse(""));

            }
            jsonGenerator.writeEndObject();
        }
    }


    public String getJobResource() {
        return jobResource;
    }

    public void setJobResource(String jobResource) {
        this.jobResource = jobResource;
    }

    public List<OverviewContent> getOverviewContents() {
        return overviewContents;
    }

    public void setOverviewContents(List<OverviewContent> overviewContents) {
        this.overviewContents = overviewContents;
    }

    public Integer getNotYetKilledJobCount() {
        return notYetKilledJobCount;
    }

    public void setNotYetKilledJobCount(Integer notYetKilledJobCount) {
        this.notYetKilledJobCount = notYetKilledJobCount;
    }


}
