package com.dtstack.engine.master.multiengine.engine.jobtrigger.agent;

/**
 * @author leon
 * @date 2023-04-17 17:52
 **/
public class AgentExeArgs {

    private String files;

    private String pythonVersion;

    private String cmdOpt;

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }

    public String getPythonVersion() {
        return pythonVersion;
    }

    public void setPythonVersion(String pythonVersion) {
        this.pythonVersion = pythonVersion;
    }

    public String getCmdOpt() {
        return cmdOpt;
    }

    public void setCmdOpt(String cmdOpt) {
        this.cmdOpt = cmdOpt;
    }
}
