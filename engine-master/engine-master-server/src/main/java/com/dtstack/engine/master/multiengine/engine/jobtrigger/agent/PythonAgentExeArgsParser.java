package com.dtstack.engine.master.multiengine.engine.jobtrigger.agent;

import com.dtstack.engine.common.util.Base64Util;
import com.dtstack.engine.common.util.StringUtils;

import static org.apache.commons.lang3.StringUtils.SPACE;

/**
 * @author leon
 * @date 2023-04-17 17:54
 **/
public class PythonAgentExeArgsParser {

    /**
     * 解析 python agent 的执行参数
     * @param exeArgs --files hdfs://tmp/xxx.py --python-version 3.6 --cmd-opts xxx
     * @return AgentExeArgs
     */
    public static AgentExeArgs parse(String exeArgs) {
        AgentExeArgs agentExeArgs = new AgentExeArgs();
        if (StringUtils.isBlank(exeArgs)) {
            return agentExeArgs;
        }
        String[] args = exeArgs.split(SPACE);
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--files":
                    agentExeArgs.setFiles(args[i + 1]);
                    break;
                case "--python-version":
                    agentExeArgs.setPythonVersion(args[i + 1]);
                    break;
                case "--cmd-opts":
                    // base64 解一下
                    String decodeCmd = Base64Util.baseDecode(args[i + 1]);
                    agentExeArgs.setCmdOpt(decodeCmd);
                    break;
            }
        }
        return agentExeArgs;
    }
}
