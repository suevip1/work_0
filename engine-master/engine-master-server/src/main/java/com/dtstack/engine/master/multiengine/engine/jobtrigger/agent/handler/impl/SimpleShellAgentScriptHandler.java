package com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.impl;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.util.Base64Util;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.AgentHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 简单的 shell on agent 处理
 * @author leon
 * @date 2023-04-19 11:11
 **/
@Component
public class SimpleShellAgentScriptHandler extends AbstractAgentScriptHandler {

    @Override
    String generateFinalScript(AgentHandlerContext context) {
        String sqlText = context.getSqlText();
        ScheduleTaskShade taskShade = context.getTaskShade();
        ScheduleJob scheduleJob = context.getScheduleJob();


        if (StringUtils.isBlank(sqlText)) {
            return sqlText;
        }
        // 针对数据资产应用替换
        if (AppType.METADATA.getType().equals(taskShade.getAppType())) {
            // 原始 sql 形如「sh /opt/dtstack/DTDatasourceX/DatasourceX/script/datasourcex_launcher.sh start metadata_collect base64Str」
            String[] commands = sqlText.split("\\s+");
            String originJson = commands[commands.length - 1];
            String afterJson = Base64Util.baseDecode(originJson).replace(TaskConstant.JOB_ID, scheduleJob.getJobId());
            return sqlText.replace(originJson, Base64Util.baseEncode(afterJson));
        }

        context.setRealUserScript(sqlText);
        return sqlText;
    }
}
