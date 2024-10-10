package com.dtstack.engine.master.vo;

import java.io.Serializable;

/**
 * @author yuebai
 * @date 2022/11/1
 */
public class SM2CheckVO implements Serializable {

    private String originStr;
    private String modifyStr;

    public String getOriginStr() {
        return originStr;
    }

    public void setOriginStr(String originStr) {
        this.originStr = originStr;
    }

    public String getModifyStr() {
        return modifyStr;
    }

    public void setModifyStr(String modifyStr) {
        this.modifyStr = modifyStr;
    }
}
