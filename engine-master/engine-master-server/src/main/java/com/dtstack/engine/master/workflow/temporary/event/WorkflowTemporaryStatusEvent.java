package com.dtstack.engine.master.workflow.temporary.event;

import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.workflow.temporary.WorkflowTemporaryJobEventListener;
import org.springframework.context.ApplicationEvent;

/**
 * 工作流状态变更事件
 * <p>
 * {@link WorkflowTemporaryJobEventListener#onApplicationEvent(WorkflowTemporaryStatusEvent)}
 *
 * @author leon
 * @date 2023-03-15 16:51
 **/
public class WorkflowTemporaryStatusEvent extends ApplicationEvent {

    public WorkflowTemporaryStatusEvent(Object source) {
        super(source);
    }

    public static class WorkflowTemporaryStatusEventWrapper {

        private String flowJobId;

        private RdosTaskStatus status;

        public WorkflowTemporaryStatusEventWrapper(String flowJobId, RdosTaskStatus status) {
            this.flowJobId = flowJobId;
            this.status = status;
        }

        public String getFlowJobId() {
            return flowJobId;
        }

        public void setFlowJobId(String flowJobId) {
            this.flowJobId = flowJobId;
        }

        public RdosTaskStatus getStatus() {
            return status;
        }

        public void setStatus(RdosTaskStatus status) {
            this.status = status;
        }


    }
}