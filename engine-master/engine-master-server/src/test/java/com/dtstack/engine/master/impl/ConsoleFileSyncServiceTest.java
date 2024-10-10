package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.alibaba.testable.processor.annotation.EnablePrivateAccess;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.dto.ConsoleFileSyncDirectoryDTO;
import com.dtstack.engine.master.mockcontainer.impl.ConsoleFileSyncServiceMock;
import org.junit.Before;
import org.junit.Test;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-12 19:18
 */
@MockWith(value = ConsoleFileSyncServiceMock.class)
@EnablePrivateAccess(srcClass = ConsoleFileSyncService.class)
public class ConsoleFileSyncServiceTest {
    ConsoleFileSyncService cfService = new ConsoleFileSyncService();

    @Before
    public void before() {
        PrivateAccessor.set(cfService, "consoleFileSyncRootDirectory", ConsoleFileSyncServiceMock.path);
        cfService.init();
    }

    @Test
    public void allDirectories() {
        cfService.allDirectories();
    }

    @Test(expected = RdosDefineException.class)
    public void allFilesByDirectory() {
        ConsoleFileSyncDirectoryDTO result = cfService.allFilesByDirectory(ConsoleFileSyncServiceMock.defaultClusterPath);
        System.out.println(JSONObject.toJSONString(result));
    }

    @Test(expected = RdosDefineException.class)
    public void save() {
        ConsoleFileSyncDirectoryDTO dirDto = JSONObject.parseObject("{\"directory\":\"/Users/qiuyun/Workspace/DAGScheduleX-Duplicate/engine-master/engine-master-server/target/test-classes/consoleFileSyncRootDir/cluster_default\",\"files\":[{\"fileName\":\"core-site.xml\",\"isChosen\":1},{\"fileName\":\"hdfs-site.xml\",\"isChosen\":1},{\"fileName\":\"yarn-site.xml\",\"isChosen\":1},{\"fileName\":\"hive-site.xml\",\"isChosen\":1}]}\n",
                ConsoleFileSyncDirectoryDTO.class);
        dirDto.setIsSync(1);
        dirDto.setClusterId(1L);
        cfService.save(dirDto);
    }

    @Test
    public void load() {
        System.out.println(cfService.load(-1L));
    }

    @Test
    public void sync() {
        cfService.sync();
    }
}