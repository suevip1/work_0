package com.dtstack.engine.api.pojo;

import org.apache.commons.lang3.StringUtils;

/**
 * 控制台参数校验结果
 *
 * @author ：wangchuan
 * date：Created in 10:59 2022/10/13
 * company: www.dtstack.com
 */
public class ConsoleParamCheckResult {

    private boolean result;

    private String key;

    private String errorMsg;

    public ConsoleParamCheckResult() {
    }

    public ConsoleParamCheckResult(boolean result, String key, String errorMsg) {
        this.result =result;
        this.key = key;
        this.errorMsg = errorMsg;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    private static ConsoleParamCheckResult createInstance(boolean result, String key) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("check key can't be empty.");
        }
        ConsoleParamCheckResult consoleParamCheckResult = new ConsoleParamCheckResult();
        consoleParamCheckResult.setResult(result);
        consoleParamCheckResult.setKey(key);
        return consoleParamCheckResult;
    }

    public static ConsoleParamCheckResult createSuccessResult(String key) {
        return createInstance(true, key);
    }

    public static ConsoleParamCheckResult createErrorResult(String key, String errorMsg) {
        ConsoleParamCheckResult instance = createInstance(false, key);
        instance.setErrorMsg(errorMsg);
        return instance;
    }
}
