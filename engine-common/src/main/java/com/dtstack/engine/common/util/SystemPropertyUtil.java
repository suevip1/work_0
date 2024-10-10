
package com.dtstack.engine.common.util;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;


/**
 *
 * @author sishu.yss
 *
 */
public class SystemPropertyUtil {

    public static void setSystemUserDir(String[] args) {
        String dir = System.getProperty("user.dir");
        String conf = String.format("%s/%s", new Object[]{dir, "conf"});
        File file = new File(conf);
        if(!file.exists()) {
            dir = dir.substring(0, dir.lastIndexOf("/"));
            conf = String.format("%s/%s", new Object[]{dir, "conf"});
            file = new File(conf);
            if(file.exists()) {
                System.setProperty("user.dir", dir);
            }
        }
        System.setProperty("user.dir.conf", System.getProperty("user.dir") + "/conf");

        if (ArrayUtils.isNotEmpty(args)) {
            for (String arg : args) {
                try {
                    String[] split = arg.split("=");
                    if (split.length >= 2) {
                        System.setProperty(split[0], split[1]);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    public static void setHadoopUserName(String userName) {
        System.setProperty("HADOOP_USER_NAME", userName);
    }
}
