package com.dtstack.engine.api.pojo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * @author yuebai
 * @date 2020-05-15
 */
@ApiModel
public class ComponentTestResult {
    private int componentTypeCode;

    private boolean result;

    private String errorMsg;

    /**
     * 控制台参数校验单个结果
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<ConsoleParamCheckResult> consoleParamCheckResultList;

    /**
     * 控制台参数校验结果
     */
    private Object consoleParamCheckResult;

    private String versionName;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getComponentTypeCode() {
        return componentTypeCode;
    }

    private ClusterResourceDescription clusterResourceDescription;

    public ClusterResourceDescription getClusterResourceDescription() {
        return clusterResourceDescription;
    }

    public void setClusterResourceDescription(ClusterResourceDescription clusterResourceDescription) {
        this.clusterResourceDescription = clusterResourceDescription;
    }

    public void setComponentTypeCode(int componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<ConsoleParamCheckResult> getConsoleParamCheckResultList() {
        return consoleParamCheckResultList;
    }

    public void setConsoleParamCheckResultList(List<ConsoleParamCheckResult> consoleParamCheckResultList) {
        this.consoleParamCheckResultList = consoleParamCheckResultList;
    }

    public Object getConsoleParamCheckResult() {
        return consoleParamCheckResult;
    }

    public void setConsoleParamCheckResult(Object consoleParamCheckResult) {
        this.consoleParamCheckResult = consoleParamCheckResult;
    }

    public static ComponentTestResult createFailResult(String errorMsg) {
        ComponentTestResult componentTestResult = new ComponentTestResult();
        componentTestResult.setResult(false);
        componentTestResult.setErrorMsg(errorMsg);
        return componentTestResult;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }


    public static class ClusterResourceDescription {
        private int totalNode;
        private int totalMemory;
        private int totalCores;
        private List<QueueDescription> queueDescriptions;

        public ClusterResourceDescription(int totalNode, int totalMemory, int totalCores, List<QueueDescription> descriptions) {
            this.totalNode = totalNode;
            this.totalMemory = totalMemory;
            this.totalCores = totalCores;
            this.queueDescriptions = descriptions;
        }

        public int getTotalNode() {
            return totalNode;
        }

        public void setTotalNode(int totalNode) {
            this.totalNode = totalNode;
        }

        public int getTotalMemory() {
            return totalMemory;
        }

        public void setTotalMemory(int totalMemory) {
            this.totalMemory = totalMemory;
        }

        public int getTotalCores() {
            return totalCores;
        }

        public void setTotalCores(int totalCores) {
            this.totalCores = totalCores;
        }

        public List<QueueDescription> getQueueDescriptions() {
            return queueDescriptions;
        }

        public void setQueueDescriptions(List<QueueDescription> queueDescriptions) {
            this.queueDescriptions = queueDescriptions;
        }
    }

    public static class QueueDescription {
        private String queueName;
        private String queuePath;
        private String capacity;
        private String maximumCapacity;
        private String queueState;
        private List<QueueDescription> childQueues;

        public String getQueueName() {
            return queueName;
        }

        public void setQueueName(String queueName) {
            this.queueName = queueName;
        }

        public String getQueuePath() {
            return queuePath;
        }

        public void setQueuePath(String queuePath) {
            this.queuePath = queuePath;
        }

        public String getCapacity() {
            return capacity;
        }

        public void setCapacity(String capacity) {
            this.capacity = capacity;
        }

        public String getMaximumCapacity() {
            return maximumCapacity;
        }

        public void setMaximumCapacity(String maximumCapacity) {
            this.maximumCapacity = maximumCapacity;
        }

        public String getQueueState() {
            return queueState;
        }

        public void setQueueState(String queueState) {
            this.queueState = queueState;
        }

        public List<QueueDescription> getChildQueues() {
            return childQueues;
        }

        public void setChildQueues(List<QueueDescription> childQueues) {
            this.childQueues = childQueues;
        }
    }

}
