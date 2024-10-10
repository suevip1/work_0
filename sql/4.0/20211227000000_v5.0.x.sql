UPDATE task_param_template SET `params` = "# 当前支持TensorFlow和Pytorch框架的单机训练和分布式训练，所有训练均需要指定运行的计算资源大小，
# 具体包括CPU、内存、GPU显卡数量、节点数（即是实例数，注意：仅支持在分布式训练中设置实例数）
# 注意：
# 		(1)Keras请使用TensorFlow中的Keras;
# 		(2)请合理设置计算资源大小，请提前联系管理员获知集群当前计算资源大小。
# 			超过集群所有的计算资源将导致任务无法正常启动，过大的计算资源将导致
# 			资源浪费且其他用户无法正常使用，管理员可能会kill掉相关消耗大量资源的任务。
# 	   （3）CPU和内存推荐设置为1:2或者1:4





# 详细说明及参考设置如下，使用时请用户详细阅读帮助文档或者官方指导手册.
# 单机训练：默认使用am也就是application master进行训练，
# 开启下面的参数设置使用am进行单机训练
tony.am.memory = 4g
tony.am.vcores = 1
# tony.am.gpus = 0

# 注意：当前不能使用一个worker进行单机训练，出现worker的任务类型那么一定为分布式训练



# TensorFlow的分布式训练较为复杂，共支持5种分布式训练策略，
# 详细请参考官网(https://www.tensorflow.org/guide/distributed_training?hl=zh-cn#%E7%AD%96%E7%95%A5%E7%B1%BB%E5%9E%8B)
# 训练 API    MirroredStrategy	  TPUStrategy 	 MultiWorkerMirroredStrategy	CentralStorageStrategy  ParameterServerStrategy
# Keras API	            支持	                支持	               实验性支持	                                    实验性支持	                        计划于 2.3 后支持
# 自定义训练循环	        支持	                支持	               实验性支持	                                    实验性支持	                        计划于 2.3 后支持
# Estimator API	    有限支持	        不支持	            有限支持	                                    有限支持	                        有限支持
# 所有的策略在tony中，当且仅当使用ParameterServerStrategy策略时，tony必须开启ps、worker或者ps、chief、worker任务类型，
# 其中chief为可选参数，但ps、worker为必选，且ps不可单独设置
# 开启下面的参数设置进行ParameterServerStrategy策略的分布式训练
# tony.ps.instances = 1
# tony.ps.memory = 2g
# tony.ps.vcores = 1
# tony.ps.gpus = 0
# chief的实例数仅能设置为1
# tony.chief.instances = 1
# tony.chief.memory = 4g
# tony.chief.vcores = 2
# tony.chief.gpus = 0
# tony.worker.instances = 2
# tony.worker.memory = 8g
# tony.worker.vcores = 2
# tony.worker.gpus = 0

# 当使用非ParameterServerStrategy策略时，参数中不可出现ps相关的设置，chief为可选参数，但worker为必选
# 开启下面的参数设置进行非ParameterServerStrategy策略的分布式训练

# chief的实例数仅能设置为1
# tony.chief.instances = 1
# tony.chief.memory = 4g
# tony.chief.vcores = 2
# tony.chief.gpus = 0
# tony.worker.instances = 2
# tony.worker.memory = 8g
# tony.worker.vcores = 2
# tony.worker.gpus = 0




# Pytorch的分布式训练（详细说明参考官方文档https://pytorch.org/docs/1.3.1/distributed.html），类似于TensorFlow框架非ParameterServerStrategy策略的训练，
# 不能出现ps相关的设置，chief为可选参数，但worker为必选
# 开启下面的参数设置进行Pytorch的分布式训练
# tony.application.framework = pytorch
# chief的实例数仅能设置为1
# tony.chief.instances = 1
# tony.chief.memory = 4g
# tony.chief.vcores = 2
# tony.chief.gpus = 0
# tony.worker.instances = 2
# tony.worker.memory = 8g
# tony.worker.vcores = 2
# tony.worker.gpus = 0
" WHERE `compute_type` = 1 AND `engine_type` = 31 AND `task_type` = 0;

INSERT INTO `console_component_config` ( `cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
( -2, -119, 27, 'INPUT', 0, 'tony.java.home', '/usr/java/jdk1.8.0_181', NULL, NULL, NULL, "tony组件jdk路径", NOW(), NOW(), 0);
INSERT INTO `console_component_config` ( `cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
( -2, -121, 27, 'INPUT', 0, 'tony.java.home', '/usr/java/jdk1.8.0_181', NULL, NULL, NULL, "tony组件jdk路径", NOW(), NOW(), 0);
