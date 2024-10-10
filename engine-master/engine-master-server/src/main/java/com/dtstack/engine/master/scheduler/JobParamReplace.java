package com.dtstack.engine.master.scheduler;

import com.dtstack.engine.api.dto.ScheduleProjectParamDTO;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.impl.ParamService;
import com.dtstack.engine.master.impl.ScheduleProjectParamService;
import com.dtstack.schedule.common.enums.EParamType;
import com.dtstack.schedule.common.enums.ESystemTimeSpecialParam;
import com.dtstack.schedule.common.util.TimeParamOperator;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 变量替换
 * command--->${}格式的里面的参数需要计算,其他的直接替换
 * Date: 2017/6/6
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Component
public class JobParamReplace {

    private static final Pattern PARAM_PATTERN = Pattern.compile("\\$\\{(.*?)\\}|\\@@\\{(.*?)\\}");

    private final static String VAR_FORMAT = "${%s}";

    private final static String VAR_COMPONENT = "@@{%s}";

    private final static String SP_EL_FORMAT = "#%s";

    @Autowired
    private ParamService paramService;

    @Autowired
    private ScheduleProjectParamService projectParamService;

    public String paramReplace(String sql,
                               List<ScheduleTaskParamShade> paramList,
                               String cycTime, Integer scheduleType,
                               Long projectId) {
        return paramReplace(sql, paramList, cycTime, scheduleType, null, projectId);
    }

    public String paramReplace(String sql,
                               List<ScheduleTaskParamShade> paramList,
                               String cycTime,
                               Integer scheduleType,
                               Timestamp runtime,
                               Long projectId) {
        if (CollectionUtils.isEmpty(paramList)) {
            return sql;
        }

        Matcher matcher = PARAM_PATTERN.matcher(sql);
        if (!matcher.find()) {
            return sql;
        }

        for (ScheduleTaskParamShade param : paramList) {
            Integer type = param.getType();
            if (!canSupport(type)) {
                continue;
            }
            String paramName = param.getParamName();
            String paramCommand = param.getParamCommand();

            String replaceTarget = param.getReplaceTarget();
            String offset = param.getOffset();


            // ${A},${B}
            String parseSymbol = convertSymbol(type);
            String replaceStr;
            if (StringUtils.isNotBlank(replaceTarget)) {
                // 基于全局参数偏移的场景下，实际要替换的文本以应用传递的为准，而不是控制台的参数名称
                replaceStr = String.format(parseSymbol, replaceTarget);
            } else {
                replaceStr = String.format(parseSymbol, paramName);
            }

            // 如果 sql 不包含要替换的参数，就跳过计算参数值
            if (!sql.contains(replaceStr)) {
                continue;
            }

            // 算出A变量的值
            String targetVal = convertParam(type, paramName, paramCommand, cycTime, offset, scheduleType, replaceTarget, runtime, projectId);
            // 替换掉${A}
            sql = sql.replace(replaceStr, targetVal);
        }

        return sql;
    }

    /**
     * 替换 spring el 表达式
     *
     * @param sql       spring el 表达式 json
     * @param paramList 参数集合
     * @param cycTime   执行时间
     * @return 替换后的 text
     */
    public String spElReplace(String sql, List<ScheduleTaskParamShade> paramList, String cycTime, Integer scheduleType, Long projectId) {
        if (CollectionUtils.isEmpty(paramList)) {
            return sql;
        }
        // 兼容原有写法
        sql = paramReplace(sql, paramList, cycTime, scheduleType, projectId);
        for (ScheduleTaskParamShade param : paramList) {
            Integer type = param.getType();
            if (!canSupport(type)) {
                continue;
            }
            if (EParamType.INPUT.getType().equals(param.getType()) || EParamType.OUTPUT.getType().equals(param.getType())) {
                continue;
            }
            String paramName = param.getParamName();
            String paramCommand = param.getParamCommand();
            String replaceTarget = param.getReplaceTarget();
            String offset = param.getOffset();

            // 比较 format 后的名称是否匹配
            String replaceStr;
            if (StringUtils.isNotBlank(replaceTarget)) {
                // 基于全局参数偏移的场景下，实际要替换的文本以应用传递的为准，而不是控制台的参数名称
                replaceStr = String.format(SP_EL_FORMAT, replaceTarget);
            } else {
                replaceStr = String.format(SP_EL_FORMAT, paramName);
            }

            if (!sql.contains(replaceStr)) {
                continue;
            }

            // 算出A变量的值
            String targetVal = convertParam(type, paramName, paramCommand, cycTime, offset, scheduleType, replaceTarget, projectId);
            // 替换掉${A}
            sql = sql.replace(replaceStr, targetVal);
        }
        return sql;
    }

    /**
     * 解析运行参数
     *
     * @param paramList 运行参数集合
     * @param cycTime   执行时间
     * @return 参数名称 -> 参数值 map
     */
    public Map<String, String> parseTaskParam(List<ScheduleTaskParamShade> paramList, String cycTime, Integer scheduleType, Long projectId) {
        if (CollectionUtils.isEmpty(paramList)) {
            return Collections.emptyMap();
        }
        // 输出参数 command
        List<String> outputParamCommands = paramList
                .stream()
                .filter(e -> EParamType.OUTPUT.getType().equals(e.getType()))
                .map(ScheduleTaskParamShade::getParamCommand)
                .collect(Collectors.toList());

        Map<String, String> nameToValue = new HashMap<>(paramList.size());
        for (ScheduleTaskParamShade param : paramList) {
            Integer type = param.getType();
            if (!canSupport(type)) {
                continue;
            }
            String paramName = param.getParamName();
            String paramCommand = param.getParamCommand();
            String replaceTarget = param.getReplaceTarget();
            String offset = param.getOffset();

            // ${A},${B}
            String parseSymbol = convertSymbol(type);
            // 基于全局参数偏移的场景下，实际要替换的文本以应用传递的为准，而不是控制台的参数名称
            replaceTarget = StringUtils.isNotBlank(replaceTarget) ? replaceTarget : paramName;
            String replaceStr = String.format(parseSymbol, replaceTarget); ;


            boolean match = false;

            for (String outputParamCommand : outputParamCommands) {
                // 1.自定义参数 => equals
                // 2.计算结果 => ${replaceTarget}
                if (outputParamCommand.contains(replaceStr) || outputParamCommand.equals(replaceTarget)) {
                    match = true;
                    break;
                }
            }

            // 如果输出参数都不包含要替换的值，就不用去计算实际的值
            if (!match) {
                continue;
            }

            String targetVal = convertParam(type, paramName, paramCommand, cycTime,offset,scheduleType,replaceTarget, projectId);
            nameToValue.put(replaceTarget, targetVal);
        }
        return nameToValue;
    }

    private String convertSymbol(Integer type) {
        if (EParamType.COMPONENT.getType().equals(type)) {
            return VAR_COMPONENT;
        } else {
            return VAR_FORMAT;
        }
    }

    public String convertParam(Integer type,
                               String paramName,
                               String paramCommand,
                               String cycTime) {
        return convertParam(type, paramName, paramCommand, cycTime, null, null,null, null);
    }

    public String convertParam(Integer type,
                               String paramName,
                               String paramCommand,
                               String cycTime,
                               String offsetStr,
                               Integer scheduleType,
                               String replaceTarget,
                               Long projectId) {
        return convertParam(type,paramName,paramCommand,cycTime,offsetStr,scheduleType,replaceTarget,null,projectId);
    }

    public String convertParam(Integer type,
                               String paramName,
                               String paramCommand,
                               String cycTime,
                               String offsetStr,
                               Integer scheduleType,
                               String replaceTarget,
                               Timestamp runtime,
                               Long projectId) {
        String command = paramCommand;

        replaceTarget = StringUtils.isNotBlank(replaceTarget)?replaceTarget:paramName;
        // 偏移量
        TimeParamOperator.Offset offset = TimeParamOperator.Offset.transfer(offsetStr, replaceTarget);

        if (EParamType.SYS_TYPE.getType().equals(type)) {
            // 特殊处理 bdp.system.currenttime
            if (ESystemTimeSpecialParam.containSystemTimeSpecialParam(paramName)) {
                return TimeParamOperator.dealCustomizeTimeOperator(command, cycTime, runtime);
            }
            command = TimeParamOperator.transform(command, cycTime, offset);
            return command;
        }

        if (EParamType.COMPONENT.getType().equals(type)) {
            return command;
        }

        // 自定义参数
        if (EParamType.CUSTOMIZE_TYPE.getType().equals(type)) {
            return TimeParamOperator.dealCustomizeTimeOperator(command, cycTime, offset);
        }

        // 全局参数
        if (EParamType.isGlobalParam(type)) {
            return convertGlobalParam(type, paramName, paramCommand, cycTime, offset, scheduleType);
        }

        // 项目参数
        if (EParamType.PROJECT.getType().equals(type)) {
            // 保证实时性，所以要查 DB
            return convertProjectParam(projectId, paramName);
        }

        // 工作流参数
        if (EParamType.WORK_FLOW.getType().equals(type)) {
            return command;
        }

        throw new RdosDefineException("convertParam not support,type:" + type);
    }

    private String convertProjectParam(Long projectId, String paramName) {
        if (projectId == null || projectId.equals(0L)) {
            throw new RdosDefineException(String.format("convertProjectParam, projectId should not be null, paramName:%s", paramName));
        }
        ScheduleProjectParamDTO projectParamDTO = projectParamService.findByProjectIdAndParamName(projectId, paramName);
        if (projectParamDTO == null) {
            throw new RdosDefineException(String.format("项目参数 %s 不存在，项目 id:%s", paramName, projectId));
        }
        return projectParamDTO.getParamValue();
    }

    private String convertGlobalParam(Integer type,
                                      String paramName,
                                      String command,
                                      String cycTime,
                                      TimeParamOperator.Offset offset,
                                      Integer scheduleType) {


        String result = "";

        if (EParamType.GLOBAL_PARAM_CONST.getType().equals(type)) {
            result = TimeParamOperator.dealCustomizeTimeOperator(command, cycTime, offset);
        }
        if (EParamType.GLOBAL_PARAM_BASE_TIME.getType().equals(type)) {
            result =  paramService.dealCustomizeDate(paramName, command, cycTime, offset);
        }
        if (EParamType.GLOBAL_PARAM_BASE_CYCTIME.getType().equals(type)) {
            result =  paramService.dealCycTimeGlobalParam(paramName, cycTime, offset, scheduleType);
        }

        return result;
    }


    public boolean canSupport(Integer type) {
        return EParamType.SYS_TYPE.getType().equals(type)
                || EParamType.COMPONENT.getType().equals(type)
                || EParamType.CUSTOMIZE_TYPE.getType().equals(type)
                || EParamType.GLOBAL_PARAM_BASE_TIME.getType().equals(type)
                || EParamType.GLOBAL_PARAM_CONST.getType().equals(type)
                || EParamType.GLOBAL_PARAM_BASE_CYCTIME.getType().equals(type)
                || EParamType.GLOBAL.getType().equals(type)
                || EParamType.PROJECT.getType().equals(type)
                || EParamType.WORK_FLOW.getType().equals(type)
                ;
    }


}
