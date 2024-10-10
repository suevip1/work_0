package com.dtstack.engine.master.controller;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.common.Resource;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.dto.AddOrUpdateComponentResult;
import com.dtstack.engine.master.impl.CalenderService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ParamService;
import com.dtstack.engine.master.router.permission.Authenticate;
import com.dtstack.engine.master.utils.FileUtil;
import com.dtstack.engine.master.utils.MultipartFileUtil;
import com.dtstack.schedule.common.enums.CalenderTimeFormat;
import com.dtstack.schedule.common.enums.EParamType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.dtstack.engine.common.constrant.ConfigConstant.ZIP_SUFFIX;

@RestController
@RequestMapping("/node/upload")
@Api(value = "/node/upload", tags = {"上传接口"})
public class UploadController {

    @Autowired
    private ComponentService componentService;

    @Autowired
    private CalenderService calenderService;

    @Autowired
    private ParamService paramService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    private static String uploadsDir = System.getProperty("user.dir") + File.separator + "file-uploads";

    @RequestMapping(value = "/component/config", method = {RequestMethod.POST})
    @ApiOperation(value = "解析zip中xml或者json")
    public List<Object> upload(@RequestParam("fileName") List<MultipartFile> files, @RequestParam("componentType") Integer componentType,
                               @RequestParam(value = "autoDelete", required = false) Boolean autoDelete, @RequestParam(value = "versionName", required = false) String versionName) {
        return componentService.config(getResourcesFromFiles(files, FileUtil.safeExtends), componentType, autoDelete, versionName);
    }

    @RequestMapping(value = "/component/addOrUpdateComponent", method = {RequestMethod.POST})
    @ApiOperation(value = "保存或更新组件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "集群id", required = true, dataType = "long"),
            @ApiImplicitParam(name = "versionName", value = "组件版本名称", required = true, dataType = "string"),
            @ApiImplicitParam(name = "kerberosFileName", value = "kerberos文件名", required = true, dataType = "string"),
            @ApiImplicitParam(name = "componentCode", value = "组件code", required = true, dataType = "int"),
            @ApiImplicitParam(name = "storeType", value = "存储组件code", required = true, dataType = "int"),
            @ApiImplicitParam(name = "isMetadata", value = "是否设置为metadata", required = true, dataType = "boolean"),
    })
    @Authenticate(all = "console_resource_cluster_change_all")
    public ComponentVO addOrUpdateComponent(@RequestParam("resources1") List<MultipartFile> files1, @RequestParam("resources2") List<MultipartFile> files2, @RequestParam("clusterId") Long clusterId,
                                            @RequestParam("componentConfig") String componentConfig, @RequestParam("versionName") String versionName,
                                            @RequestParam("kerberosFileName") String kerberosFileName, @RequestParam("componentTemplate") String componentTemplate,
                                            @RequestParam("componentCode") Integer componentCode, @RequestParam("storeType") Integer storeType,
                                            @RequestParam("principals") String principals, @RequestParam("principal") String principal, @RequestParam("isMetadata") boolean isMetadata,
                                            @RequestParam(value = "isDefault", required = false) Boolean isDefault, @RequestParam(value = "deployType", required = false) Integer deployType,
                                            @RequestParam(value = "sslFileName", required = false) String sslFileName) throws Exception {
        List<Resource> resources = getResourcesFromFiles(files1,FileUtil.safeExtends);
        List<Resource> resourcesAdd = getResourcesFromFiles(files2,FileUtil.safeExtends);
        resources.addAll(resourcesAdd);
        if (StringUtils.isBlank(componentConfig)) {
            componentConfig = new JSONObject().toJSONString();
        }
        AddOrUpdateComponentResult addOrUpdateComponentResult = componentService.addOrUpdateComponent(clusterId, componentConfig, resources, versionName,
                kerberosFileName, componentTemplate, componentCode, storeType,
                principals, principal, isMetadata, isDefault, deployType, sslFileName);

        componentService.postAddOrUpdateComponent(addOrUpdateComponentResult);
        return componentService.returnAfterProcessComponent(addOrUpdateComponentResult);
    }

    @RequestMapping(value = "/component/parseKerberos", method = {RequestMethod.POST})
    @ApiOperation(value = "解析kerberos文件中信息")
    public List<String> parseKerberos(@RequestParam("fileName") List<MultipartFile> files) {
        return componentService.parseKerberos(getResourcesFromFiles(files,FileUtil.safeExtends));
    }

    @RequestMapping(value = "/component/mergeKerberos", method = {RequestMethod.POST})
    public String mergeKerberos() {
        return componentService.mergeKrb5();
    }

    @RequestMapping(value = "/component/uploadKerberos", method = {RequestMethod.POST})
    public String uploadKerberos(@RequestParam("kerberosFile") List<MultipartFile> files, @RequestParam("clusterId") Long clusterId,
                                 @RequestParam("componentCode") Integer componentCode, @RequestParam("componentVersion") String componentVersion) {
        List<Resource> resources = getResourcesFromFiles(files,FileUtil.safeExtends);
        return componentService.uploadKerberos(resources, clusterId, componentCode, componentVersion);
    }

    @RequestMapping(value = "/component/uploadSSL", method = {RequestMethod.POST})
    public void uploadSSL(@RequestParam("sslFile") List<MultipartFile> files, @RequestParam("clusterId") Long clusterId,
                          @RequestParam("componentCode") Integer componentCode,
                          @RequestParam(value = "componentVersion", required = false) String componentVersion) {
        List<Resource> resources = getResourcesFromFiles(files,FileUtil.safeExtends);
        checkAmount(resources, ErrorCode.FILE_COUNT_ERROR.getMsg());
        judgeZipFile(resources.get(0));
        // componentVersion 预留
        componentService.uploadSSL(resources.get(0), clusterId, componentCode, componentVersion);
    }

    @PostMapping(value = "/parseCalenderExcel")
    @ApiOperation(value = "解析自定义调度表格")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "excel文件", required = true, dataType = "file"),
            @ApiImplicitParam(name = "calenderTimeFormat", value = "日历时间格式", dataType = "string"),
    })
    public List<String> parseCalenderExcel(@RequestParam("file") List<MultipartFile> files, @RequestParam("calenderTimeFormat") String calenderTimeFormat) throws Exception {
        List<Resource> resources = getResourcesFromFiles(files,FileUtil.safeExtends);
        if (CollectionUtils.isEmpty(resources)) {
            throw new RdosDefineException(ErrorCode.FILE_MISS);
        }
        return calenderService.parseExcel(new File(resources.get(0).getUploadedFileName()), CalenderTimeFormat.timeFormat(calenderTimeFormat).getFormat());
    }

    @RequestMapping(value = "/parseGlobalParamCalenderExcel", method = {RequestMethod.POST})
    @ApiOperation(value = "全局参数解析自定义调度表格")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateFormat", value = "日期格式", required = true, dataType = "string"),
            @ApiImplicitParam(name = "file", value = "csv文件", required = true, dataType = "file")
    })
    public List<String> parseGlobalParamCalenderExcel(@RequestParam("dateFormat") String dateFormat,
                                                      @RequestParam("paramType") Integer paramType,
                                                      @RequestParam("file") MultipartFile file) {
        if (!EParamType.isGlobalParamBaseCycTime(paramType) && StringUtils.isEmpty(dateFormat)) {
            throw new RdosDefineException("请先填写日期格式");
        }

        if (file == null) {
            throw new RdosDefineException(ErrorCode.FILE_NOT_UPLOAD);

        }
        File csvFile = MultipartFileUtil.parse2File(file);
        List<List<String>> csvContent = paramService.parseCsv(paramType, dateFormat, csvFile);
        return csvContent.stream()
                .map(l -> l.get(0))
                .sorted().collect(Collectors.toList());
    }

    @PostMapping(value = "/addOrUpdateCalenderExcel")
    @ApiOperation(value = "保存自定义调度表格")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "excel文件", required = true, dataType = "file"),
            @ApiImplicitParam(name = "calenderName", value = "日历名称", required = true, dataType = "string"),
            @ApiImplicitParam(name = "calenderTimeFormat", value = "日历时间格式", dataType = "string"),
            @ApiImplicitParam(name = "calenderId", value = "日历id", required = true, dataType = "long")
    })
    @Authenticate(all = "console_global_calender_change_all")
    public void addOrUpdateCalenderExcel(@RequestParam("file") List<MultipartFile> files,
                                         @RequestParam("calenderId") Long calenderId,
                                         @RequestParam("calenderTimeFormat") String calenderTimeFormat,
                                         @NotNull @RequestParam(value = "calenderName") String calenderName) throws Exception {
        List<Resource> resources = getResourcesFromFiles(files,FileUtil.safeExtends);
        if (CollectionUtils.isEmpty(resources) && null == calenderId) {
            throw new RdosDefineException(ErrorCode.FILE_MISS);
        }
        File file = null;
        if (!CollectionUtils.isEmpty(resources)) {
            file = new File(resources.get(0).getUploadedFileName());
        }
        calenderService.addOrUpdateCalenderExcel(file, calenderName, calenderId, CalenderTimeFormat.timeFormat(calenderTimeFormat).getFormat());
    }


    private void checkAmount(List<Resource> resources, String msg) {
        if (resources.size() != 1) {
            throw new RdosDefineException(msg);
        }
    }

    private void judgeZipFile(Resource resource) {
        if (resource != null && !resource.getFileName().endsWith(ZIP_SUFFIX)) {
            throw new RdosDefineException(ErrorCode.FILE_NOT_ZIP);
        }
    }

    private List<Resource> getResourcesFromFiles(List<MultipartFile> files, List<String> safeExtension) {
        if (CollectionUtils.isEmpty(files)) {
            return Collections.emptyList();
        }
        List<Resource> resources = new ArrayList<>(files.size());
        for (MultipartFile file : files) {
            String fileOriginalName = FilenameUtils.normalize(file.getOriginalFilename());
            String extension = FilenameUtils.getExtension(fileOriginalName);
            if (!safeExtension.contains(extension)) {
                throw new RdosDefineException(ErrorCode.FILE_SUFFIX_ERROR + extension);
            }
            String path = uploadsDir + File.separator + fileOriginalName;
            File saveFile = new File(path);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            try {
                file.transferTo(saveFile);
            } catch (Exception e) {
                LOGGER.error("", e);
                throw new RdosDefineException(ErrorCode.FILE_STORE_FAIL);
            }
            resources.add(new Resource(fileOriginalName, path, (int) file.getSize(), file.getContentType(), file.getName()));
        }
        return resources;
    }

}
