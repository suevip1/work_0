INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
VALUES (-2, -102, 0, 'INPUT', 1, 'clusterMode', 'perjob', null, 'deploymode$perjob', null, null, now(),
        now(), 0);
