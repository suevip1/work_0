package com.dtstack.engine.api.vo.job;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/10/27 11:45 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class RelyResultVO {

    private Boolean isSuccess;

    private String msg;

    /**
     * 去依赖成功实例
     */
    private List<String> relySuccessJob;

    /**
     * 去依赖失败的实例
     */
    private List<String> relyFailJob;

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<String> getRelySuccessJob() {
        return relySuccessJob;
    }

    public void setRelySuccessJob(List<String> relySuccessJob) {
        this.relySuccessJob = relySuccessJob;
    }

    public List<String> getRelyFailJob() {
        return relyFailJob;
    }

    public void setRelyFailJob(List<String> relyFailJob) {
        this.relyFailJob = relyFailJob;
    }
}
