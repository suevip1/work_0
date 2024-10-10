package com.dtstack.engine.api.vo.job;

/**
 * 判断是否可以生成实例
 *
 * @author ：wangchuan
 * date：Created in 16:18 2022/8/23
 * company: www.dtstack.com
 */
public class JobGraphBuildJudgeVO {

    private Boolean result;

    private String msg;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static JobGraphBuildJudgeVO createErrorVo(String msg) {
        JobGraphBuildJudgeVO judgeVO = new JobGraphBuildJudgeVO();
        judgeVO.setResult(false);
        judgeVO.setMsg(msg);
        return judgeVO;
    }

    public static JobGraphBuildJudgeVO createSuccessVo() {
        JobGraphBuildJudgeVO judgeVO = new JobGraphBuildJudgeVO();
        judgeVO.setResult(true);
        return judgeVO;
    }
}
