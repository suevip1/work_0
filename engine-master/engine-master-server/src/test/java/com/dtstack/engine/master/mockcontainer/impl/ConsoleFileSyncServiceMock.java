package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.ConsoleFileSyncDao;
import com.dtstack.engine.dao.ConsoleFileSyncDetailDao;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ConsoleFileSyncService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.utils.FileUtil;
import com.dtstack.engine.po.ConsoleFileSync;
import com.dtstack.engine.po.ConsoleFileSyncDetail;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-12 19:20
 */
public class ConsoleFileSyncServiceMock extends BaseMock {
    public static String path = ConsoleFileSyncServiceMock.class.getClassLoader().getResource("consoleFileSyncRootDir").getPath();

    public static String defaultClusterPath = path + File.separator + "cluster_default";

    @MockInvoke(targetClass = ConsoleFileSyncDetailDao.class)
    void modifySyncTime(Long clusterId, List<String> fileNames) {
        return;
    }

    @MockInvoke(targetClass = ConsoleFileSyncDetailDao.class)
    List<ConsoleFileSyncDetail> listSyncFilesByClusterId(List<Long> clusterIds) {
        return staticListByClusterId(-1L);
    }

    @MockInvoke(targetClass = ConsoleFileSyncDetailDao.class)
    List<ConsoleFileSyncDetail> listByClusterId(Long clusterId) {
        return staticListByClusterId(clusterId);
    }

    static List<ConsoleFileSyncDetail> staticListByClusterId(Long clusterId) {
        if (clusterId.equals(-1L)) {
            ConsoleFileSyncDetail t1 = new ConsoleFileSyncDetail();
            t1.setFileName("core-site.xml");
            t1.setIsChosen(1);
            t1.setClusterId(clusterId);

            ConsoleFileSyncDetail t2 = new ConsoleFileSyncDetail();
            t2.setFileName("hdfs-site.xml");
            t2.setIsChosen(1);
            t2.setClusterId(clusterId);

            ConsoleFileSyncDetail t3 = new ConsoleFileSyncDetail();
            t3.setFileName("hive-site.xml");
            t3.setIsChosen(1);
            t3.setClusterId(clusterId);

            ConsoleFileSyncDetail t4 = new ConsoleFileSyncDetail();
            t4.setFileName("yarn-site.xml");
            t4.setIsChosen(1);
            t4.setClusterId(clusterId);

            return Lists.newArrayList(t1, t2, t3, t4);
        }
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ConsoleFileSyncDetailDao.class)
    int batchSave(List<ConsoleFileSyncDetail> list) {
        return 1;
    }

    @MockInvoke(targetClass = FileUtil.class)
    public static void forceDelete(File file) throws IOException {
        return;
    }

    @MockInvoke(targetClass = ConsoleFileSyncDao.class)
    void modifyMd5(Long clusterId, String md5) {
        return;
    }

    @MockInvoke(targetClass = ConsoleFileSyncDao.class)
    List<ConsoleFileSync> listSyncDirectories() {
        ConsoleFileSync c = staticGetByClusterId(-1L);
        return Lists.newArrayList(c);
    }


    @MockInvoke(targetClass = ConsoleFileSyncDao.class)
    int insertSelective(ConsoleFileSync record) {
        return 1;
    }

    @MockInvoke(targetClass = ComponentService.class)
    public File downloadFile(Long componentId, Integer downloadType, Integer componentType,
                             String versionName, Long clusterId, Integer deployType) {
        String file = getClass().getClassLoader().getResource("consoleFileSyncRootDir/hadoopConf.zip").getFile();
        return new File(file);
    }

    @MockInvoke(targetClass = ComponentService.class)
    public String buildComponentPath(Long clusterId, Integer componentCode) {
        return "CONSOLE_default/" + EComponentType.getByCode(componentCode).getName();
    }

    @MockInvoke(targetClass = ConsoleFileSyncDao.class)
    ConsoleFileSync getByClusterId(Long clusterId) {
        return staticGetByClusterId(clusterId);
    }

    static ConsoleFileSync staticGetByClusterId(Long clusterId) {
        if (clusterId.equals(1L)) {
            return null;
        }
        if (clusterId.equals(-1L)) {
            ConsoleFileSync f = new ConsoleFileSync();
            f.setClusterId(-1L);
            f.setSyncPath(defaultClusterPath);
            f.setIsDeleted(0);
            f.setIsSync(1);
            return f;
        }
        return null;
    }

    @MockInvoke(targetClass = ComponentDao.class)
    Component getByClusterIdAndComponentType(Long clusterId, Integer type, String componentVersion, Integer deployType) {
        return ComponentServiceMock.mockDefaultComponents()
                .stream().filter(t -> t.getComponentTypeCode().equals(type))
                .findFirst()
                .orElse(null);
    }

    @MockInvoke(targetClass = ConsoleFileSyncService.class)
    private void doSyncZip(List<File> syncFiles, Long syncClusterId, Component component) {
        return;
    }

    @MockInvoke(targetClass = ConsoleFileSyncService.class)
    private List<File> compareWithRemote(List<File> nowNeedSyncFiles, Integer fileType, Component component, String unZipDir, Long syncClusterId) {
        return Collections.emptyList();
    }

    @MockInvoke(targetClass = ClusterDao.class)
    Cluster getOne(Long clusterId) {
        return ClusterServiceMock.mockDefaultCluster();
    }
}
