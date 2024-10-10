CREATE TABLE `console_ssl`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT,
    `cluster_id`        bigint(11) NOT NULL COMMENT '集群id',
    `component_type`    int(11)      DEFAULT NULL COMMENT '组件类型',
    `component_version` varchar(25)  DEFAULT NULL COMMENT '组件版本',
    `remote_path`       varchar(200) NOT NULL COMMENT 'sftp存储路径',
    `truststore`        varchar(100) NOT NULL COMMENT 'truststore文件名称',
    `ssl_client`        varchar(100) NOT NULL COMMENT 'ssl client配置文件名称',
    `md5`               varchar(63)  NOT NULL COMMENT 'truststore,ssl_client.xml文件md5',
    `gmt_create`        datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`        tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table console_component add column ssl_file_name varchar(255) DEFAULT NULL COMMENT 'ssl文件名称';

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, `type`, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES('ssl_template', 'ssl_client', '<configuration>

  <!-- Client certificate Store -->
  <property>
    <name>ssl.client.keystore.type</name>
    <value>jks</value>
    <desc>keystore 类型，默认为 jks</desc>
  </property>
  <property>
    <name>ssl.client.keystore.location</name>
    <value>${user.home}/keystores/client-keystore.jks</value>
    <desc>keystore 储存路径，双向认证时，服务端需要验证客户端需要该参数，单向认证不需要该参数</desc>
  </property>
  <property>
    <name>ssl.client.keystore.password</name>
    <value>clientfoo</value>
    <desc>keystore 密码，双向认证时，服务端需要验证客户端需要该参数，单向认证不需要该参数</desc>
  </property>

  <!-- Client Trust Store -->
  <property>
    <name>ssl.client.truststore.type</name>
    <value>jks</value>
    <desc></desc>
  </property>
  <property>
    <name>ssl.client.truststore.location</name>
    <value>truststore.jks</value>
    <desc>truststore 储存路径,相对路径</desc>
  </property>
  <property>
    <name>ssl.client.truststore.password</name>
    <value>clientserverbar</value>
    <desc>truststore 密码</desc>
  </property>
  <property>
    <name>ssl.client.truststore.reload.interval</name>
    <value>10000</value>
    <desc>truststore 刷新时间</desc>
  </property>
</configuration>', NULL, 11, 0, 'STRING', '', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);