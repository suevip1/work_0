package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.vo.console.KerberosProjectVO;
import com.dtstack.engine.master.impl.ConsoleKerberosProjectService;
import com.dtstack.engine.master.mapstruct.KerberosProjectStruct;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: dazhi
 * @Date: 2022/3/14 5:07 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/sdk/kerberos/project")
public class ConsoleKerberosProjectSdkController {

    @Autowired
    private ConsoleKerberosProjectService consoleKerberosProjectService;

    @Autowired
    private KerberosProjectStruct kerberosProjectStruct;

    @RequestMapping(value = "/addOrUpdateKer", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务环境参数")
    public Boolean addOrUpdateKerberosProject(@RequestBody @Validated KerberosProjectVO kerberosProjectVO) {
        return consoleKerberosProjectService.addOrUpdateKerberosProject(kerberosProjectVO);
    }

    @RequestMapping(value = "/findKerberosProject", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务环境参数")
    public KerberosProjectVO findKerberosProject(@RequestParam("projectId") Long projectId,@RequestParam("appType") Integer appType) {
        return kerberosProjectStruct.toKerberosProjectVO(consoleKerberosProjectService.findKerberosProject(projectId,appType));
    }

}
