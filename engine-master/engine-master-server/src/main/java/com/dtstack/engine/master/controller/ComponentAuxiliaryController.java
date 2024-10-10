package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.param.AuxiliaryQueryParam;
import com.dtstack.engine.api.param.AuxiliarySelectQueryParam;
import com.dtstack.engine.api.vo.auxiliary.AuxiliaryConfigVO;
import com.dtstack.engine.api.vo.console.ComponentAuxiliaryVO;
import com.dtstack.engine.master.dto.ComponentAuxiliaryConfigDTO;
import com.dtstack.engine.master.impl.ComponentAuxiliaryService;
import com.dtstack.engine.po.ConsoleComponentAuxiliary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-04-13 16:25
 */
@RestController
@RequestMapping("/node/component/auxiliary")
@Api(value = "/node/component/auxiliary", tags = {"组件附属信息接口"})
public class ComponentAuxiliaryController {

    @Autowired
    private ComponentAuxiliaryService componentAuxiliaryService;

    @RequestMapping(value="/loadAuxiliaryTemplate", method = {RequestMethod.POST})
    @ApiOperation(value = "查询当前集群的附属配置信息")
    public List<ComponentAuxiliaryVO> loadAuxiliaryTemplate(@RequestBody ConsoleComponentAuxiliary auxiliary) {
        List<ComponentConfig> componentConfigs = componentAuxiliaryService.loadAuxiliaryTemplate(auxiliary);
        if (CollectionUtils.isEmpty(componentConfigs)) {
            return Collections.emptyList();
        }
        Collections.sort(componentConfigs, Comparator.comparing(ComponentConfig::getId));
        return tran2VoList(componentConfigs);
    }

    @RequestMapping(value="/listAuxiliaries", method = {RequestMethod.POST})
    @ApiOperation(value = "查询当前集群的附属配置信息")
    public List<ConsoleComponentAuxiliary> listAuxiliaries(@RequestBody ConsoleComponentAuxiliary auxiliary) {
        return componentAuxiliaryService.listAuxiliaries(auxiliary);
    }

    @RequestMapping(value="/queryAuxiliaryConfig", method = {RequestMethod.POST})
    @ApiOperation(value = "查询当前集群的knox配置信息")
    public ComponentAuxiliaryConfigDTO queryAuxiliaryConfig(@RequestBody ConsoleComponentAuxiliary auxiliary) {
        return componentAuxiliaryService.queryAuxiliaryConfig(auxiliary, true);
    }

    @RequestMapping(value="/queryAuxiliary", method = {RequestMethod.POST})
    @ApiOperation(value = "查询某一个或多个组件的附属配置信息")
    public List<AuxiliaryConfigVO> queryConfig(@Validated @RequestBody AuxiliaryQueryParam auxiliaryParam) {
        return componentAuxiliaryService.queryConfig(auxiliaryParam, true);
    }

    @RequestMapping(value="/queryAuxiliarySelect", method = {RequestMethod.POST})
    @ApiOperation(value = "查询附属配置信息 select 可选项")
    public List<String> queryAuxiliarySelect(@Validated @RequestBody AuxiliarySelectQueryParam auxiliarySelectQueryParam) {
        return componentAuxiliaryService.queryAuxiliarySelect(auxiliarySelectQueryParam);
    }

    @RequestMapping(value="/addAuxiliaryConfig", method = {RequestMethod.POST})
    @ApiOperation(value = "新增或更新集群的附属配置信息")
    public void addAuxiliaryConfig(@RequestBody ComponentAuxiliaryConfigDTO auxiliaryConfigDTO) {
        componentAuxiliaryService.addAuxiliaryConfig(auxiliaryConfigDTO);
    }

    @RequestMapping(value="/removeAuxiliaryConfig", method = {RequestMethod.POST})
    @ApiOperation(value = "删除集群的knox配置信息")
    public void removeAuxiliaryConfig(@RequestBody ConsoleComponentAuxiliary auxiliary) {
        componentAuxiliaryService.removeAuxiliaryConfig(auxiliary);
    }

    @RequestMapping(value="/switchAuxiliaryConfig", method = {RequestMethod.POST})
    @ApiOperation(value = "开关集群的knox配置信息")
    public void switchAuxiliaryConfig(@RequestBody ConsoleComponentAuxiliary auxiliary) {
        componentAuxiliaryService.switchAuxiliaryConfig(auxiliary);
    }

    public static List<ComponentAuxiliaryVO> tran2VoList(List<ComponentConfig> componentConfigs) {
        if (CollectionUtils.isEmpty(componentConfigs)) {
            return Collections.emptyList();
        }
        return componentConfigs.stream().map(ComponentAuxiliaryController::trans2Vo).collect(Collectors.toList());
    }

    public static ComponentAuxiliaryVO trans2Vo(ComponentConfig componentConfig) {
        if (componentConfig == null) {
            return null;
        }
        ComponentAuxiliaryVO vo = new ComponentAuxiliaryVO();
        vo.setKey(componentConfig.getKey());
        vo.setLabel(componentConfig.getDesc());
        vo.setRequired(componentConfig.getRequired());
        vo.setValue(componentConfig.getValue());
        vo.setFormType(componentConfig.getType());
        return vo;
    }
}