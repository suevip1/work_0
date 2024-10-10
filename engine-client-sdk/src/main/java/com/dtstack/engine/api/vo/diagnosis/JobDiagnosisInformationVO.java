package com.dtstack.engine.api.vo.diagnosis;

import com.dtstack.engine.api.vo.diagnosis.metric.MetricsResult;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author yuebai
 * @date 2022/7/14
 */
public class JobDiagnosisInformationVO {

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

    private List<DiagnosisContentVO> contents;

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

    public Timestamp getDiagnosisTime() {
        return diagnosisTime;
    }

    public void setDiagnosisTime(Timestamp diagnosisTime) {
        this.diagnosisTime = diagnosisTime;
    }

    public Integer getDiagnosisType() {
        return diagnosisType;
    }

    public void setDiagnosisType(Integer diagnosisType) {
        this.diagnosisType = diagnosisType;
    }

    public String getMetricsResult() {
        return metricsResult;
    }

    public void setMetricsResult(String metricsResult) {
        this.metricsResult = metricsResult;
    }

    public List<DiagnosisContentVO> getContents() {
        return contents;
    }

    public void setContents(List<DiagnosisContentVO> contents) {
        this.contents = contents;
    }
}
