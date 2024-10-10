package com.dtstack.schedule.common.dto;

import java.sql.Timestamp;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2023-02-08 10:36
 */
public class SystemTimeParamQueryDTO {
    /**
     * 指令
     */
    private final String realCommand;

    /**
     * 实际运行时间
     */
    private final Timestamp runtime;

    /**
     * 计划时间
     */
    private final String cycTime;

    public String getRealCommand() {
        return realCommand;
    }

    public Timestamp getRuntime() {
        return runtime;
    }

    public String getCycTime() {
        return cycTime;
    }

    private SystemTimeParamQueryDTO(SystemTimeParamQueryDTOBuilder builder) {
        this.realCommand = builder.realCommand;
        this.runtime = builder.runtime;
        this.cycTime = builder.cycTime;
    }

    public static final class SystemTimeParamQueryDTOBuilder {
        private String realCommand;

        private Timestamp runtime;

        private String cycTime;

        public SystemTimeParamQueryDTOBuilder realCommand(String realCommand) {
            this.realCommand = realCommand;
            return this;
        }

        public SystemTimeParamQueryDTOBuilder runtime(Timestamp runtime) {
            this.runtime = runtime;
            return this;
        }

        public SystemTimeParamQueryDTOBuilder cycTime(String cycTime) {
            this.cycTime = cycTime;
            return this;
        }

        public static SystemTimeParamQueryDTOBuilder builder() {
            return new SystemTimeParamQueryDTOBuilder();
        }

        public SystemTimeParamQueryDTO build() {
            return new SystemTimeParamQueryDTO(this);
        }
    }
}