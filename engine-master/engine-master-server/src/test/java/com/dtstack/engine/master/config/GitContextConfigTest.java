package com.dtstack.engine.master.config;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class GitContextConfigTest {

    @Test
    public void getGitBranch() {
        Assert.assertEquals("dev",GitContextConfig.getGitBranch());
    }
}