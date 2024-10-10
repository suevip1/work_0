package com.dtstack.engine.master.config;

import java.util.Properties;

public class GitContextConfig {

    /**
     * properties对象
     */
    private static Properties prop;

    /**
     * git版本
     */
    private static String GIT_BRANCH;

    static {
        prop = new Properties();
        try {
            prop.load(GitContextConfig.class.getClassLoader().getResourceAsStream("git.properties"));
        } catch (Exception e) {
        }
        GIT_BRANCH = prop.getProperty("git.branch", "dev");
    }

    public static final String getGitBranch() {
        return GIT_BRANCH;
    }
}
