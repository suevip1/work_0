package com.dtstack.engine.api.dto;

public class ScheduleSqlTextDTO {

    private String jobId;

    private String sqlText;

    private Integer versionId;

    private String engineType;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    @Override
    public String toString() {
        return "ScheduleSqlTextDTO{" +
                "jobId='" + jobId + '\'' +
                ", sqlText='" + sqlText + '\'' +
                ", versionId=" + versionId +
                ", engineType='" + engineType + '\'' +
                '}';
    }
}
