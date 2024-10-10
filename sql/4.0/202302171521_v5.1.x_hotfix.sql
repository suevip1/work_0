-- flink Standalone 参数：metrics.reporter.promgateway.deleteOnShutdown,metrics.reporter.promgateway.randomJobNameSuffix 改成下拉框
-- ref :http://zenpms.dtstack.cn/zentao/bug-view-74638.html
UPDATE console_component_config SET type = 'SELECT' WHERE component_id = -120 AND `key` IN ('metrics.reporter.promgateway.deleteOnShutdown','metrics.reporter.promgateway.randomJobNameSuffix');

