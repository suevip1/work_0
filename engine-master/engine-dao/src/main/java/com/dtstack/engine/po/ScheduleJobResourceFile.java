package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;
import com.dtstack.engine.api.domain.JobResourceFile;

/**
 * @author leon
 * @date 2022-08-29 16:00
 **/
public class ScheduleJobResourceFile extends BaseEntity {

    private String jobId;

    /**
     * 0正常调度 1补数据 2临时运行
     */
    private Integer type;


    /**
     * 单个job涉及到的资源文件, 主要是 python 和 shell 任务 --files 和 dtscript.ship-files 后的内容
     * {@link JobResourceFile}
     */
    private String jobResourceFiles;


    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getJobResourceFiles() {
        return jobResourceFiles;
    }

    public void setJobResourceFiles(String jobResourceFiles) {
        this.jobResourceFiles = jobResourceFiles;
    }
}
