package com.dtstack.engine.api.dto;

import com.dtstack.engine.api.domain.ConsoleParam;

/**
 * @author leon
 * @date 2022-11-14 20:18
 **/
public class ConsoleParamBO extends ConsoleParam {

    private String offset;

    private String replaceTarget;

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getReplaceTarget() {
        return replaceTarget;
    }

    public void setReplaceTarget(String replaceTarget) {
        this.replaceTarget = replaceTarget;
    }
}
