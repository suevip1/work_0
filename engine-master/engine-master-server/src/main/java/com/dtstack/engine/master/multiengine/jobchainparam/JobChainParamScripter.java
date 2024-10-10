package com.dtstack.engine.master.multiengine.jobchainparam;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-10 11:30
 */
public class JobChainParamScripter {

    public static String generateSparkSql(String paramCommand, String paramValue) {
        return String.format(";output directory '%s' stored as orc %s ", paramValue, paramCommand);
    }

    /**
     * 组装 shell 脚本: echo v1 > filePath
     * @param paramCommand
     * @param fileName
     * @return
     */
    public static String generateShellScript(String paramCommand, String fileName) {
        return String.format(";%s > %s", paramCommand, fileName);
    }

    /**
     * 组装 shellOnAgent 脚本: echo v1'※k1'
     * @param paramCommand
     * @param fileName
     * @return
     */
    public static String generateShellOnAgentScript(String paramCommand, String fileName) {
        return String.format(";%s'※%s'", paramCommand, fileName);
    }

    public static String generatePythonScript(String paramCommand, String fileName) {
        return String.format("\nimport sys"
                + "\noutput_stream=open('%s', 'w')"
                + "\nsys.stdout=output_stream"
                + "\n%s"
                + "\noutput_stream.close()", fileName, paramCommand);
    }

    public static String generateHiveSql(String paramCommand, String tableName, String paramValue) {
        return String.format(";create temporary table %s as %s ;"
                + "insert overwrite directory '%s' stored as orc select * from %s", tableName, paramCommand, paramValue, tableName);
    }

    public static String generateOutputTaskParams(String outputFiles, String outputParentDir) {
        return String.format("\n internal.application.outputfiles=%s"
                + "\n internal.application.output.parentdir=%s", outputFiles, outputParentDir);
    }
}
