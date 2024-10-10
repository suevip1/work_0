package com.dtstack.engine.master.controller.sdk;

import com.dtstack.dtcenter.common.enums.Sort;
import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.api.dto.ConsoleParamBO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.CalenderParam;
import com.dtstack.engine.api.vo.console.param.ConsoleCalenderTimeShowVO;
import com.dtstack.engine.api.vo.console.param.ConsoleParamVO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.impl.ParamService;
import com.dtstack.engine.master.mapstruct.ParamStruct;
import com.dtstack.schedule.common.enums.EParamType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2022-03-09
 * sdk接口
 */
@RestController
@RequestMapping({"/node/sdk/param"})
public class ParamSdkController {

    @Autowired
    private ParamStruct paramStruct;

    @Autowired
    private ParamService paramService;

    @PostMapping(value = "/getAllParam")
    public PageResult<List<ConsoleParamVO>> getAllParam(@RequestBody CalenderParam calenderParam) {
        PageQuery pageQuery = new PageQuery(calenderParam.getCurrentPage(), calenderParam.getPageSize(), "gmt_modified", Sort.DESC.name());
        List<ConsoleParam> consoleParams = paramService.pageQuery(pageQuery);
        int total = paramService.generalCount();
        if (CollectionUtils.isNotEmpty(consoleParams)) {
            List<ConsoleParamVO> consoleCalenderVOS = paramStruct.toVos(consoleParams);
            consoleCalenderVOS.forEach(a -> {
                // 返回给离线，用于展示的数据
                a.setShowParamType(a.getParamType());
                // 兼容历史数据，旧字段仍然保持不变
                a.setParamType(EParamType.GLOBAL.getType());
            });
            return new PageResult<>(consoleCalenderVOS, total, pageQuery);
        }
        return new PageResult<>(new ArrayList<>(), total, pageQuery);
    }

    @PostMapping(value = "/queryByName")
    public ConsoleParamVO queryByName(@RequestBody ConsoleParam consoleParam) {
        if (StringUtils.isBlank(consoleParam.getParamName())) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        ConsoleParam param = paramService.selectByName(consoleParam.getParamName());

        ConsoleParamVO paramVO = paramStruct.toVo(param);
        if (paramVO != null) {
            // 返回给离线，用于展示的数据
            paramVO.setShowParamType(param.getParamType());
            // 兼容历史数据，旧字段仍然保持不变
            paramVO.setParamType(EParamType.GLOBAL.getType());
        }
        return paramVO;
    }

    @PostMapping(value = "/queryTaskParam")
    public List<ConsoleParamVO> queryTaskParam(@RequestBody ConsoleParam consoleParam) {
        if (consoleParam.getTaskId() == null) {
            return new ArrayList<>(0);
        }
        if (consoleParam.getAppType() <= 0L) {
            throw new RdosDefineException(ErrorCode.APP_TYPE_NOT_NULL);
        }
        List<ConsoleParamBO> params = paramService.selectByTaskId(consoleParam.getTaskId(), consoleParam.getAppType());
        List<ConsoleParamVO> voList = params.stream().map(p -> {
            ConsoleParamVO vo = paramStruct.toVo(p);
            // 返回给离线，用于展示的数据
            vo.setShowParamType(p.getParamType());
            // 兼容历史数据，旧字段仍然保持不变
            vo.setParamType(EParamType.GLOBAL.getType());

            vo.setOffset(p.getOffset());
            vo.setReplaceTarget(p.getReplaceTarget());
            return vo;
        }).collect(Collectors.toList());
        return voList;
    }

    @PostMapping(value = "/findById")
    public ConsoleParamVO findById(@RequestBody ConsoleParam consoleParam) {
        if (consoleParam == null || consoleParam.getParamId() == null || consoleParam.getParamId() <= 0L) {
            return null;
        }
        ConsoleParam param = paramService.findById(consoleParam.getParamId());

        ConsoleParamVO vo = paramStruct.toVo(param);
        if (vo != null) {
            // 返回给离线，用于展示的数据
            vo.setShowParamType(param.getParamType());
            // 兼容历史数据，旧字段仍然保持不变
            vo.setParamType(EParamType.GLOBAL.getType());
        }
        return vo;
    }

    @PostMapping(value = "/showCalenderTime")
    public ConsoleCalenderTimeShowVO showCalenderTime(@RequestBody ConsoleParam consoleParam) {
        if (consoleParam == null || consoleParam.getParamId() == null || consoleParam.getParamId() <= 0L) {
            return null;
        }
        return paramService.getTimeByCalenderId(consoleParam.getParamId());
    }
}
