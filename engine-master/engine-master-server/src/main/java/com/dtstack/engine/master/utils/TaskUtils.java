package com.dtstack.engine.master.utils;

import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DtStringUtil;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * @Auther: dazhi
 * @Date: 2021/10/27 10:48 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class TaskUtils {


    public static Long dissectJobKey(String jobKey) {
        String[] jobKeySplit = jobKey.split("_");

        if (jobKeySplit.length < 3) {
            return null;
        }

        String taskIdStr = jobKeySplit[jobKeySplit.length - 2];
        return MathUtil.getLongVal(taskIdStr);
    }

    public static Pair<Long, String> dissectJobKeyReturnTaskIdAndCycTime(String jobKey) {
        String[] jobKeySplit = jobKey.split("_");

        if (jobKeySplit.length < 3) {
            return null;
        }

        String taskIdStr = jobKeySplit[jobKeySplit.length - 2];

        if (StringUtils.isBlank(taskIdStr)) {
            return null;
        }

        String cycTime = jobKeySplit[jobKeySplit.length - 1];

        if (StringUtils.isBlank(cycTime)) {
            return null;
        }

        Long longVal = null;
        try {
            longVal = MathUtil.getLongVal(taskIdStr);
        } catch (Exception e) {
            return null;
        }

        return new ImmutablePair<>(longVal,cycTime);
    }



    public static List<Long> dissectJobKeys(Collection<String> jobKeys) {
        List<Long> ids = Lists.newArrayList();
        for (String jobKey : jobKeys) {
            Long id = dissectJobKey(jobKey);
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }

    /**
     * 获取任务的 extraInfo
     * @param extObject
     * @return
     */
    public static JSONObject getActualTaskExtraInfo(JSONObject extObject) {
        if (Objects.isNull(extObject)) {
            return null;
        }
        // 兼容历史 scheduleTaskShade.extraInfo/ScheduleTaskCommit.extraInfo 字段外层嵌套一层 info 的情况
        if (extObject.containsKey(TaskConstant.INFO)) {
            return extObject.getJSONObject(TaskConstant.INFO);
        }
        return extObject;
    }

   public static String parseTaskArgs(String args,String key) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(args)) {
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
        List<String> argsList = DtStringUtil.splitIngoreBlank(args);

        for(int i = 0; i < argsList.size() - 1; ++i) {
            if (key.equals(argsList.get(i))) {
                return argsList.get(i + 1);
            }
        }

        return org.apache.commons.lang3.StringUtils.EMPTY;
    }

    /**
     * extraInfo --> extraInfo json --> taskParamsToReplace str --> List<ScheduleTaskParamShade>
     * @param extraInfo
     * @return
     */
    public static List<ScheduleTaskParamShade> parseTaskParamsShade(String extraInfo) {
        if (StringUtils.isEmpty(extraInfo)) {
            return Collections.emptyList();
        }
        JSONObject infoJson = JSONObject.parseObject(extraInfo);
        return parseScheduleTaskParamShades(infoJson);
    }

    /**
     * extraInfo json --> taskParamsToReplace str --> List<ScheduleTaskParamShade>
     * @param infoJson
     * @return
     */
    public static List<ScheduleTaskParamShade> parseScheduleTaskParamShades(JSONObject infoJson) {
        if (infoJson == null) {
            return Collections.emptyList();
        }
        String taskParamsToReplaceStr = infoJson.getString(TaskConstant.TASK_PARAMS_TO_REPLACE);
        return parseTaskParamsToReplace(taskParamsToReplaceStr);
    }

    /**
     * taskParamsToReplace str --> List<ScheduleTaskParamShade>
     * @param taskParamsToReplaceStr
     * @return
     */
    public static List<ScheduleTaskParamShade> parseTaskParamsToReplace(String taskParamsToReplaceStr) {
        if (StringUtils.isEmpty(taskParamsToReplaceStr)) {
            return Collections.emptyList();
        }
        List<ScheduleTaskParamShade> taskParamShades = JSONObject.parseArray(taskParamsToReplaceStr, ScheduleTaskParamShade.class);
        if (CollectionUtils.isEmpty(taskParamShades)) {
            return Collections.emptyList();
        }
        return taskParamShades;
    }

    /**
     * 校验参数不重复，防止上层应用传值错误
     * @param taskParamShades
     */
    public static void checkUniqueTaskParamShades(List<ScheduleTaskParamShade> taskParamShades) {
        if (CollectionUtils.isEmpty(taskParamShades)) {
            return;
        }
        Set<String> params = new HashSet<>(taskParamShades.size());
        for (ScheduleTaskParamShade taskParamShade : taskParamShades) {
            if (!params.add(taskParamShade.getParamName())) {
                throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
            }
        }
    }
}
