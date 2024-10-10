package com.dtstack.engine.master.controller;

import com.alibaba.fastjson.parser.ParserConfig;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.RoleService;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.engine.master.router.permission.Authenticate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/node")
public class StatusController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusController.class);

    @Autowired
    private SessionUtil sessionUtil;

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/status")
    public String status(@RequestParam("dt_token") String dtToken) {
        UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);
        if(ParserConfig.getGlobalInstance().isSafeMode()){
            LOGGER.info("fastjson 开启了safeMode");
        }
        if (null == user) {
            throw new RdosDefineException(ErrorCode.USER_IS_NULL);
        }
        boolean isAdmin = roleService.checkIsSysAdmin(user);
        if (isAdmin) {
            return "SUCCESS";
        }
        throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
    }

    @RequestMapping(value = "/enterConsolePermission")
    @Authenticate(all = "console_enter_all")
    public String enterConsolePermission(@RequestParam("dt_token") String dtToken) {
        return "SUCCESS";
    }

    @RequestMapping(value = "/value")
    public String value(@RequestParam("value") String value) {
        return value;
    }

    @RequestMapping(value = "/health")
    public String health() {
        return "SUCCESS";
    }

}
