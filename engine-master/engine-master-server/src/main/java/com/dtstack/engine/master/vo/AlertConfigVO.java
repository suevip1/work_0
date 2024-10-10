package com.dtstack.engine.master.vo;

import java.util.List;

public class AlertConfigVO {

    private Boolean enabled;
    private List<NotifyMethodVO> methods;
    private List<Long> receivers;
    private String webhook;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<NotifyMethodVO> getMethods() {
        return methods;
    }

    public void setMethods(List<NotifyMethodVO> methods) {
        this.methods = methods;
    }

    public List<Long> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<Long> receivers) {
        this.receivers = receivers;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }
}
