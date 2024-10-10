package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.AlertChannel;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.master.impl.ComponentConfigService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.worker.EnginePluginsOperator;

import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-25 19:15
 */
public class AlertChannelServiceMock extends BaseMock {


    @MockInvoke(targetClass = ComponentConfigDao.class)
    List<ComponentConfig> listByComponentId(Long componentId, boolean isFilter) {
        return ComponentConfigServiceMock.mockSftpComponentConfigs(-1L);
    }

    @MockInvoke(targetClass = ComponentConfigService.class)
    public void addOrUpdateComponentConfig(List<ClientTemplate> clientTemplates, Long componentId, Long clusterId, Integer componentTypeCode) {
        return;
    }

    @MockInvoke(targetClass = EnginePluginsOperator.class)
    public ComponentTestResult testConnect(String engineType, String pluginInfo, Long clusterId, Long tenantId) {
        return new ComponentTestResult();
    }

    public static List<AlertChannel> mockAlertChannels() {
        List<AlertChannel> alertChannels = JSONArray.parseArray("[\n" +
                "      {\n" +
                "        \"clusterId\": 0,\n" +
                "        \"alertId\": 79,\n" +
                "        \"isDefault\": 1,\n" +
                "        \"gmtCreated\": 1609989320000,\n" +
                "        \"gmtModified\": 1632386221000,\n" +
                "        \"alertGateName\": \"默认钉钉通道\",\n" +
                "        \"alertGateSource\": \"def_ding\",\n" +
                "        \"alertGateType\": 3,\n" +
                "        \"alertTemplate\": null,\n" +
                "        \"isDeleted\": 0,\n" +
                "        \"alertGateTypes\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"clusterId\": 0,\n" +
                "        \"alertId\": 81,\n" +
                "        \"isDefault\": 1,\n" +
                "        \"gmtCreated\": 1609989414000,\n" +
                "        \"gmtModified\": 1622427439000,\n" +
                "        \"alertGateName\": \"默认邮箱通道\",\n" +
                "        \"alertGateSource\": \"def_mail\",\n" +
                "        \"alertGateType\": 2,\n" +
                "        \"alertTemplate\": \"测试邮箱内789容：${message}\",\n" +
                "        \"isDeleted\": 0,\n" +
                "        \"alertGateTypes\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"clusterId\": 0,\n" +
                "        \"alertId\": 97,\n" +
                "        \"isDefault\": 1,\n" +
                "        \"gmtCreated\": 1614241045000,\n" +
                "        \"gmtModified\": 1655103889000,\n" +
                "        \"alertGateName\": \"zy测试短信通道\",\n" +
                "        \"alertGateSource\": \"zy_test_mail\",\n" +
                "        \"alertGateType\": 1,\n" +
                "        \"alertTemplate\": null,\n" +
                "        \"isDeleted\": 0,\n" +
                "        \"alertGateTypes\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"clusterId\": 0,\n" +
                "        \"alertId\": 107,\n" +
                "        \"isDefault\": 0,\n" +
                "        \"gmtCreated\": 1622425561000,\n" +
                "        \"gmtModified\": 1655103889000,\n" +
                "        \"alertGateName\": \"大智冒烟\",\n" +
                "        \"alertGateSource\": \"dz_sms\",\n" +
                "        \"alertGateType\": 1,\n" +
                "        \"alertTemplate\": null,\n" +
                "        \"isDeleted\": 0,\n" +
                "        \"alertGateTypes\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"clusterId\": 0,\n" +
                "        \"alertId\": 111,\n" +
                "        \"isDefault\": 0,\n" +
                "        \"gmtCreated\": 1622426214000,\n" +
                "        \"gmtModified\": 1622427438000,\n" +
                "        \"alertGateName\": \"大智冒烟-邮件-扩展\",\n" +
                "        \"alertGateSource\": \"dz_mail_k\",\n" +
                "        \"alertGateType\": 2,\n" +
                "        \"alertTemplate\": null,\n" +
                "        \"isDeleted\": 0,\n" +
                "        \"alertGateTypes\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"clusterId\": 0,\n" +
                "        \"alertId\": 115,\n" +
                "        \"isDefault\": 0,\n" +
                "        \"gmtCreated\": 1622427799000,\n" +
                "        \"gmtModified\": 1632386220000,\n" +
                "        \"alertGateName\": \"大智冒烟-钉钉\",\n" +
                "        \"alertGateSource\": \"dz_dd_k\",\n" +
                "        \"alertGateType\": 3,\n" +
                "        \"alertTemplate\": null,\n" +
                "        \"isDeleted\": 0,\n" +
                "        \"alertGateTypes\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"clusterId\": 0,\n" +
                "        \"alertId\": 121,\n" +
                "        \"isDefault\": 0,\n" +
                "        \"gmtCreated\": 1634093734000,\n" +
                "        \"gmtModified\": 1634093734000,\n" +
                "        \"alertGateName\": \"test_fenghe\",\n" +
                "        \"alertGateSource\": \"test_fenghe\",\n" +
                "        \"alertGateType\": 1,\n" +
                "        \"alertTemplate\": null,\n" +
                "        \"isDeleted\": 0,\n" +
                "        \"alertGateTypes\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"clusterId\": 0,\n" +
                "        \"alertId\": 123,\n" +
                "        \"isDefault\": 0,\n" +
                "        \"gmtCreated\": 1634610483000,\n" +
                "        \"gmtModified\": 1634610483000,\n" +
                "        \"alertGateName\": \"1234\",\n" +
                "        \"alertGateSource\": \"1234\",\n" +
                "        \"alertGateType\": 3,\n" +
                "        \"alertTemplate\": null,\n" +
                "        \"isDeleted\": 0,\n" +
                "        \"alertGateTypes\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"clusterId\": 0,\n" +
                "        \"alertId\": 667,\n" +
                "        \"isDefault\": 0,\n" +
                "        \"gmtCreated\": 1635151851000,\n" +
                "        \"gmtModified\": 1635151851000,\n" +
                "        \"alertGateName\": \"axy\",\n" +
                "        \"alertGateSource\": \"axy\",\n" +
                "        \"alertGateType\": 3,\n" +
                "        \"alertTemplate\": null,\n" +
                "        \"isDeleted\": 0,\n" +
                "        \"alertGateTypes\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"clusterId\": 0,\n" +
                "        \"alertId\": 671,\n" +
                "        \"isDefault\": 0,\n" +
                "        \"gmtCreated\": 1635302249000,\n" +
                "        \"gmtModified\": 1635302249000,\n" +
                "        \"alertGateName\": \"银联云DGC告警通道\",\n" +
                "        \"alertGateSource\": \"unionPay_alarmChanel\",\n" +
                "        \"alertGateType\": 4,\n" +
                "        \"alertTemplate\": \"<企业名称>${messge}，请及时处理\",\n" +
                "        \"isDeleted\": 0,\n" +
                "        \"alertGateTypes\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"clusterId\": 0,\n" +
                "        \"alertId\": 681,\n" +
                "        \"isDefault\": 0,\n" +
                "        \"gmtCreated\": 1641885302000,\n" +
                "        \"gmtModified\": 1641885302000,\n" +
                "        \"alertGateName\": \"微信\",\n" +
                "        \"alertGateSource\": \"iiii\",\n" +
                "        \"alertGateType\": 4,\n" +
                "        \"alertTemplate\": \"<袋鼠云>${message}，请及时处理\",\n" +
                "        \"isDeleted\": 0,\n" +
                "        \"alertGateTypes\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"clusterId\": 0,\n" +
                "        \"alertId\": 685,\n" +
                "        \"isDefault\": 0,\n" +
                "        \"gmtCreated\": 1642591648000,\n" +
                "        \"gmtModified\": 1642591648000,\n" +
                "        \"alertGateName\": \"温大短信\",\n" +
                "        \"alertGateSource\": \"alert_wd_message\",\n" +
                "        \"alertGateType\": 4,\n" +
                "        \"alertTemplate\": \"22\",\n" +
                "        \"isDeleted\": 0,\n" +
                "        \"alertGateTypes\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"clusterId\": 0,\n" +
                "        \"alertId\": 687,\n" +
                "        \"isDefault\": 0,\n" +
                "        \"gmtCreated\": 1645170836000,\n" +
                "        \"gmtModified\": 1645170836000,\n" +
                "        \"alertGateName\": \"11111111\",\n" +
                "        \"alertGateSource\": \"111111\",\n" +
                "        \"alertGateType\": 3,\n" +
                "        \"alertTemplate\": null,\n" +
                "        \"isDeleted\": 0,\n" +
                "        \"alertGateTypes\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"clusterId\": 0,\n" +
                "        \"alertId\": 689,\n" +
                "        \"isDefault\": 0,\n" +
                "        \"gmtCreated\": 1645171204000,\n" +
                "        \"gmtModified\": 1645171204000,\n" +
                "        \"alertGateName\": \"qwqwq\",\n" +
                "        \"alertGateSource\": \"qwww\",\n" +
                "        \"alertGateType\": 4,\n" +
                "        \"alertTemplate\": \"wq\",\n" +
                "        \"isDeleted\": 0,\n" +
                "        \"alertGateTypes\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"clusterId\": 0,\n" +
                "        \"alertId\": 691,\n" +
                "        \"isDefault\": 0,\n" +
                "        \"gmtCreated\": 1645514027000,\n" +
                "        \"gmtModified\": 1645514027000,\n" +
                "        \"alertGateName\": \"dada\",\n" +
                "        \"alertGateSource\": \"dada\",\n" +
                "        \"alertGateType\": 1,\n" +
                "        \"alertTemplate\": null,\n" +
                "        \"isDeleted\": 0,\n" +
                "        \"alertGateTypes\": null\n" +
                "      }\n" +
                "    ]", AlertChannel.class);
        return alertChannels;
    }
}
