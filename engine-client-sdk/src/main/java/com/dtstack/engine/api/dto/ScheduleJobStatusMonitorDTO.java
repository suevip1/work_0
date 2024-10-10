package com.dtstack.engine.api.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Job 状态监听 DTO
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-02-14 20:02
 */
public class ScheduleJobStatusMonitorDTO implements Serializable {
    private static final long serialVersionUID = 872525931882098877L;

    private Integer id;
    /**
     * 应用：RDOS、DQ、API、TAG、MAP、STREAM、DATASCIENCE
     */
    private String appTypeKey;

    /**
     * 回调地址，域名形式
     */
    private String callbackUrl;

    /**
     * Job 状态监听策略
     */
    private ScheduleJobStatusMonitorPolicyDTO jobStatusMonitorPolicy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppTypeKey() {
        return appTypeKey;
    }

    public void setAppTypeKey(String appTypeKey) {
        this.appTypeKey = appTypeKey;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public ScheduleJobStatusMonitorPolicyDTO getJobStatusMonitorPolicy() {
        return jobStatusMonitorPolicy;
    }

    public void setJobStatusMonitorPolicy(ScheduleJobStatusMonitorPolicyDTO jobStatusMonitorPolicy) {
        this.jobStatusMonitorPolicy = jobStatusMonitorPolicy;
    }

    @Override
    public String toString() {
        return "ScheduleJobStatusMonitorDTO{" +
                "id=" + id +
                ", appTypeKey='" + appTypeKey + '\'' +
                ", callbackUrl='" + callbackUrl + '\'' +
                ", jobStatusMonitorPolicy=" + jobStatusMonitorPolicy +
                '}';
    }

    /**
     * 监听策略
     */
    public static class ScheduleJobStatusMonitorPolicyDTO implements Serializable{
        private static final long serialVersionUID = 8001517863597044566L;

        /**
         * 监听的 Job 状态 {@link com.dtstack.engine.common.enums.RdosTaskStatus}
         */
        private List<Integer> listenerStatus;

        /**
         * 监听的应用：RDOS、DQ、API、TAG、MAP、STREAM、DATASCIENCE
         */
        private List<String> listenerAppType;

        /**
         * 监听的 Job 类型 {@link com.dtstack.schedule.common.enums.EScheduleJobType}
         */
        private List<String> listenerJobType;

        public List<Integer> getListenerStatus() {
            return listenerStatus;
        }

        public void setListenerStatus(List<Integer> listenerStatus) {
            this.listenerStatus = listenerStatus;
        }

        public List<String> getListenerAppType() {
            return listenerAppType;
        }

        public void setListenerAppType(List<String> listenerAppType) {
            this.listenerAppType = listenerAppType;
        }

        public List<String> getListenerJobType() {
            return listenerJobType;
        }

        public void setListenerJobType(List<String> listenerJobType) {
            this.listenerJobType = listenerJobType;
        }

        @Override
        public String toString() {
            return "ScheduleJobStatusMonitorPolicyDTO{" +
                    "listenerStatus=" + listenerStatus +
                    ", listenerAppType=" + listenerAppType +
                    ", listenerJobType=" + listenerJobType +
                    '}';
        }
    }
}
