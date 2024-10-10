ALTER TABLE console_cluster_tenant ADD COLUMN common_config VARCHAR(256) DEFAULT '{
    "enableDownloadResult":true,
    "downloadLimit":1000000,
    "selectLimit":1000000
}' COMMENT '租户对应集群的通用配置';

