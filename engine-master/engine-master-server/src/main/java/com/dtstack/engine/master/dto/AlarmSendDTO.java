package com.dtstack.engine.master.dto;

import com.dtstack.engine.api.domain.AlertChannel;
import com.dtstack.engine.api.dto.UserMessageDTO;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/1/13 4:15 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Deprecated
public class AlarmSendDTO {
    private String title;

    private Long contentId;

    /**
     * 如果应用自己传内容，不会去查询
     */
    private String content;

    /**
     * 发送钉钉时必传
     */
    private String webhook;

    /**
     * 通道标识
     */
    private List<String> alertGateSources;

    /**
     * 用户信息
     */
    private List<UserMessageDTO> receivers;

    /**
     * 邮件附件
     */
    private List<File> emailFiles;

    private String jobId;

    private Long tenantId;
    private Long projectId;
    private Long userId;
    private Integer appType;
    private Integer status;
    private Long readId;
    /**
     * 环境参数（jar告警时会传入）
     * @return
     */
    private Map<String,Object> env;

    private List<AlertChannel> alertChannels;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public List<String> getAlertGateSources() {
        return alertGateSources;
    }

    public void setAlertGateSources(List<String> alertGateSources) {
        this.alertGateSources = alertGateSources;
    }

    public List<UserMessageDTO> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<UserMessageDTO> receivers) {
        this.receivers = receivers;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Long getReadId() {
        return readId;
    }

    public void setReadId(Long readId) {
        this.readId = readId;
    }

    public Map<String, Object> getEnv() {
        return env;
    }

    public void setEnv(Map<String, Object> env) {
        this.env = env;
    }

    public List<File> getEmailFiles() {
        return emailFiles;
    }

    public void setEmailFiles(List<File> emailFiles) {
        this.emailFiles = emailFiles;
    }

    public List<AlertChannel> getAlertChannels() {
        return alertChannels;
    }

    public void setAlertChannels(List<AlertChannel> alertChannels) {
        this.alertChannels = alertChannels;
    }
}
