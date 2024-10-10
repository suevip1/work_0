package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.processor.annotation.EnablePrivateAccess;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.po.Tenant;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.vo.ClusterPageVO;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.api.vo.ComponentMultiVersionVO;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.api.vo.IComponentVO;
import com.dtstack.engine.api.vo.SchedulingVo;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EComponentScheduleType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.mockcontainer.impl.ClusterServiceMock;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.po.Tenant;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static com.dtstack.engine.common.constrant.ConfigConstant.DEFAULT_CLUSTER_ID;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-05-16 21:46
 */
@MockWith(value = ClusterServiceMock.class)
@EnablePrivateAccess(srcClass = ClusterService.class)
public class ClusterServiceTest {

    ClusterService clusterService = new ClusterService();

    @Test
    public void testAddCluster() {
        ClusterDTO clusterDTO = new ClusterDTO();
        clusterDTO.setClusterName("mockClusterName");
        clusterService.addCluster(clusterDTO);
    }

    @Test
    public void testClusters() {
        Assert.assertEquals(1, clusterService.clusters().size());
    }

    @Test
    public void testAddDefaultCluster() throws Exception {
        clusterService.addDefaultCluster();
    }

    @Test
    public void testPageQuery() {
        PageResult<List<ClusterPageVO>> result = clusterService.pageQuery(1, 1, "", "", true, null);
    }

    @Test
    public void testClusterInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.clusterInfo(-1L)));
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.clusterInfo(1L)));
    }

    @Test
    public void testClusterExtInfo() {
        Assert.assertNotNull(clusterService.clusterExtInfo(-1L, false));
    }


    public void testPluginInfo() {
        String s = clusterService.pluginInfo(null, null, null, null);
        Assert.assertEquals("{}", s);
    }

    @Test
    public void testGetSslConfig() {
        Assert.assertNotNull(clusterService.getSslConfig(-1L, EComponentType.TRINO_SQL, null));
        Assert.assertNotNull(clusterService.getSslConfig(-1L, EComponentType.YARN, null));
    }

    @Test
    public void testGetTargetSSLConfig() {
        Assert.assertNotNull(clusterService.getTargetSSLConfig(-1L, EComponentType.TRINO_SQL, null));
        Assert.assertNotNull(clusterService.getTargetSSLConfig(-1L, EComponentType.YARN, null));
    }

    @Test
    public void testClusterSftpDir() {
        Assert.assertNotNull(clusterService.clusterSftpDir(-1L, EComponentType.YARN.getTypeCode()));
        Assert.assertNotNull(clusterService.clusterSftpDir(-1L, null));
    }

    @Test
    public void testGetQueue() {
        Assert.assertNotNull(clusterService.getQueue(-1L, -1L, -1L));
    }

    @Test
    public void testGetNamespace() {
        ParamAction paramAction = new ParamAction();
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.getNamespace(paramAction, -1L, "flink", ComputeType.BATCH)));
    }

    @Test
    public void testHiveInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.hiveInfo(-1L, null, null)));
    }

    @Test
    public void testHiveServerInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.hiveServerInfo(-1L, null, null)));
    }

    @Test
    public void testHadoopInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.hadoopInfo(-1L, null, null)));
    }

    @Test
    public void testCarbonInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.carbonInfo(-1L, null, null)));
    }

    @Test
    public void testImpalaInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.impalaInfo(-1L, null, null)));
    }

    @Test
    public void testPrestoInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.prestoInfo(-1L, Boolean.FALSE, null)));
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.prestoInfo(-1L, Boolean.FALSE, null)));
    }

    @Test
    public void testSftpInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.sftpInfo(-1L)));
    }

    @Test
    public void testBuildClusterConfig() {
        ClusterVO clusterVO = mockDefaultClusterVO();
        JSONObject jsonObject = clusterService.buildClusterConfig(clusterVO, null);
        Assert.assertTrue(jsonObject.get("sftpConf") instanceof JSONObject);
    }

    @Test
    public void testGetClusterByTenant() {
        Assert.assertNotNull(clusterService.getClusterByTenant(-1L));
    }

    @Test
    public void testGetCluster() {
        Assert.assertNotNull(clusterService.getCluster(-1L));
        Assert.assertNotNull(clusterService.getCluster(-1L, true, true));
        Assert.assertNotNull(clusterService.getCluster(-1L, true, false, true,false));
    }

    @Test
    public void testGetConfigByKey() {
        String config = clusterService.getConfigByKey(-1L, EComponentType.HDFS.getConfName(), false, null, false);
        Assert.assertTrue(StringUtils.isNotEmpty(config));
    }

    @Test
    public void testAddKerberosConfigWithHdfs() {
        KerberosConfig kerberosConfig = new KerberosConfig();
        kerberosConfig.setOpenKerberos(1);
        kerberosConfig.setClusterId(-1L);
        Assert.assertNotNull(clusterService.addKerberosConfigWithHdfs(EComponentType.YARN.getTypeCode(), -1L, kerberosConfig));
    }


    @Test
    public void testTiDBInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.tiDBInfo(-1L, -1L, null, null)));
    }

    @Test
    public void testOracleInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.oracleInfo(-1L, -1L, null, null)));
    }

    @Test
    public void testMysqlInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.mysqlInfo(-1L, -1L, null, null)));
    }

    @Test
    public void testDb2Info() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.db2Info(-1L, -1L, null, null)));
    }

    @Test
    public void testSqlServerInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.sqlServerInfo(-1L, -1L, null, null)));
    }

    @Test
    public void testOceanBaseInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.oceanBaseInfo(-1L, -1L, null, null)));
    }

    @Test
    public void testGreenplumInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.greenplumInfo(-1L, -1L, null, null)));
    }

    @Test
    public void testInceptorSqlInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.inceptorSqlInfo(-1L, -1L, null)));
    }

    @Test
    public void testAdbPostgrepsqlInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.adbPostgrepsqlInfo(-1L, -1L, null, null)));
    }

    @Test
    public void testGetDataSourceType() {
        Assert.assertSame(com.dtstack.schedule.common.enums.DataSourceType.Oracle, clusterService.getDataSourceType(EComponentType.ORACLE_SQL));
        Assert.assertSame(com.dtstack.schedule.common.enums.DataSourceType.TiDB, clusterService.getDataSourceType(EComponentType.TIDB_SQL));
        Assert.assertSame(com.dtstack.schedule.common.enums.DataSourceType.GREENPLUM6, clusterService.getDataSourceType(EComponentType.GREENPLUM_SQL));
        Assert.assertSame(com.dtstack.schedule.common.enums.DataSourceType.Presto, clusterService.getDataSourceType(EComponentType.PRESTO_SQL));
        Assert.assertSame(com.dtstack.schedule.common.enums.DataSourceType.INCEPTOR_SQL, clusterService.getDataSourceType(EComponentType.INCEPTOR_SQL));
    }

    @Test(expected = Exception.class)
    public void testDeleteDefaultCluster() {
        clusterService.deleteCluster(DEFAULT_CLUSTER_ID);
    }

    @Test
    public void testDeleteCluster() {
        clusterService.deleteCluster(-999L);
    }

    @Test
    public void testGetAllCluster() {
        Assert.assertTrue(CollectionUtils.isNotEmpty(clusterService.getAllCluster()));
    }

    @Test
    public void testPluginInfoForType() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.pluginInfoForType(-1L, false, EComponentType.FLINK.getTypeCode(), false)));
    }

    @Test
    public void testDbInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.dbInfo(-1L, -1L, DataSourceType.MySQL.getVal(), null)));
    }

    @Test
    public void testIsSameCluster() {
        Assert.assertFalse(clusterService.isSameCluster(-1L, Collections.emptyList()));
        Assert.assertFalse(clusterService.isSameCluster(-1L, Lists.newArrayList(1L)));
        Assert.assertTrue(clusterService.isSameCluster(-1L, Lists.newArrayList(-1L)));
    }

    @Test
    public void testHasStandalone() {
        Assert.assertTrue(clusterService.hasStandalone(-1L, EComponentType.FLINK.getTypeCode()));
    }

    @Test
    public void testClearStandaloneCache() {
        clusterService.clearStandaloneCache();
    }

    @Test
    public void testClusterComponent() {
        Assert.assertTrue(CollectionUtils.isNotEmpty(clusterService.clusterComponent(-1L, true)));
    }

    @Test
    public void testComponentInfo() {
        Assert.assertTrue(StringUtils.isNotEmpty(clusterService.componentInfo(-1L, -1L, EComponentType.FLINK.getTypeCode(), null, null)));
    }

    @Test
    public void testGetYarnInfo() {
        Assert.assertNotNull(clusterService.getYarnInfo(-1L,-1L,-1L));
    }

    @NotNull
    private ClusterVO mockDefaultClusterVO() {
        ClusterVO clusterVO = new ClusterVO();
        clusterVO.setClusterId(-1L);

        ComponentVO sftp = new ComponentVO();
        sftp.setId(Long.MAX_VALUE);
        sftp.setClusterId(-1L);
        sftp.setComponentTypeCode(EComponentType.SFTP.getTypeCode());
        sftp.setIsDefault(true);
        IComponentVO sftpMulti = ComponentMultiVersionVO.getInstanceWithCapacityAndType(EComponentType.SFTP.getTypeCode(), 1);
        sftpMulti.addComponent(sftp);

        SchedulingVo commonSchedule = new SchedulingVo();
        commonSchedule.setSchedulingCode(EComponentScheduleType.COMMON.getType());
        commonSchedule.setComponents(Lists.newArrayList(sftpMulti));

        List<SchedulingVo> scheduling = Lists.newArrayList(commonSchedule);
        clusterVO.setScheduling(scheduling);
        return clusterVO;
    }
}