DELETE FROM console_param WHERE param_name IN ('${bdp.system.preqrtrend}', '${bdp.system.preyrend}');
INSERT INTO console_param (param_name, param_value, param_desc, param_type, is_deleted, gmt_create, gmt_modified, create_user_id) VALUES ('bdp.system.preqrtrend', '${bdp.system.preqrtrend}', '上季末，以计划时间为准，距离其最近的上一个季末时间，例：计划时间为 7.1，上季末为 6.30', 0, 0, now(),now(), -1);
INSERT INTO console_param (param_name, param_value, param_desc, param_type, is_deleted, gmt_create, gmt_modified, create_user_id) VALUES ('bdp.system.preyrend', '${bdp.system.preyrend}', '上年末，以计划时间为准，即去年 12.31，例：今年为 2022 年，上年末为 2021.12.31', 0, 0, now(),now(), -1);
