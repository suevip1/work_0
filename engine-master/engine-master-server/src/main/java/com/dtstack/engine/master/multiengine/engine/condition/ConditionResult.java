package com.dtstack.engine.master.multiengine.engine.condition;

/**
 * 分支条件运行结果
 *
 * @author ：wangchuan
 * date：Created in 下午4:30 2022/6/29
 * company: www.dtstack.com
 */
public class ConditionResult {

    private boolean result;

    private String msg;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static ConditionResult createSuccessResult() {
        ConditionResult conditionResult = new ConditionResult();
        conditionResult.setResult(true);
        return conditionResult;
    }

    public static ConditionResult createFailedResult(String msg) {
        ConditionResult conditionResult = new ConditionResult();
        conditionResult.setResult(false);
        conditionResult.setMsg(msg);
        return conditionResult;
    }
}
