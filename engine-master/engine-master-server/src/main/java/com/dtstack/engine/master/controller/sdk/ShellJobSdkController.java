package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.master.multiengine.job.FileCopyJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * shell job sdk controller
 *
 * @author ：wangchuan
 * date：Created in 16:05 2022/8/4
 * company: www.dtstack.com
 */
@RestController
@RequestMapping("/node/sdk/shellJob/")
public class ShellJobSdkController {

    @Autowired
    private FileCopyJobService fileCopyJobService;

    @RequestMapping(value = "/getShellContentByFileName", method = {RequestMethod.POST})
    public String getShellContentByFileName(@RequestParam("fileName") String fileName) throws Exception {
        return fileCopyJobService.getFileShellByName(fileName);
    }
}
