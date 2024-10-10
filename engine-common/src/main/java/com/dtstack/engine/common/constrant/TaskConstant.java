package com.dtstack.engine.common.constrant;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/3/19
 */
public interface TaskConstant {

    /**
     * -----extraInfo中json key------
     **/
    String INFO = "info";

    String TASK_PARAMS_TO_REPLACE = "taskParamsToReplace";

    /**
     * jobId 占位标识符
     */
    String JOB_ID = "${jobId}";
    String UPLOADPATH = "${uploadPath}";
    String LAUNCH = "${launch}";
    String LAUNCH_CMD = "launch-cmd";
    String MODEL_PARAM = "${modelParam}";
    String FILE_NAME = "${file}";
    String DQ_JOB_ID = "#{jobId}";
    String DQ_FLOW_JOB_ID = "#{flowJobId}";

    String CMD_OPTS = "--cmd-opts";

    String RELY_TASK_STATUS_ERROR = "当前任务“%s”已是%s状态，无须去依赖";

    String NODE_GRANT_ID = "node.grantId";
}
