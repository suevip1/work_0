package com.dtstack.engine.master.utils;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class FileUtilTest {

    @Test
    public void getContentFromFile() throws FileNotFoundException {
        String contentFromFile = FileUtil.getContentFromFile(this.getClass().getClassLoader().getResource("csv/calendar.csv").getPath());
    }

    @Test
    public void getUploadFileName() {
        FileUtil.getUploadFileName(1,"sdsds");
    }

    @Test
    public void mkdirsIfNotExist() {
        String path = this.getClass().getClassLoader().getResource("csv").getPath() + "/test";
        FileUtil.mkdirsIfNotExist(path);
        new File(path).delete();
    }
}