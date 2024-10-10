package com.dtstack.engine.master.controller;

import com.dtstack.engine.master.dto.ConsoleFileSyncDirectoryDTO;
import com.dtstack.engine.master.impl.ConsoleFileSyncService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 控制台文件同步
 *
 * @author qiuyun
 * @version 1.0
 * @date 2021-12-29 15:13
 */
@RestController
@RequestMapping("/node/console/fileSync")
@Api(value = "/node/console/fileSync", tags = {"控制台文件同步"})
public class ConsoleFileSyncController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleFileSyncController.class);

    @Autowired
    private ConsoleFileSyncService consoleFileSyncService;

    @RequestMapping(value="/allDirectories", method = {RequestMethod.GET})
    @ApiOperation(value = "查询所有文件目录，备注：后台会查询根路径下的所有目录，由前端支持模糊搜索")
    public List<ConsoleFileSyncDirectoryDTO> allDirectories() {
        return consoleFileSyncService.allDirectories();
    }

    @RequestMapping(value="/allFilesByDirectory", method = {RequestMethod.GET})
    @ApiOperation(value = "查询指定目录下的所有文件")
    public ConsoleFileSyncDirectoryDTO allFilesByDirectory(@NotEmpty String directory) {
        // todo 是否写一个接口将文件和目录整合起来，让前端初始化的时候加载
        return consoleFileSyncService.allFilesByDirectory(directory);
    }

    @RequestMapping(value="/save", method = {RequestMethod.POST})
    @ApiOperation(value = "保存配置信息")
    public void save(@RequestBody ConsoleFileSyncDirectoryDTO directoryDTO) {
        consoleFileSyncService.save(directoryDTO);
    }

    @RequestMapping(value="/load", method = {RequestMethod.GET})
    @ApiOperation(value = "加载已保存的配置")
    public ConsoleFileSyncDirectoryDTO load(Long clusterId) {
        return consoleFileSyncService.load(clusterId);
    }
}
