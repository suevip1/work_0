-- 离线告警支持动态添加 http://zenpms.dtstack.cn/zentao/story-view-8330.html
ALTER TABLE alert_alarm ADD COLUMN `scope` tinyint(1) DEFAULT NULL COMMENT '告警范围 1：选择  2：所有，3: 类目';