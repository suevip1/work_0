package com.dtstack.engine.common.exception;

import java.io.Serializable;

/**
 * @author yuebai
 * @date 2021-09-07
 */
public enum ErrorCode implements ExceptionEnums, Serializable {


    /**
     * 0 ~ 100 常用错误code
     * 100 ~ 200 运维中心错误code
     * 200 ~ 300 控制台配置错误code
     * 300 ~ 400 任务提交错误code
     */
    NOT_LOGIN(0, "not login", "未登录"),
    SUCCESS(1, "success", ""),
    //权限不足 前端页面会进入404
    PERMISSION_LIMIT(3, "permission limit", "权限不足"),
    TOKEN_IS_NULL(4, "dt_token is null", "token信息为空"),
    USER_IS_NULL(5, "user is null", "用户不存在"),
    TOKEN_IS_INVALID(6, "dt_token is invalid", "无效token"),
    NOT_SUPPORT(7, "not support", "暂不支持"),

    UNKNOWN_ERROR(10, "unknown error", "未知错误"),
    SERVER_EXCEPTION(11, "server exception", "服务异常"),


    EMPTY_PARAMETERS(12, "empty parameters", "必填参数为空"),
    INVALID_PARAMETERS(13, "invalid parameters", "非法参数"),
    NAME_ALREADY_EXIST(14, "name already exist","名称已存在"),
    DATA_NOT_FIND(17, "data not exist","数据不存在"),
    INVALID_TASK_STATUS(20, "invalid task status","无效的task"),
    INVALID_TASK_RUN_MODE(21, "invalid task run mod","无效的运行模式"),
    JOB_CACHE_NOT_EXIST(22, "job cache not exist this job","任务不存在cache"),
    JOB_STATUS_IS_SAME(23, "job status is same as cluster","任务状态与集群一致"),
    APP_TYPE_NOT_NULL(24, "appType can not be null","子产品类型不能为空"),
    TASK_TYPE_NOT_NULL(25, "taskType can not be null","任务类型不能为空"),
    FUNCTION_CAN_NOT_FIND(28, "function can not found","方法不存在"),

    UPDATE_EXCEPTION(30, "update exception", "更新异常"),
    INSERT_EXCEPTION(31, "insert exception", "插入异常"),
    QUERY_EXCEPTION(32, "query exception", "查询异常"),

    HTTP_CALL_ERROR(64, "http call error", "远程调用失败"),
    HTTP_CALL_RETURN_NULL(65, "http call return null", "远程调用返回数据为空"),

    INVALID_APP_TYPE(100, "appType invalid","无效的appType"),
    INVALID_UIC_TENANT_ID(101, "dtuicTenantId invalid","无效的dtuicTenantId"),
    INVALID_PAGE_PARAM(102, "page params invalid","无效的分页数据"),
    TENANT_ID_NOT_NULL(103, "dtuicTenantId cat not be null","租户id不能为空"),
    COMPONENT_TYPE_CODE_NOT_NULL(104, "component type code cat not be null","组件code不能为空"),
    STORE_COMPONENT_CONFIG_NULL(105, "storage component is null","存储组件配置为空"),
    RESOURCE_COMPONENT_CONFIG_NULL(106, "resource component is null","资源组件配置为空"),
    RESOURCE_NOT_SUPPORT_COMPONENT_VERSION(107, "resource component {} not support {} version {}","资源组件 {} 不支持 {} 版本 {}"),
    COMPONENT_CONFIG_NOT_SUPPORT_VERSION(108, "component {} config not support {} version {}","组件 {} 配置不支持版本 {}"),
    NOT_SUPPORT_COMPONENT(109, "not support component {} version {} ,pluginName is empty","不支持组件 {} 版本 {}，找不到插件名称"),
    DEPEND_ON_COMPONENT_NOT_CONFIG(110, "depend_on_component_not_config","依赖组件未配置"),
    RESOURCE_COMPONENT_NOT_SUPPORT_DEPLOY_TYPE(111, "resource component {} not support deployType {}","资源组件不支持部署版本 {}"),
    COMPONENT_INVALID(112, "component_invalid","组件不支持"),
    CLUSTER_ID_EMPTY(113, "cluster id is empty","集群id为空"),
    RESOURCE_NOT_SUPPORT_STORAGE_COMPUTE_COMPONENT_VERSION(114, "resource component {} not support storage: {} version {},compute: {} version {}","资源组件 {} 不支持存储组件: {} 版本 {}，计算组件: {} 版本 {}"),
    PROJECT_ID_CAN_NOT_NULL(115, "projectId can not be null","项目 id 不能为空"),
    TASK_IS_RELOADING(116, "The current task is undergoing a hot update operation. Please wait for the hot update to end before performing the operation","当前任务正在进行热更新操作，请等待热更新结束后再执行操作"),


    RESOURCE_COMPONENT_NOT_CONFIG(200, "please config resource component", "请先配置调度组件"),
    STORE_COMPONENT_NOT_CONFIG(201, "please config store component", "请先配置存储组件"),
    S3_STORE_COMPONENT_NOT_CONFIG(202, "Please configure s3 storage components first", "请先配置 S3 存储组件"),
    UNSUPPORTED_PLUGIN(203, "unsupported plugin", "插件不支持"),
    STORE_COMPONENT_NOT_CHOOSE(204, "Please choose storeType first", "请先选择存储组件"),
    SFTP_SERVER_NOT_CONFIG(205, "Please configure the sftp server to upload files", "请先配置SFTP公共组件"),
    FILE_NOT_UPLOAD(206, "Please upload file", "请上传文件"),
    FILE_NOT_ZIP(207, "upload files are not in zip format", "上传文件须为zip格式"),
    FILE_MISS(208, "The necessary file is missing", "必备文件缺失"),
    FILE_DECOMPRESSION_EMPTY(209, "file decompression empty", "文件解压数量为空"),

    FILE_STORE_FAIL(210, "An error occurred while storing the file", "文件存储异常"),
    FILE_SUFFIX_EMPTY(211, "File suffix empty", "文件名称后缀为空"),

    FILE_DECOMPRESSION_ERROR(212, "file decompression error", "文件解压异常"),
    FILE_COUNT_ERROR(213, "file count wrong", "文件数量不正确"),
    XML_FILE_MISS(214, "xml file is missing", "xml 文件缺失"),
    FILE_PARSE_ERROR(215, "file parse error", "文件解析异常"),
    S3_MATCH_K8S(216, "S3 must match kubernetes component.", "S3 需匹配 kubernetes 组件"),
    FILE_NOT_FOUND(217, "file not found", "文件不存在"),
    FILE_CONTENT_FOUND(218, "file content is empty", "文件内容为空"),
    FILE_SUFFIX_ERROR(219, "File suffix error", "不支持的文件名称后缀"),


    KEYTAB_FILE_PARSE_ERROR(220, "Failed to parse keytab file", "keytab 文件解析失败"),
    KEYTAB_FILE_NOT_CONTAINS_PRINCIPAL(221, "keytab file does not contain principal information", "keytab 文件缺失 principal 信息"),
    KERBEROS_REMOVE_ERROR(222, "remove kerberos error", "移除kerberos配置异常"),
    NOT_OPEN_SSL_WHEN_KERBEROS(223, "not open ssl when open Kerberos", "未开启SSL，开启Kerberos的同时必须开启SSL"),
    NOT_OPEN_KERBEROS_WHEN_SSL(224, "not open Kerberos when open ssl", "未开启Kerberos，开启SSL的同时必须开启Kerberos"),
    KEYTAB_FILE_NOT_FOUND(225, "cannot find keytab file in the zip file of the uploaded Hadoop-Kerberos file", "keytab 文件不存在"),
    CHANGE_META_NOT_PERMIT_WHEN_BIND_CLUSTER(226, "cluster has bind tenant can not change metadata component", "集群已经绑定过租户,不允许修改"),
    MERGE_KRB5_ERROR(227, "merge krb5 error", "合并 Kerberos 文件异常"),
    SSL_REMOVE_ERROR(228, "remove ssl error", "移除ssl配置异常"),

    COMPONENT_NOT_EXISTS(230, "Component does not exist", "组件不存在"),
    PRE_COMPONENT_NOT_EXISTS(231, "pre Component does not exist", "前置组件不存在"),
    COMPONENT_COUNT_ERROR(232, "Component count wrong", "组件数量不正确"),
    COMPONENT_UPDATE_ERROR(233, "Component update error", "组件更新异常"),

    CONFIG_COMPONENT_ERROR(240, "Component config error", "组件配置异常"),
    CONFIG_COMPONENT_NOT_FOUND(241, "Component config empty", "组件配置为空"),
    CONFIG_CONTENT_ILLEGAL(242, "Illegal config content", "非法配置内容"),
    CONFIG_FILE_START_WITH_BRACE(243, "Config file should not start with \"{\"", "配置文件不应以\"{\"开头"),

    CAN_NOT_FIND_TASK(250, "task can not found","该任务不存在"),
    TASK_TYPE_ERROR(251, "task type is wrong", "任务类型异常"),
    NO_FILLDATA_TASK_IS_GENERATE(256, "can not build fill data","没有补数据任务生成"),
    CAN_NOT_FIND_JOB(258, "job can not found","任务实例不存在"),
    JOB_CAN_NOT_STOP(259, "job can not stop","该任务处于不可停止状态"),
    JOB_ID_CAN_NOT_EMPTY(260, "job id can not empty","jobId不能为空"),

    JOB_QUERY_DAYS_TOO_LONG(658, "job query days too long", "为防止内存溢出，实例信息筛选范围为%s个月。当前筛选范围超过%s个月，请缩小筛选范围。"),
    JOB_QUERY_DAYS_EMPTY(659, "job query days empty", "请限制查询业务日期范围"),
    JOB_QUERY_JOB_ID_TOO_MANY(660, "job query jobIds too many", "查询任务实例数过多"),

    TENANT_ALREADY_BIND(261, "tenant has been bound", "租户已绑定"),
    TENANT_NOT_EXIST(262, "Tenant not found", "租户不存在"),

    CHECK_CONNECT_FAIL(270, "check connect fail", "测试联通性失败"),
    CHECK_CONNECT_NOT_PASS(270, "check connect not pass", "测试联通性不通过"),
    CHECK_CONNECT_COMPONENT(271, "component name:", "组件："),

    QUEUE_HAS_SUB_QUEUE(280, "The selected queue has sub-queues, and the correct sub-queues are selected", "选择的队列存在子队列"),
    QUEUE_NOT_EXIST(281, "Queue does not exist", "队列不存在"),
    QUEUE_SWITCH_FAIL(282, "Failed to switch queue", "切换队列失败"),
    DATA_REPEAT(283,"%s data is repeat ","%s 数据重复"),
    DATA_LIMIT(284,"data is limit  ","超过数据限制"),
    CALENDER_FORMAT_ERROR(285,"calender format  %s error  ","存在调度日期%s不符合yyyyMMddhhmm格式，请检查源文件"),
    CALENDER_IS_USING(286,"calender is using ","调度日历已被任务使用，无法删除"),
    CALENDER_IS_NULL(287,"calender is not found ","调度日历为空"),
    CALENDER_EXPAND_TIME_IS_NULL(288,"expandTime is empty ","自定义调度时间不能为空"),

    ROLE_SIZE_LIMIT(300, "role size limit","超过管理员用户限制"),
    USER_NOT_ADMIN(301, "user not admin","当前操作用户不是管理员"),
    APPLICATION_CAT_NOT_EMPTY(302, "application can not be empty","application不能为空"),
    APPLICATION_NOT_FOUND(303, "application not found on yarn","获取不到application信息"),
    APPLICATION_NOT_MATCH(304, "application not match on yarn","application和当前任务jobId不匹配"),
    GET_APPLICATION_INFO_ERROR(305, "get application information error","获取applicationId信息错误"),



    RESOURCE_GROUP_NOT_FOUND(350,"resource group not  exist","资源组不存在"),
    CAN_NOT_CHANGE_RESOURCE(351,"source resource group not belong the same cluster","目标资源组不在当前租户对应集群下"),
    RESOURCE_GROUP_NOT_GRANTED(352,"resource group not granted","资源组未授权"),


    CLUSTER_ALREADY_EXISTS(650, "Cluster name already exists", "集群已存在"),
    CANT_NOT_FIND_CLUSTER(651, "cluster can not found","集群不存在"),
    CONSOLE_FILE_SYNC_CLUSTER_ILLEGAL(400, "cluster illegal", "集群配置信息异常，请核实后重试"),
    CONSOLE_FILE_SYNC_CLUSTER_ALREADY_EXISTS(401, "cluster of file sync already exists", "该集群文件同步配置已存在"),
    CONSOLE_FILE_SYNC_MUST_FILE_EMPTY(402, "please choose necessary file", "请勾选必选文件"),
    CONSOLE_FILE_SYNC_FILE_DIR_NOT_FOUND(403, "not found file dir", "不存在该文件目录"),
    CONSOLE_FILE_SYNC_FILE_NOT_FOUND(404, "not found file, please refresh later", "配置文件不存在，请重新加载目录"),
    CONSOLE_FILE_SYNC_FILE_DIR_EMPTY(405, "please choose file dir", "请选择文件目录"),
    CONSOLE_FILE_SYNC_FILE_EMPTY(406, "please choose file", "请选择配置文件"),
    CONSOLE_FILE_SYNC_CHOSEN_FILE_EMPTY(407, "please choose at least one sync file", "请至少勾选一个需同步的配置文件"),
    CONSOLE_FILE_SYNC_FILE_OUT_RANGE(408, "file out of range, please check and try again", "文件不在限定范围，请核实后重试"),
    CONSOLE_FILE_SYNC_DIR_ERROR(409, "directory config error", "文件路径配置异常"),

    CONSOLE_KERBEROS_OR_SSL_MUTEX_WHEN_HIVE_SERVER(400, "kerberos or ssl should be only one when HiveServer", "HiveServer 组件，Kerberos 文件和 SSL 文件具备互斥性"),

    SQLPARSE_ERROR(652, "sql parse error", "sql解析失败"),
    BIND_COMPONENT_NOT_DELETED(653, "component can not deleted","集群已绑定租户，对应组件不能删除"),
    METADATA_COMPONENT_NOT_DELETED(654, "metadata component can not deleted","集群已绑定租户，对应元数据不能删除"),
    TENANT_NOT_BIND(655, "tenant not bind any cluster","租户未绑定集群"),
    DEFAULT_RESOURCE_ID_NULL(656, "default resource id is null","默认资源组为空"),

    AUXILIARY_TYPE_NOT_EXISTS(800,"auxiliary config type does not exist","附属配置类型不存在"),
    COMPONENT_VERSION_SUPPORT(657,"component version support","组件对应版本不存在"),
    ;



    private final int code;
    private final String enMsg;
    private final String zhMsg;

    ErrorCode(int code, String enMsg, String zhMsg) {
        this.code = code;
        this.enMsg = enMsg;
        this.zhMsg = zhMsg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getDescription() {
        return getMsg();
    }

    public String getMsg() {
//        if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(LocaleContextHolder.getLocale().getLanguage())) {
//            return this.zhMsg;
//        } else {
//            return this.enMsg;
//        }
        return this.enMsg;
    }

    public String getEnMsg() {
        return enMsg;
    }

    public String getZhMsg() {
        return zhMsg;
    }
}
