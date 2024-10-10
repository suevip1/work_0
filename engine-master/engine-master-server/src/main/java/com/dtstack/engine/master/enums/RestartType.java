package com.dtstack.engine.master.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/11/17 下午3:39
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum RestartType {
    /**
     * 重跑当前节点
     */
    RESTART_CURRENT_NODE(0, Boolean.FALSE, Boolean.FALSE),

    /**
     * 重跑及其下游
     */
    RESTART_CURRENT_AND_DOWNSTREAM_NODE(1,Boolean.TRUE, Boolean.FALSE),

    /**
     * 重跑并恢复调度
     */
    RESTART_CURRENT_AND_RESUME_SCHEDULING(4,Boolean.TRUE, Boolean.FALSE),

    /**
     * 置成功不恢复调度
     */
    SET_SUCCESSFULLY(3, Boolean.FALSE, Boolean.TRUE),

    /**
     * 置成功并恢复调度
     */
    SET_SUCCESSFULLY_AND_RESUME_SCHEDULING(2,Boolean.TRUE, Boolean.TRUE),
    ;

    /**
     * 重跑类型
     */
    private final Integer type;

    /**
     * 重跑当前节点
     */
    private final Boolean justRunChild;

    /**
     * 当前节点是否值成功
     */
    private final Boolean setSuccess;

    RestartType(Integer type, Boolean justRunChild, Boolean setSuccess) {
        this.type = type;
        this.justRunChild = justRunChild;
        this.setSuccess = setSuccess;
    }

    public static RestartType getByCode(Integer type){
        if (type == null) {
            return null;
        }
        for (RestartType et:values()){
            if (et.getType().equals(type)){
                return et;
            }
        }
        return null;
    }


    public Integer getType() {
        return type;
    }

    public Boolean getJustRunChild() {
        return justRunChild;
    }

    public Boolean getSetSuccess() {
        return setSuccess;
    }
}
