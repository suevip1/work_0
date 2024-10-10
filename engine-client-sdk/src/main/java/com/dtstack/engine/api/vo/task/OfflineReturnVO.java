package com.dtstack.engine.api.vo.task;

/**
 * @Auther: dazhi
 * @Date: 2022/2/11 1:45 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class OfflineReturnVO {

    /**
     * 是否下线成功
     */
    private Boolean success;

    /**
     * 下线失败原因
     */
    private String msg;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
