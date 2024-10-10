UPDATE `schedule_dict` SET `dict_value` = '{\"deleteDateConfig\":30,\"clearDateConfig\":180,\"appendWhere\":\"and compute_type = 1\"}' WHERE
        dict_code = 'data_clear_name' and dict_name = 'schedule_job';

DELETE FROM `schedule_dict` WHERE dict_code = 'data_clear_name' and dict_name = 'console_security_log';
