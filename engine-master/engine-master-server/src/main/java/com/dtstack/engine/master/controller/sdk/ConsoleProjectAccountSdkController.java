package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.vo.console.ConsoleProjectAccountVO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.ConsoleProjectAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 项目级账号绑定
 *
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-14 15:30
 */
@RestController
@RequestMapping("/node/sdk/project/account")
public class ConsoleProjectAccountSdkController {

    @Autowired
    private ConsoleProjectAccountService consoleProjectAccountService;

    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public Long add(@RequestBody @Validated ConsoleProjectAccountVO projectAccountVO) {
        return consoleProjectAccountService.add(projectAccountVO);
    }

    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public boolean modify(@RequestBody ConsoleProjectAccountVO projectAccountVO) {
        if (Objects.isNull(projectAccountVO.getId())) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        if (Objects.isNull(projectAccountVO.getStatus())
                || Objects.isNull(projectAccountVO.getUserName())
                || Objects.isNull(projectAccountVO.getPassword())) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        return consoleProjectAccountService.modify(projectAccountVO);
    }

    @RequestMapping(value = "/findByProjectAndComponent", method = {RequestMethod.POST})
    public ConsoleProjectAccountVO findByProjectAndComponent(@RequestBody ConsoleProjectAccountVO projectAccountVO) {
        if (Objects.isNull(projectAccountVO.getProjectId()) || Objects.isNull(projectAccountVO.getAppType())
                || Objects.isNull(projectAccountVO.getComponentTypeCode())) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        return consoleProjectAccountService.findByProjectAndComponent(projectAccountVO);
    }
}