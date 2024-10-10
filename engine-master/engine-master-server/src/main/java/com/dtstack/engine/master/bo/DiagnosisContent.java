package com.dtstack.engine.master.bo;

/**
 * @author yuebai
 * @date 2022/8/3
 */
public class DiagnosisContent {
    private String title;
    private String content;

    public DiagnosisContent() {
    }

    public DiagnosisContent(String title, String content) {
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
