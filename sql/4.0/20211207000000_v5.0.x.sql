UPDATE task_param_template SET `params` = "# 实例个数
tony.am.instances = 1

# 内存大小，字符串格式
tony.am.memory = 2g

# 核数
tony.am.vcores = 1

# gpu个数
tony.am.gpus = 0

# 实例个数
tony.ps.instances = 0

# 内存大小，字符串格式
tony.ps.memory = 2g

# 核数
tony.ps.vcores = 1

# gpu个数
tony.ps.gpus = 0

# worker实例个数
tony.worker.instances = 0

# 内存大小，字符串格式
tony.worker.memory = 2g

# 核数
tony.worker.vcores = 1

# gpu个数
tony.worker.gpus = 0" WHERE `compute_type` = 1 AND `engine_type` = 31 AND `task_type` = 0;