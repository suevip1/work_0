
package com.dtstack.engine.api.vo.task;


public class SaveTaskRefResultVO {

    public static final String REF_FAIL_NOT_EXIST = "任务引用失败，引用任务不存在";

    public static final String REF_FAIL_CYCLE = "任务引用失败，引用任务成环";

    public static final String REF_FAIL_TASK_TYPE_NOT_SUPPORT = "任务引用失败，引用任务类型不支持，目前仅支持 python, shell, python on agent, shell on agent 任务";


    private String msg;


    private Boolean success;

    public static SaveTaskRefResultVO success() {
        SaveTaskRefResultVO saveTaskTaskVO = new SaveTaskRefResultVO();
        saveTaskTaskVO.setSuccess(true);
        return saveTaskTaskVO;
    }

    public static SaveTaskRefResultVO fail(String msg) {
        SaveTaskRefResultVO saveTaskTaskVO = new SaveTaskRefResultVO();
        saveTaskTaskVO.setSuccess(false);
        saveTaskTaskVO.setMsg(msg);
        return saveTaskTaskVO;
    }

    public static SaveTaskRefResultVO notExistFail() {
        SaveTaskRefResultVO saveTaskTaskVO = new SaveTaskRefResultVO();
        saveTaskTaskVO.setSuccess(false);
        saveTaskTaskVO.setMsg(REF_FAIL_NOT_EXIST);
        return saveTaskTaskVO;
    }

    public static SaveTaskRefResultVO cycleFail() {
        SaveTaskRefResultVO saveTaskTaskVO = new SaveTaskRefResultVO();
        saveTaskTaskVO.setSuccess(false);
        saveTaskTaskVO.setMsg(REF_FAIL_CYCLE);
        return saveTaskTaskVO;
    }

    public static SaveTaskRefResultVO taskTypeNotSupportFail() {
        SaveTaskRefResultVO saveTaskTaskVO = new SaveTaskRefResultVO();
        saveTaskTaskVO.setSuccess(false);
        saveTaskTaskVO.setMsg(REF_FAIL_TASK_TYPE_NOT_SUPPORT);
        return saveTaskTaskVO;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
