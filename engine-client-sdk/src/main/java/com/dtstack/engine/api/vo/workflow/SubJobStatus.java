package com.dtstack.engine.api.vo.workflow;

public class SubJobStatus {
        /**
         * 子任务 taskId
         */
        private Long taskId;
        /**
         * 子任务临时运行 jobId
         */
        private String jobId;
        /**
         * 子任务临时运行状态
         */
        private Integer status;

        private String taskName;

        public Long getTaskId() {
            return taskId;
        }

        public void setTaskId(Long taskId) {
            this.taskId = taskId;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }