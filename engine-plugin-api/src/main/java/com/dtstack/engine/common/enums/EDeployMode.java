package com.dtstack.engine.common.enums;

import com.dtstack.engine.common.exception.RdosDefineException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum EDeployMode{
    PERJOB("perjob", 1),
    SESSION("session", 2),
    STANDALONE("standalone", 3);
    private String mode;
    private Integer type;

    public String getMode() {
        return mode;
    }

    public Integer getType() {
        return type;
    }

    EDeployMode(String mode, Integer type) {
        this.mode = mode;
        this.type = type;
    }

    public static EDeployMode getByType(Integer type) {
        for (EDeployMode value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        throw new RdosDefineException(String.format("不支持的模式:%s", type));
    }

    public static EDeployMode getByMode(String mode) {
        for (EDeployMode value : values()) {
            if (value.mode.equals(mode)) {
                return value;
            }
        }
        return null;
    }

    public static List<String> allDeployModes() {
        return Collections.unmodifiableList(Arrays.stream(EDeployMode.values()).map(EDeployMode::getMode).collect(Collectors.toList()));
    }
}