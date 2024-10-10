-- 全局参数支持上季末yyyy-MM-dd格式的参数
INSERT ignore INTO console_param (param_name, param_value, param_desc, param_type, is_deleted, gmt_create, gmt_modified, create_user_id, date_benchmark, date_format, calender_id)
 VALUES
 ('bdp.system.preqrtrend2', '${bdp.system.preqrtrend2}', '上季末yyyy-MM-dd格式，以计划时间为准，距离其最近的上一个季末时间，例：计划时间为 7.1，上季末为 6.30', 0, 0, '2023-06-02 14:54:40', '2023-06-02 14:54:40', '-1', null, null, null),
('bdp.system.preyrend2', '${bdp.system.preyrend2}', '上年末yyyy-MM-dd格式，以计划时间为准，即去年 12.31，例：今年为 2022 年，上年末为 2021.12.31', 0, 0, '2023-06-02 14:54:40', '2023-06-02 14:54:40', '-1', null, null, null),
('bdp.system.qrtrstart', '${bdp.system.qrtrstart}', 'yyyyMMdd格式，季末时间范围：3.31、6.30、9.30、12.31,上季末：以计划时间为准，当前季度的第一天例：计划时间为7.2，本季初就是7.1', 0, 0, '2023-06-02 14:54:40', '2023-06-02 14:54:40', '-1', null, null, null),
('bdp.system.qrtrstart2', '${bdp.system.qrtrstart2}', 'yyyy-MM-dd格式，季末时间范围：3.31、6.30、9.30、12.31,上季末：以计划时间为准，当前季度的第一天例：计划时间为7.2，本季初就是7.1', 0, 0, '2023-06-02 14:54:40', '2023-06-02 14:54:40', '-1', null, null, null);


