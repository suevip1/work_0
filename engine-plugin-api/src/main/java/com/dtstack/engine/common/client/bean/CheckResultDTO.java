package com.dtstack.engine.common.client.bean;

import java.io.Serializable;

/**
 * @Auther: dazhi
 * @Date: 2021/11/5 4:18 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class CheckResultDTO implements Serializable {
    private boolean result;
    private String errorMsg;

    public static CheckResultDTO success() {
        CheckResultDTO checkResult = new CheckResultDTO();
        checkResult.setResult(true);
        return checkResult;
    }

    public static CheckResultDTO exception(String msg) {
        CheckResultDTO checkResult = new CheckResultDTO();
        checkResult.setResult(false);
        checkResult.setErrorMsg(msg);
        return checkResult;
    }

    public boolean isResult() {
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
}
