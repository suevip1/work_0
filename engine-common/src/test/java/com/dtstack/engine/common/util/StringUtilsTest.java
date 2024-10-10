package com.dtstack.engine.common.util;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void split() {
        String[] split = StringUtils.split("sdsd-sdsd_sds", "[a-z0-9]([-a-z0-9]*[a-z0-9])?");
        Assert.assertNotNull(split);
    }

    @Test
    public void removeDuplicate() {
        String[] strings = StringUtils.removeDuplicate(new String[]{"1", "1", "1"});
        Assert.assertTrue(strings.length == 1);
    }

    @Test
    public void isEmpty() {
        Assert.assertTrue(StringUtils.isEmpty(""));
        Assert.assertFalse(StringUtils.isEmpty("notEmpty"));
    }

    @Test
    public void isNotEmpty() {
        Assert.assertFalse(StringUtils.isNotEmpty(""));
        Assert.assertFalse(StringUtils.isNotEmpty(null));
    }

    @Test
    public void isBlank() {
        Assert.assertTrue(StringUtils.isBlank("   "));
        Assert.assertFalse(StringUtils.isBlank("sds"));
    }

    @Test
    public void isNotBlank() {
        Assert.assertFalse(StringUtils.isNotBlank("   "));
        Assert.assertTrue(StringUtils.isNotBlank("sds"));
    }


    @Test
    public void toSnakeCase() {
       Assert.assertEquals("sd_bc_d",StringUtils.toSnakeCase("sdBcD"));
    }

    @Test
    public void indexOfNot() {
        Assert.assertEquals(1,StringUtils.indexOfNot('c', "abc", 1, 3));
    }


    @Test
    public void testIndexOfNot() {
        Assert.assertEquals(0,StringUtils.indexOfNot('c',"abc"));
    }

    @Test
    public void testIndexOfNot1() {
        Assert.assertEquals(1,StringUtils.indexOfNot('c',new char[]{'a','b','c'},1,3));
    }

    @Test
    public void indexOf() {
        Assert.assertEquals(2,StringUtils.indexOf('c', new char[]{'a', 'b', 'c'}, 1, 3));
    }

    @Test
    public void format() {
        Assert.assertEquals("sdsde",StringUtils.format("sds{}","de"));
    }

    @Test
    public void equalsOnContent() {
        Assert.assertFalse(StringUtils.equalsOnContent("dsdsdsd","sdsd"));
        Assert.assertTrue(StringUtils.equalsOnContent("dsdsdsd","dsdsdsd"));
    }

    @Test
    public void equalsLiterally() {
        Assert.assertTrue(StringUtils.equalsLiterally(null,null));
    }

    @Test
    public void trim() {
        Assert.assertEquals("sdsd",StringUtils.trim("  sdsd"));
    }
}