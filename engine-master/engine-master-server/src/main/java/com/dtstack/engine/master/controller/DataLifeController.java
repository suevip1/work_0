package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.datalife.DataLifeVO;
import com.dtstack.engine.master.impl.DataLifeService;
import com.dtstack.engine.master.router.permission.Authenticate;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author jiuwei@dtstack.com
 **/
@RestController
@RequestMapping("/node/dataLife")
@Api(value = "/node/dataLife", tags = {"生命周期接口"})
public class DataLifeController {

    @Autowired
    private DataLifeService dataLifeService;

    /**
     * 获取生命周期配置
     */

    @RequestMapping(value = "/getDataLifeConfigPage", method = {RequestMethod.POST})
    @Authenticate(all = "console_global_data_life_view_all")
    public List<DataLifeVO> getTableLifeSettingPage() {
        return dataLifeService.getDataLifeConfigPage();
    }

    /**
     * 更新生命周期配置
     */

    @RequestMapping(value = "/updateDataLifeConfig", method = {RequestMethod.POST})
    @Authenticate(all = "console_global_data_life_edit_all")
    public void updateDataLifeConfig(@RequestParam(value = "id") Long id,@RequestParam(value = "mainDataTableName") String mainDataTableName,
                                        @RequestParam(value = "softLifeDay") Integer softLifeDay) {
         dataLifeService.updateDataLifeConfig(id,mainDataTableName,softLifeDay);
    }

    /**
     * 手动触发
     */
    @RequestMapping(value = "/cleanManual", method = {RequestMethod.POST})
    public void cleanDataManual() {
        dataLifeService.cleanDataManual();
    }



}
