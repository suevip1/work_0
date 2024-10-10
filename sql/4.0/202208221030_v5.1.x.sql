CREATE TABLE `console_tenant_component`
(
    `id`                  int(11)     NOT NULL AUTO_INCREMENT,
    `tenant_id`           int(11)     NOT NULL COMMENT '租户id',
    `cluster_id`          int(11)     NOT NULL COMMENT '集群id',
    `component_name`      varchar(24) NOT NULL COMMENT '组件名称',
    `component_type_code` tinyint(1)  NOT NULL COMMENT '组件类型',
    `component_config`    text        NOT NULL COMMENT '组件配置',
    `gmt_create`          datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`        datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`          tinyint(1)  NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `index_component` (`tenant_id`,`cluster_id`, `component_type_code`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8 COMMENT='租户组件配置';