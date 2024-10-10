package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.Sort;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.dto.AccountDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.AccountTenantVo;
import com.dtstack.engine.api.vo.AccountVo;
import com.dtstack.engine.api.vo.UicUserVo;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.constrant.PluginInfoConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.Base64Util;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.enums.AccountType;
import com.dtstack.engine.master.mapstruct.UserStruct;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.master.utils.SM2Util;
import com.dtstack.engine.master.worker.DataSourceXOperator;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UserDTO;
import com.dtstack.pubsvc.sdk.usercenter.domain.param.FindUserParam;
import com.dtstack.pubsvc.sdk.usercenter.domain.result.UICUserListResult;
import com.dtstack.schedule.common.enums.DataBaseType;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yuebai
 * @date 2020-02-14
 */
@Service
public class AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private AccountTenantDao accountTenantDao;

    @Autowired
    private UserService userService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ConsoleCache consoleCache;

    @Autowired
    private DataSourceXOperator dataSourceXOperator;

    @Autowired
    private UicUserApiClient uicUserApiClient;

    @Autowired
    private UserStruct userStruct;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    protected ComponentService componentService;

    @Autowired
    private ClusterDao clusterDao;

    /**
     * 绑定数据库账号 到对应数栈账号下的集群
     */
    public void bindAccount(AccountVo accountVo) throws Exception {
        if (null == accountVo) {
            throw new RdosDefineException("The binding parameter cannot be empty");
        }
        // password 可以为空, 底层会将 password 映射成空串 "", 故此处不校验
        if (null == accountVo.getUserId() || null == accountVo.getUsername()
                || null == accountVo.getBindTenantId() || null == accountVo.getBindUserId() || null == accountVo.getName()) {
            throw new RdosDefineException("Please fill in the necessary parameters");
        }
        // 解密密码
        if (!StringUtils.isBlank(accountVo.getPassword())){
            String password = accountVo.getPassword();
            accountVo.setPassword(SM2Util.decrypt(password,environmentContext.getSM2PrivateKey(),environmentContext.getSM2PublicKey()));
        }
        //校验db账号测试连通性
        checkDataSourceConnect(accountVo);
        //绑定账号
        bindAccountTenant(accountVo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindAccountList(List<AccountVo> list) throws Exception {
        for (AccountVo accountVo : list) {
            bindAccount(accountVo);
        }
    }

    private void checkDataSourceConnect(AccountVo accountVo) throws SQLException {
        JSONObject jdbc = null;
        DataBaseType dataBaseType = null;
        if (MultiEngineType.TIDB.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.tiDBInfo(accountVo.getBindTenantId(), null,null, null));
            dataBaseType = DataBaseType.TiDB;
            replacePlaceHolder(jdbc, "/");
        } else if (MultiEngineType.ORACLE.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.oracleInfo(accountVo.getBindTenantId(), null,null, null));
            dataBaseType = DataBaseType.Oracle;
        } else if (MultiEngineType.GREENPLUM.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.greenplumInfo(accountVo.getBindTenantId(), null,null, null));
            dataBaseType = DataBaseType.Greenplum;
        } else if (MultiEngineType.ANALYTICDB_FOR_PG.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.adbPostgrepsqlInfo(accountVo.getBindTenantId(), null,null, null));
            dataBaseType = DataBaseType.adb_Postgrepsql;
        } else if (MultiEngineType.MYSQL.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.mysqlInfo(accountVo.getBindTenantId(), null,null, null));
            dataBaseType = DataBaseType.MySql;
        } else if (MultiEngineType.DB2.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.db2Info(accountVo.getBindTenantId(), null,null, null));
            dataBaseType = DataBaseType.DB2;
        } else if (MultiEngineType.SQL_SERVER.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.sqlServerInfo(accountVo.getBindTenantId(), null,null, null));
            dataBaseType = DataBaseType.SQLServer;
        } else if (MultiEngineType.OCEANBASE.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.oceanBaseInfo(accountVo.getBindTenantId(), null,null, null));
            dataBaseType = DataBaseType.OCEANBASE;
        } else if (MultiEngineType.OUSHUDB.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.oushuDbInfo(accountVo.getBindTenantId(), null,null, null));
            dataBaseType = DataBaseType.OUSHUDB;
        } else if (MultiEngineType.HASHDATA.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.hashDataInfo(accountVo.getBindTenantId(), null,null, null));
            dataBaseType = DataBaseType.HASH_DATA;
        } else if (MultiEngineType.STARROCKS.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.starRocksInfo(accountVo.getBindTenantId(), null,null, null));
            dataBaseType = DataBaseType.STAR_ROCKS;
        } else if (MultiEngineType.TRINO.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.trinoSqlInfo(accountVo.getBindTenantId(), null,null, null));
            dataBaseType = DataBaseType.TRINO;
        } else if (MultiEngineType.HANA.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.hanaInfo(accountVo.getBindTenantId(), null,null, null));
            dataBaseType = DataBaseType.sapHana1;
        } else if (MultiEngineType.LIBRA.getType() == accountVo.getEngineType()) {
            // Gauss engineType
            jdbc = JSONObject.parseObject(clusterService.libraInfo(accountVo.getBindTenantId(), null,null, null));
            dataBaseType = DataBaseType.LIBRA;
        } else if (MultiEngineType.INCEPTOR.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.inceptorSqlInfo(accountVo.getBindTenantId(), null,null));
            dataBaseType = DataBaseType.Inceptor;
        } else if (MultiEngineType.HADOOP.getType() == accountVo.getEngineType()) {
            //如果是HADOOP，则添加ldap,无需校验连通性
            return;
        }

        if (null == jdbc) {
            if (MultiEngineType.TIDB.getType() == accountVo.getEngineType()) {
                throw new RdosDefineException("Please bind TiDB components first");
            } else if (MultiEngineType.ORACLE.getType() == accountVo.getEngineType()) {
                throw new RdosDefineException("Please bind Oracle components first");
            } else if (MultiEngineType.GREENPLUM.getType() == accountVo.getEngineType()) {
                throw new RdosDefineException("Please bind the GREENPLUM component first");
            } else if (MultiEngineType.ANALYTICDB_FOR_PG.getType() == accountVo.getEngineType()) {
                throw new RdosDefineException("Please bind the AnalyticDB for PostgreSQL component first");
            } else if (MultiEngineType.OUSHUDB.getType() == accountVo.getEngineType()) {
                throw new RdosDefineException("Please bind the OushuDB component first");
            } else{
                throw new RdosDefineException("Please bind the corresponding components first, accountVo.getEngineType:" + accountVo.getEngineType());
            }
        }

        JSONObject selfConf = new JSONObject();
        selfConf.put("jdbcUrl", jdbc.getString("jdbcUrl"));
        selfConf.put(GlobalConst.USERNAME, accountVo.getName());
        selfConf.put(GlobalConst.PASS_WORD, accountVo.getPassword());
        selfConf.put(ConfigConstant.TYPE_NAME_KEY, dataBaseType.getTypeName().toLowerCase());
        try {
            JSONObject pluginInfo = new JSONObject();
            pluginInfo.put(PluginInfoConst.SELF_CONF, selfConf);
            testAccountConnect(accountVo);
        } catch (Exception e) {
            throw new RdosDefineException("联通性校验失败, Account:" + accountVo
                    + "\r\n" + ExceptionUtil.getErrorMessage(e));
        }
    }

    /**
     * 校验账号联通性
     *
     * @param accountVo
     */
    private void testAccountConnect(AccountVo accountVo) {
        EComponentType eComponentType = MultiEngineType.engineToComponentType(MultiEngineType.getByType(accountVo.getEngineType()));
        Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(accountVo.getBindTenantId());
        Cluster cluster = clusterDao.getOne(clusterId);
        Component component = componentDao.getByClusterIdAndComponentType(clusterId, eComponentType.getTypeCode(), null, null);
        String pluginType = componentService.convertComponentTypeToClient(cluster.getClusterName(), eComponentType.getTypeCode(), "", null, null);
        JSONObject pluginInfo = pluginInfoManager.buildConsolePluginInfo(component, cluster, null, null, null, accountVo.getBindTenantId(), null);
        JSONObject selfConf = pluginInfo.getJSONObject(PluginInfoConst.SELF_CONF);
        Optional.ofNullable(selfConf).ifPresent(s -> {
            selfConf.put(GlobalConst.USERNAME, accountVo.getName());
            selfConf.put(GlobalConst.PASS_WORD, accountVo.getPassword());
        });
        ComponentTestResult componentTestResult = dataSourceXOperator.testConnect(pluginType, pluginInfo.toJSONString(), clusterId, accountVo.getBindTenantId(), null);
        if (null != componentTestResult && !componentTestResult.getResult()) {
            throw new RdosDefineException(componentTestResult.getErrorMsg());
        }
    }
    /**
     * 替换占位符
     *
     * @param jdbc
     * @param replacement
     */
    private void replacePlaceHolder(JSONObject jdbc, String replacement) {
        Optional.ofNullable(jdbc).ifPresent(jsonObject -> {
            String jdbcUrl = jsonObject.getString("jdbcUrl");
            if (StringUtils.isNotEmpty(jdbcUrl)) {
                jsonObject.put("jdbcUrl", jdbcUrl.replace("/%s", replacement));
            }
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindAccountTenant(AccountVo accountVo) {
        try {
            checkAccountVo(accountVo);
            Account dbAccountByName = insertAccount(accountVo);
            //bindUserId 是从uic获取 需要转换下
            User user = userService.findByUser(accountVo.getBindUserId());
            if (null == user) {
                throw new RdosDefineException("绑定用户不存在");
            }
            AccountTenant dbAccountTenant = accountTenantDao.getByAccount(user.getDtuicUserId(), accountVo.getBindTenantId(), dbAccountByName.getId(), Deleted.NORMAL.getStatus());
            if (null != dbAccountTenant) {
                throw new RdosDefineException("该账号已绑定对应产品账号");
            }
            insertAccountTenant(accountVo, dbAccountByName,user, accountVo.getBindTenantId());
        } catch (Exception e) {
            throw new RdosDefineException("绑定账户租户关系异常",e);
        }
    }

    /**
     * @author newman
     * @Description 插入账户租户关系对象
     * @Date 2020-12-22 11:56
     * @param accountVo:
     * @param dbAccountByName:
     * @param user:
     * @param tenantId:
     * @return: void
     **/
    private void insertAccountTenant(AccountVo accountVo, Account dbAccountByName, User user, Long tenantId) {
        AccountTenant accountTenant = new AccountTenant();
        accountTenant.setDtuicTenantId(tenantId);
        accountTenant.setDtuicUserId(user.getDtuicUserId());
        accountTenant.setAccountId(dbAccountByName.getId());
        accountTenant.setCreateUserId(accountVo.getUserId());
        accountTenant.setModifyUserId(accountVo.getUserId());
        accountTenant.setIsDeleted(Deleted.NORMAL.getStatus());
        accountTenantDao.insert(accountTenant);
        LOGGER.info("bind db account id [{}]username [{}] to user [{}] tenant {}  success ", accountTenant.getAccountId(), dbAccountByName.getName(),
                accountTenant.getDtuicUserId(), tenantId);
    }

    /**
     * @author newman
     * @Description 插入账号信息
     * @Date 2020-12-22 11:53
     * @param accountVo:
     * @return: com.dtstack.engine.api.domain.Account
     **/
    private Account insertAccount(AccountVo accountVo) {
        Account dbAccountByName = new Account();
        dbAccountByName.setName(accountVo.getName());
        dbAccountByName.setPassword(StringUtils.isBlank(accountVo.getPassword()) ? "" : Base64Util.baseEncode(accountVo.getPassword()));
        dbAccountByName.setType(getAccountTypeByMultiEngineType(accountVo.getEngineType()));
        dbAccountByName.setCreateUserId(accountVo.getUserId());
        dbAccountByName.setModifyUserId(accountVo.getUserId());
        accountDao.insert(dbAccountByName);
        LOGGER.info("add db account {} [{}] ", dbAccountByName.getName(), dbAccountByName.getId());
        return dbAccountByName;
    }


    /**
     * 把引擎类型转化为dataSource类型
     * @param multiEngineType
     * @return
     */
    private Integer getAccountTypeByMultiEngineType(Integer multiEngineType) {
        if (null == multiEngineType) {
            return null;
        }
        if (MultiEngineType.TIDB.getType() == multiEngineType) {
            return AccountType.TiDB.getVal();
        } else if (MultiEngineType.ORACLE.getType() == multiEngineType) {
            return AccountType.Oracle.getVal();
        } else if (MultiEngineType.GREENPLUM.getType() == multiEngineType) {
            return AccountType.GREENPLUM6.getVal();
        } else if (MultiEngineType.ANALYTICDB_FOR_PG.getType() == multiEngineType) {
            return  AccountType.ADB_POSTGREPSQL.getVal();
        } else if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            return AccountType.LDAP.getVal();
        } else if (MultiEngineType.OUSHUDB.getType() == multiEngineType) {
            return AccountType.OUSHUDB.getVal();
        } else if (MultiEngineType.TRINO.getType() == multiEngineType) {
            return AccountType.TRINO.getVal();
        }else if (MultiEngineType.LIBRA.getType() == multiEngineType) {
            return AccountType.LIBRA.getVal();
        }else if (MultiEngineType.MYSQL.getType() == multiEngineType) {
            return AccountType.MySQL.getVal();
        }else if (MultiEngineType.SQL_SERVER.getType() == multiEngineType) {
            return AccountType.SQLServer.getVal();
        }else if (MultiEngineType.HANA.getType() == multiEngineType) {
            return AccountType.SAP_HANA1.getVal();
        } else if (MultiEngineType.STARROCKS.getType() == multiEngineType) {
            return AccountType.STARROCKS.getVal();
        } else if (MultiEngineType.HASHDATA.getType() == multiEngineType) {
            return AccountType.HASHDATA.getVal();
        } else if (MultiEngineType.INCEPTOR.getType() == multiEngineType) {
            return AccountType.INCEPTOR_SQL.getVal();
        }
        return 0;
    }

    /**
     * 解绑数据库账号
     */
    @Transactional(rollbackFor = Exception.class)
    public void unbindAccount(AccountTenantVo accountTenantVo) throws Exception {
        if (null == accountTenantVo || null == accountTenantVo.getId()) {
            throw new RdosDefineException("Parameter cannot be empty");
        }
        if (StringUtils.isBlank(accountTenantVo.getName())) {
            throw new RdosDefineException("Unbind account information cannot be empty");
        }
        if (StringUtils.isBlank(accountTenantVo.getPassword())) {
            accountTenantVo.setPassword("");
        } else {
            accountTenantVo.setPassword(SM2Util.decrypt(accountTenantVo.getPassword(), environmentContext.getSM2PrivateKey(), environmentContext.getSM2PublicKey()));
        }
        AccountTenant dbAccountTenant = accountTenantDao.getById(accountTenantVo.getId());
        if (null == dbAccountTenant) {
            throw new RdosDefineException("The account is not bound to the corresponding cluster");
        }
        Account account = accountDao.getById(dbAccountTenant.getAccountId());
        if (null == account) {
            throw new RdosDefineException("Unbind account does not exist");
        }
        if (!account.getName().equals(accountTenantVo.getName())) {
            throw new RdosDefineException("Unbinding failed, please use the database account entered during binding to unbind");
        }
        String oldPassWord = StringUtils.isBlank(account.getPassword()) ? "" : Base64Util.baseDecode(account.getPassword());
        if (!oldPassWord.equals(accountTenantVo.getPassword())) {
            throw new RdosDefineException("Unbind failed, unbind account password is wrong");
        }
        try {
            //标记为删除
            dbAccountTenant.setGmtModified(new Timestamp(System.currentTimeMillis()));
            dbAccountTenant.setIsDeleted(Deleted.DELETED.getStatus());
            dbAccountTenant.setModifyUserId(accountTenantVo.getModifyDtUicUserId());
            accountTenantDao.update(dbAccountTenant);

            account.setGmtModified(new Timestamp(System.currentTimeMillis()));
            account.setIsDeleted(Deleted.DELETED.getStatus());
            account.setModifyUserId(accountTenantVo.getModifyDtUicUserId());
            accountDao.update(account);
            LOGGER.info("unbind db account id [{}] to user [{}] tenant {}  success ", dbAccountTenant.getAccountId(), dbAccountTenant.getDtuicTenantId(), dbAccountTenant.getDtuicTenantId());
            consoleCache.publishRemoveMessage(String.format("%s.%s", dbAccountTenant.getDtuicTenantId(), dbAccountTenant.getDtuicUserId()));
        } catch (Exception e) {
            throw new RdosDefineException("解绑异常", e);
        }
    }


    /**
     * 更改数据库账号
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBindAccount(AccountTenantVo accountTenantVo) throws Exception {
        if (null == accountTenantVo || null == accountTenantVo.getId()) {
            throw new RdosDefineException("Parameter cannot be empty");
        }
        if (StringUtils.isBlank(accountTenantVo.getName())) {
            throw new RdosDefineException("Update account information cannot be empty");
        }
        if (StringUtils.isBlank(accountTenantVo.getPassword())){
            accountTenantVo.setPassword("");
        }else {
            accountTenantVo.setPassword(SM2Util.decrypt(accountTenantVo.getPassword(),environmentContext.getSM2PrivateKey(),environmentContext.getSM2PublicKey()));
        }
        AccountTenant dbAccountTenant = accountTenantDao.getById(accountTenantVo.getId());
        if (null == dbAccountTenant) {
            throw new RdosDefineException("The account is not bound to the corresponding cluster");
        }
        AccountVo accountVO = new AccountVo();
        accountVO.setBindTenantId(dbAccountTenant.getDtuicTenantId());
        accountVO.setName(accountTenantVo.getName());
        accountVO.setPassword(accountTenantVo.getPassword());
        accountVO.setEngineType(accountTenantVo.getEngineType());
        //校验db账号测试连通性
        checkDataSourceConnect(accountVO);
        Account oldAccount = new Account();
        //删除旧账号
        oldAccount.setId(dbAccountTenant.getAccountId());
        oldAccount.setGmtModified(new Timestamp(System.currentTimeMillis()));
        oldAccount.setIsDeleted(Deleted.DELETED.getStatus());
        oldAccount.setModifyUserId(accountTenantVo.getModifyDtUicUserId());
        accountDao.update(oldAccount);
        //添加新账号
        Account newAccount = new Account();
        newAccount.setName(accountVO.getName());
        newAccount.setPassword(Base64Util.baseEncode(accountVO.getPassword()));
        newAccount.setType(getAccountTypeByMultiEngineType(accountTenantVo.getEngineType()));
        newAccount.setCreateUserId(accountTenantVo.getModifyDtUicUserId());
        newAccount.setModifyUserId(accountTenantVo.getModifyDtUicUserId());
        accountDao.insert(newAccount);

        //更新关联关系
        dbAccountTenant.setGmtModified(new Timestamp(System.currentTimeMillis()));
        dbAccountTenant.setAccountId(newAccount.getId());
        dbAccountTenant.setModifyUserId(accountTenantVo.getModifyDtUicUserId());
        accountTenantDao.update(dbAccountTenant);
        LOGGER.info("modify db account id [{}] old account [{}] new account [{}]  success ", dbAccountTenant.getId(), oldAccount.getId(), newAccount.getId());
        consoleCache.publishRemoveMessage(String.format("%s.%s", dbAccountTenant.getDtuicTenantId(), dbAccountTenant.getDtuicUserId()));
    }


    /**
     * 分页查询
     *
     * @param dtuicTenantId
     * @param username
     * @param currentPage
     * @param pageSize
     * @return
     */
    public PageResult<List<AccountVo>> pageQuery(Long dtuicTenantId, String username, Integer currentPage,
                                                 Integer pageSize, Integer engineType, Long dtuicUserId) {
        if (null == dtuicTenantId) {
            throw new RdosDefineException("The binding parameter cannot be empty");
        }
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setTenantId(dtuicTenantId);
        accountDTO.setName(username);
        accountDTO.setDtuicUserId(dtuicUserId);
        accountDTO.setType(getAccountTypeByMultiEngineType(engineType));

        if (StringUtils.isNotBlank(username)) {
            FindUserParam findUserParam = new FindUserParam();
            findUserParam.setUsername(username);
            findUserParam.setSize(pageSize);
            ApiResponse<UserDTO> byUsername = uicUserApiClient.findByUsername(findUserParam);
            if (byUsername != null && null != byUsername.getData()) {
                accountDTO.setDtuicUserId(byUsername.getData().getId());
            }
        }
        PageQuery<AccountDTO> pageQuery = new PageQuery<>(currentPage, pageSize, "gmt_modified", Sort.DESC.name());
        pageQuery.setModel(accountDTO);
        Integer count = accountTenantDao.generalCount(accountDTO);
        if (0 >= count) {
            return new PageResult<>(null, 0, pageQuery);
        }
        List<AccountDTO> accountDTOS = accountTenantDao.generalQuery(pageQuery);
        List<AccountVo> data = new ArrayList<>(accountDTOS.size());
        Set<Long> userIds = accountDTOS.stream()
                .flatMap(dto -> Stream.of(dto.getDtuicUserId(), dto.getModifyUserId()))
                .collect(Collectors.toSet());
        List<User> userList = userService.findUserWithFill(userIds);
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getDtuicUserId, Function.identity()));
        for (AccountDTO dto : accountDTOS) {
            AccountVo accountVo = new AccountVo(dto);
            User user = userMap.get(dto.getDtuicUserId());
            User modifyUser = userMap.get(dto.getModifyUserId());
            accountVo.setUsername(user == null ? "" : user.getUserName());
            accountVo.setModifyUserName(modifyUser == null ? "" : modifyUser.getUserName());
            data.add(accountVo);
        }
        return new PageResult<>(data, count, pageQuery);
    }

    /**
     * 获取租户未绑定用户列表
     *
     * @param dtuicTenantId
     * @return
     */
    public List<UicUserVo> getTenantUnBandList(Long dtuicTenantId, Integer engineType) {
        if (null == dtuicTenantId) {
            throw new RdosDefineException("Please select the corresponding tenant");
        }
        //获取uic下该租户所有用户
        ApiResponse<List<UICUserListResult>> allUsers = uicUserApiClient.findAllUsers(dtuicTenantId, "RDOS");
        List<UICUserListResult> allUsersList = allUsers.getData();

        if (CollectionUtils.isEmpty(allUsersList)) {
            return new ArrayList<>(0);
        }

        List<AccountDTO> tenantUser = accountTenantDao.getTenantUser(dtuicTenantId, getAccountTypeByMultiEngineType(engineType));
        List<Long> userInIds;
        if (CollectionUtils.isNotEmpty(tenantUser)) {
            userInIds = tenantUser.stream().map(AccountDTO::getDtuicUserId).collect(Collectors.toList());
        } else {
            userInIds = new ArrayList<>();
        }
        List<UicUserVo> uicUserVos = userStruct.toUicUserVO(allUsersList);

        // 由于admin超管账号需要在用户中心添加上面的findAllUsers才会有，但是admin是默认和所有租户具有绑定关系，该关系没在数据库表中体现出来，因此这儿需要加上
        List<UICUserVO>  allAdminUsersList = uicUserApiClient.getAllAdmin().getData();
        if (CollectionUtils.isNotEmpty(allAdminUsersList)) {
            Optional<UICUserVO> optionalAdminUser = allAdminUsersList.stream().filter(e -> e.getUserName().equals("admin@dtstack.com")).findFirst();
            if (optionalAdminUser.isPresent()) {
                UICUserVO adminUser = optionalAdminUser.get();
                UicUserVo adminUicUser = userStruct.toUicUserVo(adminUser);
                adminUicUser.setRoot(adminUser.getRoot());
                List<Long> userIds = uicUserVos.stream().map(UicUserVo::getUserId).collect(Collectors.toList());
                // 不包含admin管理员账号，就添加
                if (!userIds.contains(adminUicUser.getUserId())) {
                    uicUserVos.add(adminUicUser);
                }
            }
        }

        //过滤租户下已绑定的用户
        return uicUserVos.stream()
                .filter((uicUser) -> !userInIds.contains(uicUser.getUserId()))
                .collect(Collectors.toList());

    }

    private void checkAccountVo(AccountVo accountVo) {

        Integer accountType = getAccountTypeByMultiEngineType(accountVo.getEngineType());
        if (accountType != AccountType.LDAP.getVal()) {
            return;
        }
        //检查ldap 同一个租户下一个ldap name 只能被一个账号绑定
        //检查同租户下用户是否已被绑定
        Account one = accountDao.getOne(accountVo.getBindTenantId(), accountVo.getBindUserId(), accountType, null);
        if ( null != one ) {
            throw new RdosDefineException("User "+ accountVo.getUsername() + "is bound");
        }
        //检查同租户下用户名是否被绑定
        Account exit = accountDao.getOne(accountVo.getBindTenantId(), null, accountType, accountVo.getName());
        if ( null != exit ) {
            throw new RdosDefineException("User "+ accountVo.getName() + "is bound");
        }

    }

    public void deleteUser(Long userId) {
        accountTenantDao.deleteByUserId(userId);
    }
}
