package com.dtstack.engine.master.workflow.temporary.event;

import com.dtstack.engine.master.workflow.temporary.WorkflowTemporaryAcceptor;
import org.springframework.context.ApplicationEvent;

/**
 * 工作流临时运行事件
 * <p>
 * {@link WorkflowTemporaryAcceptor#onApplicationEvent(WorkflowTemporaryExecuteEvent)}
 *
 * @author leon
 * @date 2023-03-10 10:18
 **/
public class WorkflowTemporaryExecuteEvent extends ApplicationEvent {

    public WorkflowTemporaryExecuteEvent(Object source) {
        super(source);
    }
}
