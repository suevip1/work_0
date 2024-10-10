package com.dtstack.engine.master.dto;

/**
 * @Auther: dazhi
 * @Date: 2023-11-17 15:40
 * @Email: dazhi@dtstack.com
 * @Description: ExecutorConfigDTO
 */
public class ExecutorConfigDTO {

    private Integer threadNum;

    private Integer scanNum;

    private Boolean takeEffect;

    public Integer getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(Integer threadNum) {
        this.threadNum = threadNum;
    }

    public Integer getScanNum() {
        return scanNum;
    }

    public void setScanNum(Integer scanNum) {
        this.scanNum = scanNum;
    }

    public Boolean getTakeEffect() {
        return takeEffect;
    }

    public void setTakeEffect(Boolean takeEffect) {
        this.takeEffect = takeEffect;
    }
}
