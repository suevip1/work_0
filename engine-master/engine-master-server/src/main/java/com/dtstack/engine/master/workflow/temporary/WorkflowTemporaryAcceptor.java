package com.dtstack.engine.master.workflow.temporary;

import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.master.workflow.temporary.event.WorkflowTemporaryExecuteEvent;
import com.dtstack.engine.master.workflow.temporary.model.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author leon
 * @date 2023-03-09 15:35
 **/
@Component
public class WorkflowTemporaryAcceptor
        implements DisposableBean, Runnable, ApplicationListener<WorkflowTemporaryExecuteEvent> {

    private final Logger LOGGER = LoggerFactory.getLogger(WorkflowTemporaryAcceptor.class);

    private final WorkflowTemporaryActuator workflowTemporaryActuator;

    private final ThreadPoolTaskExecutor workflowTemporaryAcceptorExecutor;

    private final LinkedBlockingQueue<Workflow> workflowTemporaryAcceptorQueue;

    private final WorkflowDBOperator workflowDBOperator;

    protected final AtomicBoolean RUNNING = new AtomicBoolean(false);

    public WorkflowTemporaryAcceptor(WorkflowTemporaryActuator workflowTemporaryActuator,
                                     ThreadPoolTaskExecutor workflowTemporaryAcceptorExecutor,
                                     LinkedBlockingQueue<Workflow> workflowTemporaryAcceptorQueue,
                                     WorkflowDBOperator workflowDBOperator) {
        this.workflowTemporaryActuator = workflowTemporaryActuator;
        this.workflowTemporaryAcceptorExecutor = workflowTemporaryAcceptorExecutor;
        this.workflowTemporaryAcceptorQueue = workflowTemporaryAcceptorQueue;
        this.workflowDBOperator = workflowDBOperator;
    }

    @Override
    public void run() {
        while (RUNNING.get()) {
            Workflow workflow = null;
            try {
                workflow = workflowTemporaryAcceptorQueue.take();
                LOGGER.info("flowJobId:{},workflow take from the execution queue",
                        workflow.getFlowJobId());
                asyncExecuteWorkflow(workflow);
            } catch (Throwable e) {
                String errorMessage = ExceptionUtil.getErrorMessage(e);
                workflowDBOperator.updateWorkflowStatus(workflow, RdosTaskStatus.FAILED, errorMessage);
                LOGGER.error("flowJobId:{},workflow execution failed:{}",
                        Optional.ofNullable(workflow).map(Workflow::getFlowJobId).orElse(null),
                        errorMessage);
            }
        }
    }

    private void asyncExecuteWorkflow(Workflow workflow) {
        workflowTemporaryAcceptorExecutor.submit(() -> {
            try {
                LOGGER.info("flowJobId:{},workflow start execute", workflow.getFlowJobId());
                workflowTemporaryActuator.execute(workflow);
            } catch (Throwable e) {
                String errorMessage = ExceptionUtil.getErrorMessage(e);
                workflowDBOperator.updateWorkflowStatus(workflow, RdosTaskStatus.FAILED, errorMessage);
                LOGGER.error("flowJobId:{},workflow execution failed:{}",
                        Optional.ofNullable(workflow).map(Workflow::getFlowJobId).orElse(null),
                        errorMessage);
            }
        });
    }


    public void putWorkflow(Workflow workflow) {
        try {
            if (Objects.isNull(workflow) || !workflowDBOperator.isWorkflowExistInDB(workflow.getFlowJobId())) {
                return;
            }
            workflowTemporaryAcceptorQueue.put(workflow);
            LOGGER.info("flowJobId:{},workflow enters the execution queue, current execution queue size:{}",
                    workflow.getFlowJobId(),
                    workflowTemporaryAcceptorQueue.size());
        } catch (Throwable e) {
            String errorMessage = ExceptionUtil.getErrorMessage(e);
            workflowDBOperator.updateWorkflowStatus(workflow, RdosTaskStatus.SUBMITFAILD, errorMessage);
            LOGGER.error("flowJobId:{},workflow enters the execution queue error, status change to SUBMIT_FAILED(9), errorMsg: {}",
                    workflow.getFlowJobId(),
                    errorMessage, e);
        }
    }

    @Override
    public void onApplicationEvent(WorkflowTemporaryExecuteEvent event) {
        if (!RUNNING.get()) {
            RUNNING.compareAndSet(false, true);
            workflowTemporaryAcceptorExecutor.submit(this);
        }
        Workflow workflow = (Workflow) event.getSource();
        LOGGER.info("flowJobId:{},workflow execution event arrival",
                workflow.getFlowJobId());
        putWorkflow(workflow);
    }


    @Override
    public void destroy() throws Exception {
        RUNNING.set(false);
        workflowTemporaryAcceptorExecutor.shutdown();
        LOGGER.info("destroy [WorkflowTemporaryAcceptor] done.");
    }

}
