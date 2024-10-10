package com.dtstack.engine.api.vo;

/**
 * @auther: shuxing
 * @date: 2022/3/14 11:45 周一
 * @email: shuxing@dtstack.com
 * @description:
 */
public class JobStatusVo {

    private Long FINISHED;
    private Long RUNNING;
    private Long PARENTFAILED;
    private Long SUBMITFAILD;
    private Long FAILED;
    private Long UNSUBMIT;
    private Long WAITENGINE;
    private Long SUBMITTING;
    private Long CANCELED;
    private Long FROZEN;
    private Long ALL;

    public Long getFINISHED() {
        return FINISHED;
    }

    public void setFINISHED(Long FINISHED) {
        this.FINISHED = FINISHED;
    }

    public Long getRUNNING() {
        return RUNNING;
    }

    public void setRUNNING(Long RUNNING) {
        this.RUNNING = RUNNING;
    }

    public Long getPARENTFAILED() {
        return PARENTFAILED;
    }

    public void setPARENTFAILED(Long PARENTFAILED) {
        this.PARENTFAILED = PARENTFAILED;
    }

    public Long getSUBMITFAILD() {
        return SUBMITFAILD;
    }

    public void setSUBMITFAILD(Long SUBMITFAILD) {
        this.SUBMITFAILD = SUBMITFAILD;
    }

    public Long getFAILED() {
        return FAILED;
    }

    public void setFAILED(Long FAILED) {
        this.FAILED = FAILED;
    }

    public Long getUNSUBMIT() {
        return UNSUBMIT;
    }

    public void setUNSUBMIT(Long UNSUBMIT) {
        this.UNSUBMIT = UNSUBMIT;
    }

    public Long getWAITENGINE() {
        return WAITENGINE;
    }

    public void setWAITENGINE(Long WAITENGINE) {
        this.WAITENGINE = WAITENGINE;
    }

    public Long getSUBMITTING() {
        return SUBMITTING;
    }

    public void setSUBMITTING(Long SUBMITTING) {
        this.SUBMITTING = SUBMITTING;
    }

    public Long getCANCELED() {
        return CANCELED;
    }

    public void setCANCELED(Long CANCELED) {
        this.CANCELED = CANCELED;
    }

    public Long getFROZEN() {
        return FROZEN;
    }

    public void setFROZEN(Long FROZEN) {
        this.FROZEN = FROZEN;
    }

    public Long getALL() {
        return ALL;
    }

    public void setALL(Long ALL) {
        this.ALL = ALL;
    }

    @Override
    public String toString() {
        return "JobStatusVo{" +
                "FINISHED=" + FINISHED +
                ", RUNNING=" + RUNNING +
                ", PARENTFAILED=" + PARENTFAILED +
                ", SUBMITFAILD=" + SUBMITFAILD +
                ", FAILED=" + FAILED +
                ", UNSUBMIT=" + UNSUBMIT +
                ", WAITENGINE=" + WAITENGINE +
                ", SUBMITTING=" + SUBMITTING +
                ", CANCELED=" + CANCELED +
                ", FROZEN=" + FROZEN +
                ", ALL=" + ALL +
                '}';
    }
}
