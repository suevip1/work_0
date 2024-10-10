-- 全局参数支持上季末yyyy-MM-dd格式的参数
INSERT ignore INTO console_param (param_name, param_value, param_desc, param_type, is_deleted, gmt_create, gmt_modified,
                                  create_user_id, date_benchmark, date_format, calender_id)
VALUES ('bdp.system.preqrtrstart', '${bdp.system.preqrtrstart}',
        'yyyyMMdd格式 季初时间范围：1.1、4.1、7.1、10.1 上季初：以计划时间为准，距离其最近的上一个季初时间 例：计划时间为5.5，上季初为1.1', 0, 0, now(),
        now(), '-1', null, null, null),
       ('bdp.system.preqrtrstart2', '${bdp.system.preqrtrstart2}',
        'yyyy-MM-dd格式 季初时间范围：1.1、4.1、7.1、10.1 上季初：以计划时间为准，距离其最近的上一个季初时间 例：计划时间为5.5，上季初为1.1', 0, 0, now(),
        now(), '-1', null, null, null),
       ('bdp.system.premonthstart', '${bdp.system.premonthstart}',
        'yyyyMMdd格式，以计划时间为准，当前月份-1的月份的1号 例：今天为2.2，上月初为1.1', 0, 0, now(),
        now(), '-1', null, null, null),
       ('bdp.system.premonthstart2', '${bdp.system.premonthstart2}',
        'yyyy-MM-dd格式，以计划时间为准，当前月份-1的月份的1号 例：今天为2.2，上月初为1.1', 0, 0, now(),
        now(), '-1', null, null, null),
       ('bdp.system.premonthend', '${bdp.system.premonthend}',
        'yyyyMMdd格式，以计划时间为准，当前月份-1的月份的最后一天 例：今天为2.2，上月初为1.31', 0, 0, now(),
        now(), '-1', null, null, null),
       ('bdp.system.premonthend2', '${bdp.system.premonthend2}',
        'yyyy-MM-dd格式，以计划时间为准，当前月份-1的月份的最后一天 例：今天为2.2，上月初为1.31', 0, 0, now(),
        now(), '-1', null, null, null),
       ('bdp.system.preyrstart', '${bdp.system.preyrstart}',
        'yyyyMMdd格式，以计划时间为准，今年-1的1.1 例：今年为2023年，上年初为2022.1.1', 0, 0, now(),
        now(), '-1', null, null, null),
       ('bdp.system.preyrstart2', '${bdp.system.preyrstart2}',
        'yyyy-MM-dd格式，以计划时间为准，今年-1的1.1 例：今年为2023年，上年初为2022.1.1', 0, 0, now(),
        now(), '-1', null, null, null)
;


