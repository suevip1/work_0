package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.po.ConsoleSSL;
import com.dtstack.engine.common.Resource;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.common.util.ZipUtil;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.dto.AddOrUpdateComponentResult;
import com.dtstack.engine.master.dto.ConsoleFileSyncDirectoryDTO;
import com.dtstack.engine.master.enums.DownloadType;
import com.dtstack.engine.master.utils.FileUtil;
import com.dtstack.engine.po.ConsoleFileSync;
import com.dtstack.engine.po.ConsoleFileSyncDetail;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件同步
 *
 * @author qiuyun
 * @version 1.0
 * @date 2021-12-29 15:31
 */
@Service
public class ConsoleFileSyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleFileSyncService.class);

    /**
     * 约定的第三方文件存放路径
     */
    private String consoleFileSyncRootDirectory;

    @Autowired
    private ConsoleFileSyncDao consoleFileSyncDao;
    @Autowired
    private ConsoleFileSyncDetailDao consoleFileSyncDetailDao;
    @Autowired
    private ClusterDao clusterDao;
    @Autowired
    private ComponentDao componentDao;
    @Autowired
    private ComponentService componentService;
    @Autowired
    private ComponentConfigDao componentConfigDao;
    @Autowired
    private EnvironmentContext env;

    private static final String HIVE_SITE_XML = "hive-site.xml";
    private static final List<String> YARN_OR_HDFS_MUST_FILE = Collections.unmodifiableList(Arrays.asList("core-site.xml",
            "hdfs-site.xml", "yarn-site.xml"));

    private static List<String> YARN_OR_HDFS_OTHER_FILE;
    @Value("${console.file.sync.yarn.other.file:hadoop-policy.xml,capacity-scheduler-bak.xml,capacity-scheduler.xml,httpfs-site.xml," +
            "kms-acls.xml,mapred-site.xml,kms-site.xml,yarn-site-docker.xml}")
    private void setYarnOrHdfsOtherFile(List<String> yarnOrHdfsOtherFile) {
        YARN_OR_HDFS_OTHER_FILE = Collections.unmodifiableList(yarnOrHdfsOtherFile);
    }

    /**
     * yarn 文件列表
     */
    private static List<String> YARN_OR_HDFS_TOTAL_FILE;
    /**
     * 总的文件列表范围
     */
    private static List<String> FILE_RANGE;

    private static final List<String> SSL_FILE_LIST = Collections.unmodifiableList(Arrays.asList("ssl-client.xml", "truststore.jks"));
    private static final String DOWNLOAD_ZIP = "download.zip";

    private static String uploadsDir = System.getProperty("user.dir") + File.separator + "file-uploads";
    private static String fileSyncDir = System.getProperty("user.dir") + File.separator + "fileSync";

    @PostConstruct
    public void init() {
        List<String> yarnOrHdfsTotalFiles = new ArrayList<>(YARN_OR_HDFS_MUST_FILE);
        if (CollectionUtils.isNotEmpty(YARN_OR_HDFS_OTHER_FILE)) {
            yarnOrHdfsTotalFiles.addAll(YARN_OR_HDFS_OTHER_FILE);
        }
        yarnOrHdfsTotalFiles.add(HIVE_SITE_XML);
        // yarn 文件列表
        YARN_OR_HDFS_TOTAL_FILE = Collections.unmodifiableList(yarnOrHdfsTotalFiles);

        List<String> fileRange = new ArrayList<>(YARN_OR_HDFS_TOTAL_FILE);
        fileRange.addAll(SSL_FILE_LIST);
        // 总的文件列表范围
        FILE_RANGE = Collections.unmodifiableList(fileRange);

        consoleFileSyncRootDirectory = env.getConsoleFileSyncRootDirectory();
        try {
            FileUtil.mkdirsIfNotExist(consoleFileSyncRootDirectory);
            FileUtil.mkdirsIfNotExist(fileSyncDir);
        } catch (Exception e) {
            LOGGER.error("ConsoleFileSyncService create dir error", e);
        }
    }

    /**
     * @return 配置的固定路径下的所有目录
     */
    public List<ConsoleFileSyncDirectoryDTO> allDirectories() {
        // todo 如果用户恶意不断调用，怎么办
        File rootDir = new File(consoleFileSyncRootDirectory);
        if (!rootDir.exists()) {
            return Collections.emptyList();
        }
        if (!rootDir.isDirectory()) {
            throw new RdosDefineException(ErrorCode.CONSOLE_FILE_SYNC_DIR_ERROR);
        }

        // 获取所有子目录
        Collection<File> files = FileUtil.listFilesAndDirs(rootDir, new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY);
        if (CollectionUtils.isEmpty(files)) {
            return Collections.emptyList();
        }
        List<ConsoleFileSyncDirectoryDTO> result = files.stream()
                .filter(f -> f.getParentFile().equals(rootDir))
                .sorted()
                .map(f -> new ConsoleFileSyncDirectoryDTO(f.getAbsolutePath()))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * @param directory
     * @return 指定目录下的所有文件
     */
    public ConsoleFileSyncDirectoryDTO allFilesByDirectory(String directory) {
        File specifyDir = this.validateDirExists(directory);

        List<File> files = FileUtil.listFiles(specifyDir, null, false)
                .stream()
                .filter(file -> !file.isHidden())
                .filter(file -> FILE_RANGE.contains(file.getName()))
                .collect(Collectors.toList());
        List<ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO> fileDTOList = files.stream()
                .sorted()
                .map(f -> {
                    ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO fileDTO = new ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO(f.getName());
                    if (YARN_OR_HDFS_MUST_FILE.contains(f.getName())) {
                        // 默认勾选
                        fileDTO.setIsChosen(ConfigConstant.YES);
                    }
                    if (HIVE_SITE_XML.equals(f.getName())) {
                        // 默认勾选
                        fileDTO.setIsChosen(ConfigConstant.YES);
                    }
                    return fileDTO;
                })
                .collect(Collectors.toList());

        this.sortByMustFileName(fileDTOList);

        ConsoleFileSyncDirectoryDTO directoryDTO = new ConsoleFileSyncDirectoryDTO();
        directoryDTO.setFiles(fileDTOList);
        directoryDTO.setDirectory(directory);
        return directoryDTO;
    }

    /**
     * 保存配置信息
     *
     * @param directoryDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(ConsoleFileSyncDirectoryDTO directoryDTO) {
        this.validateParam(directoryDTO);
        this.validateDirAndFileExist(directoryDTO);
        // 主键不存在，则为新增操作，否则为修改操作
        boolean isAdd = (directoryDTO.getId() == null);
        // 打开了同步开关
        boolean openSync = ConfigConstant.YES.equals(directoryDTO.getIsSync());

        ConsoleFileSync consoleFileSync = new ConsoleFileSync();
        consoleFileSync.setClusterId(directoryDTO.getClusterId());
        consoleFileSync.setSyncPath(directoryDTO.getDirectory());
        consoleFileSync.setIsSync(directoryDTO.getIsSync());
        consoleFileSync.setId(directoryDTO.getId());
        List<ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO> files = directoryDTO.getFiles();
        List<ConsoleFileSyncDetail> fileSyncDetails = files.stream().map(f -> {
            ConsoleFileSyncDetail fileSyncDetail = new ConsoleFileSyncDetail();
            fileSyncDetail.setFileName(f.getFileName());
            fileSyncDetail.setIsChosen(f.getIsChosen());
            fileSyncDetail.setClusterId(directoryDTO.getClusterId());
            return fileSyncDetail;
        }).collect(Collectors.toList());

        ConsoleFileSync dbFileSync = consoleFileSyncDao.getByClusterId(directoryDTO.getClusterId());
        if (isAdd) {
            if (dbFileSync != null) {
                throw new RdosDefineException(ErrorCode.CONSOLE_FILE_SYNC_CLUSTER_ALREADY_EXISTS);
            }
            consoleFileSyncDao.insertSelective(consoleFileSync);
            if (openSync && CollectionUtils.isNotEmpty(fileSyncDetails)) {
                consoleFileSyncDetailDao.batchSave(fileSyncDetails);
            }
        } else {
            // 更新
            if (dbFileSync == null) {
                throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
            }
            // 防止前端数据传错
            if (!directoryDTO.getId().equals(dbFileSync.getId())) {
                throw new RdosDefineException(ErrorCode.CONSOLE_FILE_SYNC_CLUSTER_ILLEGAL);
            }
            consoleFileSyncDao.updateByPrimaryKeySelective(consoleFileSync);

            if (openSync && CollectionUtils.isNotEmpty(fileSyncDetails)) {
                consoleFileSyncDetailDao.deleteByClusterId(directoryDTO.getClusterId());
                consoleFileSyncDetailDao.batchSave(fileSyncDetails);
            }
        }
    }

    /**
     * 加载保存的配置
     *
     * @param clusterId
     * @return
     */
    public ConsoleFileSyncDirectoryDTO load(Long clusterId) {
        ConsoleFileSync dbFileSync = consoleFileSyncDao.getByClusterId(clusterId);
        if (dbFileSync == null) {
            return null;
        }
        List<ConsoleFileSyncDetail> dbDetails = consoleFileSyncDetailDao.listByClusterId(clusterId);
        List<ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO> fileDTOS = dbDetails.stream().map(detail -> {
            ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO fileDTO = new ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO();
            fileDTO.setFileName(detail.getFileName());
            fileDTO.setIsChosen(detail.getIsChosen());
            return fileDTO;
        }).collect(Collectors.toList());

        this.sortByMustFileName(fileDTOS);

        ConsoleFileSyncDirectoryDTO directoryDTO = new ConsoleFileSyncDirectoryDTO();
        directoryDTO.setId(dbFileSync.getId());
        directoryDTO.setClusterId(dbFileSync.getClusterId());
        directoryDTO.setDirectory(dbFileSync.getSyncPath());
        directoryDTO.setIsSync(dbFileSync.getIsSync());
        directoryDTO.setFiles(fileDTOS);
        return directoryDTO;
    }

    /**
     * 配置文件同步
     */
    public void sync() {
        long start = System.currentTimeMillis();
        LOGGER.info("sync start...");
        List<ConsoleFileSync> syncDirs = consoleFileSyncDao.listSyncDirectories();
        if (CollectionUtils.isEmpty(syncDirs)) {
            LOGGER.info("no syncDirs, no sync.");
            return;
        }
        List<Long> syncClusterIds = syncDirs.stream().map(ConsoleFileSync::getClusterId).collect(Collectors.toList());
        List<ConsoleFileSyncDetail> syncFiles = consoleFileSyncDetailDao.listSyncFilesByClusterId(syncClusterIds);
        if (CollectionUtils.isEmpty(syncFiles)) {
            LOGGER.info("no syncFiles, no sync.");
            return;
        }
        List<ConsoleFileSyncDirectoryDTO> syncDirDTOList = this.parse2SyncDirectoryDTO(syncDirs, syncFiles);
        for (ConsoleFileSyncDirectoryDTO syncDirDTO : syncDirDTOList) {
            // todo multi thread optimize
            try {
                this.doSync(syncDirDTO);
            } catch (Exception e) {
                LOGGER.error("syncClusterId:{}, dir:{}, sync error", syncDirDTO.getClusterId(), syncDirDTO.getDirectory(), e);
            }
        }
        LOGGER.info("sync end, last:{} ms.", (System.currentTimeMillis() - start));
    }

    /**
     * 同步单个集群的配置文件信息
     *
     * @param syncDirDTO
     */
    private void doSync(ConsoleFileSyncDirectoryDTO syncDirDTO) {
        LOGGER.info("syncClusterId:{}, doSync:{}", syncDirDTO.getClusterId(), syncDirDTO);
        String dbDirectory = syncDirDTO.getDirectory();
        List<ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO> dbFiles = syncDirDTO.getFiles();
        // 本次勾选的所有文件
        File[] dbChosenSyncFiles = dbFiles.stream()
                .map(dbFile -> {
                    String localFilePath = dbDirectory + File.separator + dbFile.getFileName();
                    return new File(localFilePath);
                })
                .filter(File::exists)
                .sorted()
                .toArray(File[]::new);
        Long syncClusterId = syncDirDTO.getClusterId();
        if (ArrayUtils.isEmpty(dbChosenSyncFiles)) {
            LOGGER.info("syncClusterId:{}, not found chosenSync files, over.", syncClusterId);
            return;
        }
        // 按名称排序后计算所有文件的 md5 值
        String nowMd5 = MD5Util.getMd5StringFromFiles(dbChosenSyncFiles);
        // 判断是否需要同步
        boolean needSync = !StringUtils.equals(syncDirDTO.getLastSyncMd5(), nowMd5);
        LOGGER.info("syncClusterId:{}, is needSync? {}", syncClusterId, needSync);
        if (!needSync) {
            LOGGER.info("syncClusterId:{}, no needSync, over.", syncClusterId);
            return;
        }
        // yarnComponent 组件
        Component yarnComponent = componentDao.getByClusterIdAndComponentType(syncClusterId, EComponentType.YARN.getTypeCode(), null, null);
        if (yarnComponent == null) {
            LOGGER.error("syncClusterId:{}, yarnComponent not config, no sync, over", syncClusterId);
            return;
        }
        // hdfs maybe null
        Component hdfsComponent = componentDao.getByClusterIdAndComponentType(syncClusterId, EComponentType.HDFS.getTypeCode(), null, null);

        // 同步 yarn 或 hdfs 的配置文件
        this.doSyncYarnOrHdfsFiles(dbChosenSyncFiles, syncClusterId, yarnComponent, hdfsComponent);

        // 同步 ssl 配置文件
        this.doSyncSslFiles(dbChosenSyncFiles, syncClusterId, yarnComponent, hdfsComponent);

        // 都成功了才更新 md5；失败了不能更新，否则一旦更新，后续就不会同步了
        this.postSyncDir(syncClusterId, nowMd5);
    }

    /**
     * 同步 yarn 或 hdfs 的配置文件
     * @param dbChosenSyncFiles
     * @param syncClusterId
     * @param yarnComponent
     * @param hdfsComponent
     */
    private void doSyncYarnOrHdfsFiles(File[] dbChosenSyncFiles, Long syncClusterId, Component yarnComponent, Component hdfsComponent) {
        // 本次勾选的需要同步的 yarn/hdfs 组件相关文件
        List<File> nowNeedSyncYarnOrHdfsFiles = Arrays.stream(dbChosenSyncFiles)
                .filter(file -> YARN_OR_HDFS_TOTAL_FILE.contains(file.getName()))
                .collect(Collectors.toList());
        // fileSyncDir/clusterName/componentName
        String unZipDir = fileSyncDir + File.separator + componentService.buildComponentPath(syncClusterId, yarnComponent.getComponentTypeCode());
        List<File> syncFiles = this.compareWithRemote(nowNeedSyncYarnOrHdfsFiles, DownloadType.Config.getCode(), yarnComponent, unZipDir, syncClusterId);
        // 同步新文件到远端
        this.doSyncZip(syncFiles, syncClusterId, yarnComponent);
        if (hdfsComponent != null) {
            this.doSyncZip(syncFiles, syncClusterId, hdfsComponent);
        }
        this.postSyncFiles(syncClusterId, nowNeedSyncYarnOrHdfsFiles.stream().map(File::getName).collect(Collectors.toList()));
        this.cleanFiles(syncClusterId, unZipDir);
    }

    /**
     * 同步 ssl 配置文件
     * @param dbChosenSyncFiles
     * @param syncClusterId
     * @param yarnComponent
     * @param hdfsComponent
     */
    private void doSyncSslFiles(File[] dbChosenSyncFiles, Long syncClusterId, Component yarnComponent, Component hdfsComponent) {
        // 本次勾选的 ssl 文件
        List<File> nowNeedSyncSslFiles = Arrays.stream(dbChosenSyncFiles)
                .filter(file -> SSL_FILE_LIST.contains(file.getName()))
                .collect(Collectors.toList());
        boolean notSyncSslFiles = CollectionUtils.isEmpty(nowNeedSyncSslFiles);
        if (notSyncSslFiles) {
            // 未勾选，则不予同步
            LOGGER.info("syncClusterId:{}, user not choose ssl, no sync.", syncClusterId);
        } else {
            ConsoleSSL sslConfig = componentService.getSSLConfig(syncClusterId, yarnComponent.getComponentTypeCode(), null);
            if (sslConfig == null) {
                // 勾选了，但是远端没有 ssl 配置，则本次不处理
                LOGGER.info("syncClusterId:{}, componentTypeCode:{}, no sslConfig, no sync.", syncClusterId, yarnComponent.getComponentTypeCode());
            } else {
                // fileSyncDir/ssl/clusterName/componentName
                String unZipSslDir = fileSyncDir + File.separator + "ssl" + File.separator
                        + componentService.buildComponentPath(syncClusterId, yarnComponent.getComponentTypeCode());
                // 获取最终需要同步的 ssl 配置
                List<File> syncSslFiles = this.compareWithRemote(nowNeedSyncSslFiles, DownloadType.SSL_UPLOAD.getCode(), yarnComponent, unZipSslDir, syncClusterId);
                String sslZipName = StringUtils.isEmpty(yarnComponent.getSslFileName()) ? DOWNLOAD_ZIP : yarnComponent.getSslFileName();
                this.doSyncSsl(syncSslFiles, syncClusterId, yarnComponent, sslZipName);
                if (hdfsComponent != null) {
                    // 将 hdfs 同步的 ssl 文件跟 yarn 的 ssl 文件保持同步
                    this.doSyncSsl(syncSslFiles, syncClusterId, hdfsComponent, sslZipName);
                }
                this.postSyncFiles(syncClusterId, nowNeedSyncSslFiles.stream().map(File::getName).collect(Collectors.toList()));
                this.cleanFiles(syncClusterId, unZipSslDir);
            }
        }
    }

    /**
     * 同步 Yarn 或者 Hdfs 的配置文件
     *
     * @param syncFiles
     * @param syncClusterId
     * @param component
     */
    private void doSyncZip(List<File> syncFiles, Long syncClusterId, Component component) {
        if (CollectionUtils.isEmpty(syncFiles)) {
            throw new RdosDefineException("syncClusterId:" + syncClusterId + ", componentTypeCode:" + component.getComponentTypeCode()
                    + ", syncFiles empty");
        }
        String syncZipName = StringUtils.isEmpty(component.getUploadFileName()) ? DOWNLOAD_ZIP : component.getUploadFileName();
        String path = uploadsDir + File.separator + syncZipName;
        // 将多文件打成 zip，原先存在 zip 会被覆盖掉，不用担心无法 zip 成功
        ZipUtil.zipFile(path, syncFiles);
        File zip = new File(path);
        Resource zipResource = new Resource(syncZipName, path, (int) FileUtil.sizeOf(zip), null, zip.getName());
        List<Resource> resources = Collections.singletonList(zipResource);

        // 获取所有文件聚合成的新配置信息
        List<Object> config = componentService.config(resources, component.getComponentTypeCode(), false, null);
        if (CollectionUtils.isEmpty(config)) {
            throw new RdosDefineException("syncClusterId:" + syncClusterId + ", componentTypeCode:" + component.getComponentTypeCode()
                    + ", config parse error, no sync");
        }
        // 新的配置信息
        Map<String, Object> componentConfig = (Map) config.get(0);
        // ref: com.dtstack.engine.master.impl.ComponentConfigService.getComponentVoByComponent
        ComponentConfig configObj = componentConfigDao.listByKey(component.getId(), ConfigConstant.HADOOP_VERSION);
        String hadoopVersion = (configObj != null ? configObj.getValue() : null);

        AddOrUpdateComponentResult addOrUpdateComponentResult = componentService.addOrUpdateComponent(syncClusterId, JSONObject.toJSONString(componentConfig), resources,
                component.getVersionName(), component.getKerberosFileName(), null, component.getComponentTypeCode(),
                null, null, null, Boolean.FALSE,
                component.getIsDefault(), component.getDeployType(), component.getSslFileName());
        componentService.postAddOrUpdateComponent(addOrUpdateComponentResult);
    }

    /**
     * 同步 ssl 文件：只覆盖，不新增
     *
     * @param clusterId
     * @param component
     */
    private void doSyncSsl(List<File> syncSslFiles, Long clusterId, Component component, String sslZipName) {
        List<String> sslFileNames = syncSslFiles.stream().map(File::getName).collect(Collectors.toList());
        boolean isErrorSslFiles = !SSL_FILE_LIST.containsAll(sslFileNames);
        if (isErrorSslFiles) {
            // ssl 必须两个文件齐全才同步，否则视为异常
            throw new RdosDefineException("syncClusterId:" + clusterId + ", componentTypeCode:"
                    + component.getComponentTypeCode() + ", ssl file maybe lost, no sync");
        }
        String path = uploadsDir + File.separator + sslZipName;
        // 将 ssl 多文件打成 zip，原先存在 zip 会被覆盖掉，不用担心无法 zip 成功
        ZipUtil.zipFile(path, syncSslFiles);
        File sslFile = new File(path);
        Resource sslResource = new Resource(sslZipName, path, (int) FileUtil.sizeOf(sslFile), null, sslFile.getName());
        componentService.uploadSSL(sslResource, clusterId, component.getComponentTypeCode(), null);
    }

    /**
     * 清理临时文件
     *
     * @param syncClusterId
     * @param unZipDir
     */
    private void cleanFiles(Long syncClusterId, String unZipDir) {
        try {
            FileUtil.forceDelete(new File(unZipDir));
        } catch (Exception e) {
            LOGGER.error("syncClusterId:{}, unZipDir:{}, clean Files error", syncClusterId, unZipDir, e);
        }
    }

    /**
     * 本次需同步的文件与远端文件比较，找到最终需要同步的文件
     *
     * @param nowNeedSyncFiles
     * @param fileType
     * @param component
     * @param unZipDir
     * @param syncClusterId
     * @return
     */
    private List<File> compareWithRemote(List<File> nowNeedSyncFiles, Integer fileType, Component component, String unZipDir, Long syncClusterId) {
        if (CollectionUtils.isEmpty(nowNeedSyncFiles)) {
            return Collections.emptyList();
        }
        // 拉取远端 zip 到本地
        File originZip = componentService.downloadFile(component.getId(), fileType, component.getComponentTypeCode(), null, syncClusterId, null);
        ZipUtil.upzipFile(originZip, unZipDir);
        // 合并完成的文件列表
        Map<String, File> name2File = this.overrideOldFiles2Map(unZipDir, nowNeedSyncFiles);
        // 删掉拉取到本地的 zip
        ZipUtil.deletefile(originZip.getAbsolutePath());
        return new ArrayList<>(name2File.values());
    }

    /**
     * 文件新增或覆盖
     *
     * @param oldDirPath
     * @param newFiles
     * @return
     */
    private Map<String, File> overrideOldFiles2Map(String oldDirPath, List<File> newFiles) {
        File oldDir = new File(oldDirPath);
        Map<String, File> name2File = FileUtil.listFiles(oldDir, null, true).stream()
                .collect(Collectors.toMap(File::getName, x -> x, (x, y) -> x));
        // 新增或覆盖旧文件
        for (File newFile : newFiles) {
            name2File.put(newFile.getName(), newFile);
        }
        return name2File;
    }

    /**
     * 文件同步的后置处理：更新文件同步时间
     *
     * @param clusterId
     * @param syncFileNames
     */
    private void postSyncFiles(Long clusterId, List<String> syncFileNames) {
        if (CollectionUtils.isEmpty(syncFileNames)) {
            return;
        }
        consoleFileSyncDetailDao.modifySyncTime(clusterId, syncFileNames);
    }

    /**
     * 文件夹同步的后置处理：更新 md5
     *
     * @param clusterId
     * @param nowMd5
     */
    private void postSyncDir(Long clusterId, String nowMd5) {
        consoleFileSyncDao.modifyMd5(clusterId, nowMd5);
    }

    /**
     * 将要同步的文件按照集群进行归集
     *
     * @param syncDirs
     * @param syncFiles
     * @return
     */
    private List<ConsoleFileSyncDirectoryDTO> parse2SyncDirectoryDTO(List<ConsoleFileSync> syncDirs, List<ConsoleFileSyncDetail> syncFiles) {
        Map<Long, List<ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO>> clusterToFiles = syncFiles.stream()
                .map(syncFile -> {
                    ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO fileDTO = new ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO();
                    fileDTO.setFileName(syncFile.getFileName());
                    fileDTO.setIsChosen(syncFile.getIsChosen());
                    fileDTO.setLastSyncTime(syncFile.getLastSyncTime());
                    fileDTO.setClusterId(syncFile.getClusterId());
                    return fileDTO;
                }).collect(Collectors.groupingBy(ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO::getClusterId));
        return syncDirs.stream()
                .map(syncDir -> {
                    ConsoleFileSyncDirectoryDTO dirDTO = new ConsoleFileSyncDirectoryDTO();
                    dirDTO.setId(syncDir.getId());
                    dirDTO.setClusterId(syncDir.getClusterId());
                    dirDTO.setDirectory(syncDir.getSyncPath());
                    dirDTO.setIsSync(syncDir.getIsSync());
                    dirDTO.setLastSyncMd5(syncDir.getLastSyncMd5());
                    dirDTO.setFiles(clusterToFiles.get(syncDir.getClusterId()));
                    return dirDTO;
                }).collect(Collectors.toList());
    }

    private void validateParam(ConsoleFileSyncDirectoryDTO directoryDTO) {
        Long clusterId = directoryDTO.getClusterId();
        if (clusterId == null) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        Cluster cluster = clusterDao.getOne(clusterId);
        if (cluster == null) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
    }

    private void validateDirAndFileExist(ConsoleFileSyncDirectoryDTO directoryDTO) {
        Integer isSync = directoryDTO.getIsSync();
        if (ConfigConstant.NO.equals(isSync)) {
            return;
        }
        // 如果开启了同步，需要校验目录和文件的存在性
        String directory = directoryDTO.getDirectory();
        if (StringUtils.isEmpty(directory)) {
            throw new RdosDefineException(ErrorCode.CONSOLE_FILE_SYNC_FILE_DIR_EMPTY);
        }

        // 入参文件列表
        List<ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO> specifyFiles = directoryDTO.getFiles();
        if (CollectionUtils.isEmpty(specifyFiles)) {
            throw new RdosDefineException(ErrorCode.CONSOLE_FILE_SYNC_FILE_EMPTY);
        }
        List<String> specifyFileNames = specifyFiles.stream()
                .filter(f -> ConfigConstant.YES.equals(f.getIsChosen()))
                .map(ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO::getFileName)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(specifyFileNames)) {
            throw new RdosDefineException(ErrorCode.CONSOLE_FILE_SYNC_CHOSEN_FILE_EMPTY);
        }
        // 校验勾选文件必含指定文件
        if (!specifyFileNames.containsAll(YARN_OR_HDFS_MUST_FILE)) {
            throw new RdosDefineException(ErrorCode.CONSOLE_FILE_SYNC_MUST_FILE_EMPTY);
        }

        File specifyDir = this.validateDirExists(directory);

        Collection<File> localFiles = FileUtil.listFiles(specifyDir, null, false);
        List<String> localFileNames = localFiles.stream().map(File::getName).collect(Collectors.toList());
        for (ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO specifyFile : specifyFiles) {
            String specifyFileName = specifyFile.getFileName();
            // 校验入参文件是否实际存在
            if (!localFileNames.contains(specifyFileName)) {
                throw new RdosDefineException(specifyFileName + "," + ErrorCode.CONSOLE_FILE_SYNC_FILE_NOT_FOUND.getMsg());
            }
            // 校验入参文件不能超过限定范围
            if (!FILE_RANGE.contains(specifyFileName)) {
                throw new RdosDefineException(specifyFileName + "," + ErrorCode.CONSOLE_FILE_SYNC_FILE_OUT_RANGE.getMsg());
            }
        }
    }

    private File validateDirExists(String directory) {
        File specifyDir = new File(directory);
        if (!specifyDir.exists()
                || !specifyDir.isDirectory()
                || !specifyDir.getParent().equals(consoleFileSyncRootDirectory)) {
            throw new RdosDefineException(ErrorCode.CONSOLE_FILE_SYNC_FILE_DIR_NOT_FOUND.getMsg() + ":" + specifyDir);
        }
        return specifyDir;
    }

    /**
     * 将默认勾选文件置顶然后排序
     * @param fileDTOS
     */
    private void sortByMustFileName(List<ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO> fileDTOS) {
        if (CollectionUtils.isEmpty(fileDTOS)) {
            return;
        }
        List<ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO> mustFiles = new ArrayList<>();
        ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO tempHiveFile = null;
        List<ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO> otherFiles = new ArrayList<>();
        for (ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO fileDTO : fileDTOS) {
            if (YARN_OR_HDFS_MUST_FILE.contains(fileDTO.getFileName())) {
                mustFiles.add(fileDTO);
            } else if (HIVE_SITE_XML.equals(fileDTO.getFileName())) {
                tempHiveFile = fileDTO;
            }else {
                otherFiles.add(fileDTO);
            }
        }

        Collections.sort(mustFiles, Comparator.comparing(ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO::getFileName));
        Collections.sort(otherFiles, Comparator.comparing(ConsoleFileSyncDirectoryDTO.ConsoleFileSyncFileDTO::getFileName));
        // 保持原引用不变，放入排序后的结果
        fileDTOS.clear();
        fileDTOS.addAll(mustFiles);
        if (tempHiveFile != null) {
            fileDTOS.add(tempHiveFile);
        }
        fileDTOS.addAll(otherFiles);
    }
}