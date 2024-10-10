package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.vo.UicUserVo;
import com.dtstack.engine.master.impl.AccountService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @auther: shuxing
 * @date: 2022/3/14 15:45 周一
 * @email: shuxing@dtstack.com
 * @description:
 */
@RestController
@RequestMapping({"/node/sdk/account"})
public class AccountSdkController {

    @Autowired
    private AccountService accountService;


    @RequestMapping(value="/getTenantUnBandList", method = {RequestMethod.POST})
    @ApiOperation(value = "获取租户未绑定用户列表")
    public List<UicUserVo> getTenantUnBandList(@RequestParam("dtuicTenantId") Long dtuicTenantId, @RequestParam("engineType")Integer engineType) {
        return accountService.getTenantUnBandList(dtuicTenantId, engineType);
    }

}
