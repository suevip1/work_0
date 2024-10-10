package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.UrlUtil;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.manager.FsyncManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-11-01 23:52
 */
@RestController
@RequestMapping("/node/fsync")
@Api(value = "/node/fsync", tags = {"落盘文件查询接口"})
public class FsyncController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FsyncController.class);

    private static final String LIST_DATA = "listData";

    @Autowired
    private FsyncManager fsyncManager;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private EnvironmentContext environmentContext;

    @ApiOperation(value = "落盘文件查询接口")
    @PostMapping(value = LIST_DATA)
    public List listFsyncData(@RequestBody FsyncQueryDTO fsyncQueryDTO,
                              HttpServletResponse response) {
        String jobId = fsyncQueryDTO.getJobId();
        ScheduleJob job = scheduleJobService.getSimpleByJobId(jobId, IsDeletedEnum.NOT_DELETE.getType());
        // 如果 job 不存在，或不为成功状态，直接返回空
        if (job == null || !RdosTaskStatus.FINISHED.getStatus().equals(job.getStatus())) {
            return Collections.emptyList();
        }
        // 判断 nodeAddress 是否是当前节点
        String nodeAddress = job.getNodeAddress();
        // 如果是当前节点，查询结果直接返回
        if (environmentContext.getLocalAddress().equals(nodeAddress)) {
            String fsyncFileDir = fsyncManager.generateFsyncDir(job.getCycTime(),job.getGmtCreate(),jobId);
            String fsyncFilePath = fsyncManager.concatFilePath(fsyncFileDir, fsyncQueryDTO.getSqlId());

            List list;
            try {
                list = fsyncManager.readFromFile(fsyncFilePath, List.class);
                return CollectionUtils.isEmpty(list) ? Collections.emptyList() : list;
            } catch (Exception e) {
                // 及时抛出异常，避免排障时无所适从
                throw new RdosDefineException(
                        String.format("listFsyncData error, fsyncQueryDTO:{}", fsyncQueryDTO), e);
            }
        } else {
            // 如果不是当前节点，返回重定向的地址，要求离线无法访问成功时，及时抛出异常
            response.setHeader("location", UrlUtil.getHttpUrl(nodeAddress, "node/fsync/" + LIST_DATA));
            // 设置重定向状态码
            response.setStatus(HttpStatus.SC_TEMPORARY_REDIRECT);
            return Collections.emptyList();
        }
    }

    static class FsyncQueryDTO {
        private String jobId;

        private String sqlId;

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public String getSqlId() {
            return sqlId;
        }

        public void setSqlId(String sqlId) {
            this.sqlId = sqlId;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("FsyncQueryDTO{");
            sb.append("jobId='").append(jobId).append('\'');
            sb.append(", sqlId='").append(sqlId).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
