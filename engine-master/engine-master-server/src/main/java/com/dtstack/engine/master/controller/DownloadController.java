package com.dtstack.engine.master.controller;

import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.AlertGateTypeEnum;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.CalenderService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ParamService;
import com.dtstack.engine.master.utils.CsvUtil;
import com.dtstack.engine.master.utils.FileUtil;
import com.dtstack.schedule.common.enums.CalenderTimeFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;


@RestController
@RequestMapping("/node/download")
@Api(value = "/node/download", tags = {"下载接口"})
public class DownloadController extends AbstractDownloadController {

    @Autowired
    private ComponentService componentService;

    @Autowired
    private CalenderService calenderService;

    @Autowired
    private ParamService paramService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Value("${user.dir}")
    private String path;

    @RequestMapping(value = "/component/downloadFile", method = {RequestMethod.GET})
    @ApiOperation(value = "下载文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "0:kerberos配置文件 1:配置文件 2:模板文件 3:ssl上传配置 4:ssl模版", required = true, dataType = "int"),
            @ApiImplicitParam(name = "clusterId", value = "集群id", required = true, dataType = "long"),
            @ApiImplicitParam(name = "versionName", value = "组件版本名称", required = true, dataType = "string"),
            @ApiImplicitParam(name = "deployType", value = "部署模式 0:standalone 1:yarn 2: k8s", required = true, dataType = "int"),
    })
    public void handleDownload(@RequestParam(value = "componentId", required = false) Long componentId,
                               @RequestParam("type") Integer downloadType,
                               @RequestParam("componentType") Integer componentType,
                               @RequestParam("versionName") String versionName,
                               @RequestParam("clusterId") Long clusterId,
                               @RequestParam(value = "deployType", required = false) Integer deployType,
                               HttpServletResponse response) {
        File downLoadFile = componentService.downloadFile(componentId, downloadType, componentType, versionName, clusterId, deployType);
        downloadWithFile(downLoadFile, response, null, true);
    }

    @RequestMapping(value = "/downloadJar", method = {RequestMethod.GET})
    @ApiOperation(value = "下载文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "alertGateType", value = "MAIL(1), SMS(2), DINGDING(3),CUSTOMIZE(4);", required = true, dataType = "int")
    })
    public void handleDownload(@RequestParam(value = "alertGateType", required = false) Long alertGateType, HttpServletResponse response) {
        File downLoadFile = null;
        if (alertGateType == null) {
            throw new RdosDefineException("alertGateType不能为null");
        }
        AlertGateTypeEnum enumByCode = AlertGateTypeEnum.getEnumByCode(alertGateType.intValue());

        if (enumByCode == null) {
            throw new RdosDefineException("非法通道类型");
        }
        downLoadFile = new File(path + "/doc/" + enumByCode.name() + ".zip");
        downloadWithFile(downLoadFile,response,null,false);
    }


    @RequestMapping(value = "/downloadCalender", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "下载日历")
    public void downloadCalender(HttpServletResponse response, @RequestParam("calenderId") Long calenderId, @RequestParam("calenderTimeFormat") String timeFormat) throws IOException {
        File file;
        if (null != calenderId) {
            file = calenderService.downloadExcel(calenderId);
        } else {
            file = downloadCalendarTemplate(timeFormat);
        }
        downloadWithFile(file, response, "GBK", true);
    }

    @RequestMapping(value = "/downloadGlobalParamCalender", method = {RequestMethod.GET,RequestMethod.POST})
    @ApiOperation(value = "下载全局自定义参数配置的自定义日历")
    public void downloadGlobalParamCalender(HttpServletResponse response,
                                            @RequestParam("paramType") Integer paramType,
                                            @RequestParam("consoleParamId") Long consoleParamId,
                                            @RequestParam("dateFormat") String dateFormat) {
        File file;
        if (null != consoleParamId) {
            file = paramService.downloadExcel(consoleParamId);
        } else {
            // 下载模板
            file = downloadGlobalCalendarTemplate(paramType, dateFormat);
        }
        downloadWithFile(file, response, "GBK", true);
    }

    @RequestMapping(value = "/downloadAgentJobArchive", method = {RequestMethod.GET, RequestMethod.POST})
    public void downloadAgentJobArchive(HttpServletResponse response, @RequestParam("jobId") String jobId) {
        File file = getAgentJobArchiveFile(jobId);
        //任务zip不能删除  可能会有重试  由任务结束后删除
        downloadWithFile(file, response, null, false);
    }


    /**
     * 从 /tmp/agentJobArchive/{jobId} 路径下获取 .zip 文件
     * @param jobId
     * @return
     */
    private File getAgentJobArchiveFile(String jobId) {
        if (jobId == null || jobId.trim().isEmpty()) {
            throw new RdosDefineException("jobId must not be null or empty.");
        }

        String agentJobArchiveTmpDir = environmentContext.getAgentJobArchiveTmpDir();
        String path = Paths.get(agentJobArchiveTmpDir, jobId).toString();
        File directory = new File(path);

        if (!directory.exists() || !directory.isDirectory()) {
            throw new RdosDefineException("The path does not exist or is not a directory.");
        }

        File[] zipFiles = directory.listFiles((dir, name) -> name.endsWith(".zip"));

        if (zipFiles == null || zipFiles.length == 0) {
            return null;
        }

        return zipFiles[0];
    }

    public File downloadCalendarTemplate(String calenderTimeFormat) {
        CalenderTimeFormat timeFormat = CalenderTimeFormat.timeFormat(calenderTimeFormat);
        // 模版文件夹路径
        String templateDirPath =  ConfigConstant.USER_DIR_DOWNLOAD + File.separator;
        String templatePath = String.format(GlobalConst.CALENDER_TEMPLATE, templateDirPath + ConfigConstant.CALENDAR_TEMPLATE_DIR_PREFIX + timeFormat.getFormat());
        // 模版文件存在直接返回
        File calendarFile = new File(templatePath);
        if (!calendarFile.exists()) {
            // 判断模版文件存放目录是否存在，不存在则创建
            FileUtil.mkdirsIfNotExist(templateDirPath);
            // 写 csv 文件
            String[] title = new String[]{String.format(GlobalConst.CALENDER_TITLE, timeFormat.getFormat())};
            CsvUtil.writerCsv(templatePath, title, null, false);
        }
        return calendarFile;
    }

    public File downloadGlobalCalendarTemplate(Integer paramType, String dataFormat) {
        // 模版文件夹路径
        String templateDirPath =  ConfigConstant.USER_DIR_DOWNLOAD + File.separator;
        String fileName = paramService.getFileName(paramType);
        String templatePath = templateDirPath + fileName;
        // 模版文件存在直接返回
        File calendarFile = new File(templatePath);
        if (!calendarFile.exists()) {
            // 判断模版文件存放目录是否存在，不存在则创建
            FileUtil.mkdirsIfNotExist(templateDirPath);
            // 写 csv 文件
            String[] titles = paramService.getTitles(paramType, dataFormat);
            CsvUtil.writerCsv(templatePath, titles, null, false);
        }
        return calendarFile;
    }

}
