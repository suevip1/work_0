package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.util.Xml2JsonUtil;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.mockcontainer.impl.ScheduleDictServiceMock;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-04 19:15
 */
@MockWith(ScheduleDictServiceMock.class)
public class ScheduleDictServiceTest {
    ScheduleDictService sdService = new ScheduleDictService();

    @Test
    public void getVersion() {
        System.out.println(sdService.getVersion(-1L));
    }

    @Test
    public void loadExtraComponentConfig() {
        sdService.loadExtraComponentConfig("", EComponentType.YARN.getTypeCode());
    }

    @Test
    public void getByNameAndValue() {
        sdService.getByNameAndValue(null, null, null, null);
    }

    @Test
    public void convertVersionNameToValue() {
        sdService.convertVersionNameToValue("1.10", "flink");
    }

    @Test
    public void listById() {
        sdService.listById(1L, 1);
    }

    @Test
    public void listByDictType() {
        sdService.listByDictType(DictType.TASK_CLIENT_TYPE);
    }

    @Test
    public void operatorValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("k", "v");
        sdService.operatorValue(map, Function.identity());
    }

    @Test
    public void encryptValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("k", "v");
        sdService.encryptValue(map);
    }

    @Test
    public void findTipGroup() {
        sdService.findTipGroup(EComponentType.YARN.getTypeCode());
    }

    @Test
    public void testAddConfigToXml() {
        URL resource = this.getClass().getResource("/hadoopConf/hive-site.xml");
        File file = new File(resource.getFile());
        try {
            Xml2JsonUtil.xml2map(file);
        } catch (Exception e) {
            Assert.fail();
        }
        Map<String, Object> extraConfig = new HashMap<>();
        extraConfig.put("test1", "test1");
        extraConfig.put("test2", "test2");
        extraConfig.put("test3", "test3");
        extraConfig.put("test4", "test4");
        try {
            Xml2JsonUtil.operatorXml(file, (root, xmlKeys) -> {
                Xml2JsonUtil.appendPropertyNode(root, extraConfig);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}