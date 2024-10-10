package com.dtstack.engine.common.enums;

/**
 * Reason:
 * Date: 2017/11/10
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
public enum EDeployType {
    //
    STANDALONE(0, "standalone"),
    //
    YARN(1, "yarn"),
    //
    KUBERNETES(2, "kubernetes");

    Integer type;

    String name;

    EDeployType(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static EDeployType getDeployType(Integer type){
        if (type == null) {
            return null;
        }

        if (type < 0) {
            throw new IllegalArgumentException("The code of EDeployType must not be negative.");
        }
        for(EDeployType eType : EDeployType.values()){
            if(eType.getType().equals(type)){
                return eType;
            }
        }
        return null;
    }
}
