package com.dtstack.engine.api.vo.diagnosis;

/**
 * @author yuebai
 * @date 2022/8/3
 */
public class DiagnosisContentVO {
    private String title;
    private String content;

    public DiagnosisContentVO() {
    }

    public DiagnosisContentVO(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
