-- TDH 5.2.x
update schedule_dict set dict_value = '{
    "HDFS":{
    	"FLINK":[
            {
                "1.8":"-1002"
            },
            {
                "1.10":"-1002"
            },
            {
                "1.12":"-1013"
            },
            {
                "1.16":"-1013"
            }
        ],
        "SPARK":[
            {
                "2.1":"-1001",
                "2.4":"-1014",
                "3.2":"-1014"
            }
        ],
        "DT_SCRIPT":"-1016"
    }
}' where dict_name = 'TDH 5.2.x' and dict_code  = 'extra_version_template';
-- TDH 6.x
update schedule_dict set dict_value = '{
    "HDFS":{
    	"FLINK":[
            {
                "1.8":"-1002"
            },
            {
                "1.10":"-1002"
            },
            {
                "1.12":"-1013"
            },
            {
                "1.16":"-1013"
            }
        ],
        "SPARK":[
            {
                "2.1":"-1001",
                "2.4":"-1014",
                "3.2":"-1014"
            }
        ],
        "DT_SCRIPT":"-1016"
    }
}' where dict_name = 'TDH 6.x' and dict_code  = 'extra_version_template';
-- TDH 7.x
update schedule_dict set dict_value = '{
    "HDFS":{
    	"FLINK":[
            {
                "1.8":"-1002"
            },
            {
                "1.10":"-1002"
            },
            {
                "1.12":"-1013"
            },
            {
                "1.16":"-1013"
            }
        ],
        "SPARK":[
            {
                "2.1":"-1001",
                "2.4":"-1014",
                "3.2":"-1014"
            }
        ],
        "DT_SCRIPT":"-1016"
    }
}' where dict_name = 'TDH 7.x' and dict_code  = 'extra_version_template';

-- HW MRS 3.x
update schedule_dict set dict_value = '{
    "HDFS":{
    	"FLINK":[
            {
                "1.12":"-1015"
            },
            {
                "1.16":"-1015"
            },
        ],
        "SPARK":[
            {
                "2.1":"-1004",
                "2.4":"-1004",
                "3.2":"-1004"
            }
        ]
    }
}' where dict_name = 'HW MRS 3.x' and dict_code  = 'extra_version_template';

-- HW HD6.x
update schedule_dict set dict_value = '{
    "HDFS":{
    	"FLINK":[
            {
                "1.12":"-1015"
            }
        ],
        "SPARK":[
            {
                "2.1":"-1004",
                "2.4":"-1004",
                "3.2":"-1004"
            }
        ]
    }
}' where dict_name = 'HW HD6.x' and dict_code  = 'extra_version_template';

