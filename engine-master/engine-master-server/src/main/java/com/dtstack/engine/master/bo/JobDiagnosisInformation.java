package com.dtstack.engine.master.bo;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author yuebai
 * @date 2022/7/14
 */
public class JobDiagnosisInformation {

    /**
     * 诊断结果
     */
    private Integer diagnosisResult;

    /**
     * 诊断状态
     */
    private String diagnosisStatusInfo;

    /**
     * 诊断建议
     */
    private String diagnosisSuggest;

    /**
     * 诊断时间
     */
    private Timestamp diagnosisTime;

    /**
     * 诊断类型
     */
    private Integer diagnosisType;


    /**
     * 指标信息
     */
    private String metricsResult;

    private List<DiagnosisContent> contents;

    public List<DiagnosisContent> getContents() {
        return contents;
    }

    public void setContents(List<DiagnosisContent> contents) {
        this.contents = contents;
    }

    public String getMetricsResult() {
        return metricsResult;
    }

    public void setMetricsResult(String metricsResult) {
        this.metricsResult = metricsResult;
    }

    public Timestamp getDiagnosisTime() {
        return diagnosisTime;
    }

    public JobDiagnosisInformation(Integer diagnosisResult, String diagnosisStatusInfo, String diagnosisSuggest) {
        this.diagnosisResult = diagnosisResult;
        this.diagnosisStatusInfo = diagnosisStatusInfo;
        this.diagnosisSuggest = diagnosisSuggest;
    }

    public void setDiagnosisTime(Timestamp diagnosisTime) {
        this.diagnosisTime = diagnosisTime;
    }

    public JobDiagnosisInformation() {
    }

    public Integer getDiagnosisResult() {
        return diagnosisResult;
    }

    public void setDiagnosisResult(Integer diagnosisResult) {
        this.diagnosisResult = diagnosisResult;
    }

    public String getDiagnosisStatusInfo() {
        return diagnosisStatusInfo;
    }

    public void setDiagnosisStatusInfo(String diagnosisStatusInfo) {
        this.diagnosisStatusInfo = diagnosisStatusInfo;
    }

    public String getDiagnosisSuggest() {
        return diagnosisSuggest;
    }

    public void setDiagnosisSuggest(String diagnosisSuggest) {
        this.diagnosisSuggest = diagnosisSuggest;
    }

    public Integer getDiagnosisType() {
        return diagnosisType;
    }

    public void setDiagnosisType(Integer diagnosisType) {
        this.diagnosisType = diagnosisType;
    }


}
