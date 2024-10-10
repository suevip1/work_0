-- chunjun 1.16 产品包路径变更
update `console_component_config` set `value` = '/data/insight_plugin1.16/chunjun_lib_116' where `value` = '/data/insight_plugin1.16/chunjun_lib' and `component_type_code` = 0;
update `console_component_config` set `value` = '/data/insight_plugin1.16/chunjunplugin_116' where `value` = '/data/insight_plugin1.16/chunjunplugin' and `component_type_code` = 0;