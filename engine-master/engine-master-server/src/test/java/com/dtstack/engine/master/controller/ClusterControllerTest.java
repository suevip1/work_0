package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.enums.DbType;
import com.dtstack.engine.api.enums.EComponentApiType;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ClusterEngineVO;
import com.dtstack.engine.api.vo.ClusterPageVO;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.api.vo.SchedulingVo;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.LdapUserVO;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MockWith(ClusterControllerTest.Mock.class)
public class ClusterControllerTest {

    private ClusterController controller = new ClusterController();


    public static class Mock {

        @MockInvoke(targetClass = ClusterService.class)
        public LdapUserVO getLdapUserOnOpenRangerAndLdap(Long dtUicUserId, Long dtTenantId) {
            return new LdapUserVO();
        }

            @MockInvoke(targetClass = ClusterService.class)
        public ClusterVO addCluster(ClusterDTO clusterDTO) {
            return new ClusterVO();
        }

        @MockInvoke(targetClass = ClusterService.class)
        public PageResult<List<ClusterPageVO>> pageQuery(int currentPage,
                                                         int pageSize,
                                                         String fuzzyClusterName,
                                                         String fuzzyTenantName) {
            return PageResult.EMPTY_PAGE_RESULT;
        }

        @MockInvoke(targetClass = ClusterService.class)
        public String clusterInfo( Long tenantId) {
            return "";
        }

        @MockInvoke(targetClass = ClusterService.class)
        public ClusterVO clusterExtInfo(Long uicTenantId,boolean multiVersion) {
            return new ClusterVO();
        }

        @MockInvoke(targetClass = ClusterService.class)
        public String pluginInfo( Long dtUicTenantId,  String engineTypeStr, Long dtUicUserId,Integer deployMode) {
            return "{}";
        }

        @MockInvoke(targetClass = ClusterService.class)
        public String clusterSftpDir( Long tenantId,  Integer componentType) {
            return "1";
        }

        @MockInvoke(targetClass = ClusterService.class)
        public String pluginInfoForType(Long dtUicTenantId, Boolean fullKerberos, Integer pluginType,boolean needHdfsConfig) {
            return "{}";
        }

        @MockInvoke(targetClass = ClusterService.class)
        public String hiveInfo(Long dtUicTenantId, Boolean fullKerberos, Map<Integer,String > componentVersionMap) {
            return "{}";
        }

        @MockInvoke(targetClass = ClusterService.class)
        public String hiveServerInfo( Long dtUicTenantId, Boolean fullKerberos,Map<Integer,String > componentVersionMap) {
            return "{}";
        }

        @MockInvoke(targetClass = ClusterService.class)
        public String hadoopInfo( Long dtUicTenantId, Boolean fullKerberos,Map<Integer,String > componentVersionMap) {
            return "{}";
        }

        @MockInvoke(targetClass = ClusterService.class)
        public List<ClusterVO> clusters() {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ClusterService.class)
        public String carbonInfo( Long dtUicTenantId, Boolean fullKerberos,Map<Integer,String > componentVersionMap) {
            return "{}";
        }

        @MockInvoke(targetClass = ClusterService.class)
        public String impalaInfo( Long dtUicTenantId, Boolean fullKerberos,Map<Integer,String > componentVersionMap) {
            return "{}";
        }

        @MockInvoke(targetClass = ClusterService.class)
        public String sftpInfo( Long dtUicTenantId) {
            return "{}";
        }

        @MockInvoke(targetClass = ClusterService.class)
        public String getConfigByKey(Long dtUicTenantId, String componentConfName, Boolean fullKerberos,Map<Integer,String > componentVersionMap,boolean needHdfsConfig) {
            return "{}";
        }

        @MockInvoke(targetClass = ClusterService.class)
        public String oracleInfo(Long dtUicTenantId, Long dtUicUserId,Map<Integer,String > componentVersionMap){
            return "{}";
        }

        @MockInvoke(targetClass = ClusterService.class)
        public String greenplumInfo(Long dtUicTenantId, Long dtUicUserId,Map<Integer,String > componentVersionMap){
            return "{}";
        }
        @MockInvoke(targetClass = ClusterService.class)
        public String dbInfo(Long dtUicTenantId, Long dtUicUserId, Integer type) {
            return  "{}";
        }

        @MockInvoke(targetClass = ClusterService.class)
        public void deleteCluster(Long clusterId) {
        }

        @MockInvoke(targetClass = ClusterService.class)
        public ClusterVO getCluster(@RequestParam("clusterId") Long clusterId, @RequestParam("kerberosConfig") Boolean kerberosConfig, @RequestParam("removeTypeName") Boolean removeTypeName) throws Exception{
            return new ClusterVO();
        }

        @MockInvoke(targetClass = EnvironmentContext.class)
        public String getSM2PrivateKey(){
            return "sd";
        }

        @MockInvoke(targetClass = ClusterService.class)
        public ClusterVO getCluster(Long clusterId, Boolean removeTypeName,boolean isFullPrincipal,boolean multiVersion) {
            ClusterVO clusterVO = new ClusterVO();
            SchedulingVo schedulingVo = new SchedulingVo();
            ComponentVO componentVO = new ComponentVO();
            componentVO.setComponentTypeCode(EComponentType.FLINK.getTypeCode());
            schedulingVo.setComponents(Lists.newArrayList(componentVO));
            clusterVO.setScheduling(Lists.newArrayList(schedulingVo));
            return clusterVO;
        }

        @MockInvoke(targetClass = ScheduleDictService.class)
        public Map<String, Object> encryptValue(Map<String, Object> map) {
            return new HashMap<>();
        }


        @MockInvoke(targetClass = ClusterService.class)
        public Boolean isSameCluster(Long dtUicTenantId, List<Long> dtUicTenantIds) {
            return false;
        }


        @MockInvoke(targetClass = ClusterService.class)
        public boolean hasStandalone(Long tenantId, Integer typeCode) {
            return false;
        }

        @MockInvoke(targetClass = ClusterService.class)
        public LdapUserVO getLdapUser(Long dtUicUserId, Long dtTenantId) {
            return new LdapUserVO();
        }

        @MockInvoke(targetClass = ClusterService.class)
        public List<ClusterEngineVO> getAllCluster() {
            return new ArrayList<>();
        }

        }

    @Test
    public void addCluster() {
        controller.addCluster(new ClusterDTO());
    }

    @Test
    public void pageQuery() {
        controller.pageQuery(1,1,"","","",true);
    }

    @Test
    public void clusterInfo() {
        controller.clusterInfo(1L);
    }

    @Test
    public void clusterExtInfo() {
        controller.clusterExtInfo(1L);
    }

    @Test
    public void pluginInfoJSON() {
        controller.pluginInfoJSON(1L,"1",1L,1);
    }

    @Test
    public void pluginInfo() {
        controller.pluginInfo(1L,"1",1L,1);
    }

    @Test
    public void clusterSftpDir() {
        controller.clusterSftpDir(1L,1);
    }

    @Test
    public void pluginInfoForType() {
        controller.pluginInfoForType(1L,false, EComponentApiType.ANALYTICDB_FOR_PG);
    }

    @Test
    public void hiveInfo() {
        controller.hiveInfo(1L,false);
    }

    @Test
    public void hiveServerInfo() {
        controller.hiveServerInfo(1L,false);
    }

    @Test
    public void hadoopInfo() {
        controller.hadoopInfo(1L,false);
    }

    @Test
    public void carbonInfo() {
        controller.carbonInfo(1L,false);
    }

    @Test
    public void impalaInfo() {
        controller.impalaInfo(1L,false);
    }

    @Test
    public void sftpInfo() {
        controller.sftpInfo(1L);
    }

    @Test
    public void getConfigByKey() {
        controller.getConfigByKey(1L,"",false,new HashMap<>());
    }

    @Test
    public void clusters() {
        controller.clusters();
    }

    @Test
    public void tiDBInfo() {
        controller.clusterExtInfo(1L);
    }

    @Test
    public void oracleInfo() {
        controller.oracleInfo(1L,1L);
    }

    @Test
    public void greenplumInfo() {
        controller.greenplumInfo(1L,1L);
    }

    @Test
    public void dbInfo() {
        controller.dbInfo(1L,1L, DbType.ANALYTICDB_FOR_PG);
    }

    @Test
    public void deleteCluster() {
        controller.deleteCluster(1L);
    }

    @Test
    public void getCluster() throws Exception {
        controller.getCluster(1L,false,false);
    }

    @Test
    public void getAllCluster() {
        controller.getAllCluster();
    }

    @Test
    public void prestoInfo() {
        controller.prestoInfo(1L,false);
    }

    @Test
    public void isSameCluster() {
        controller.isSameCluster(1L,null);
    }

    @Test
    public void hasStandalone() {
        controller.hasStandalone(1L,1);
    }

    @Test
    public void getLdapUserName() {
        controller.getLdapUserName(1L,1L);
    }
}