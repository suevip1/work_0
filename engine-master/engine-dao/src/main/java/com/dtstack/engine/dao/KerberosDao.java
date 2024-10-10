package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.KerberosConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface KerberosDao {

    KerberosConfig getByComponentType(@Param("clusterId") Long clusterId, @Param("componentType") Integer componentType,@Param("componentVersion")String componentVersion);

    List<KerberosConfig> getByClusters(@Param("clusterId") Long clusterId);

    List<KerberosConfig> listAll();

    /**
     * attention:只要变更了 console_kerberos 记录，就要同步更新远程「.lock」文件，保持时间戳一致
     * ref:com.dtstack.engine.master.impl.KerberosService#refreshKerberos
     * @param kerberosConfig
     * @return
     */
    Integer update(KerberosConfig kerberosConfig);

    /**
     * attention:只要变更了 console_kerberos 记录，就要同步更新远程「.lock」文件，保持时间戳一致
     * ref:com.dtstack.engine.master.impl.KerberosService#refreshKerberos
     * @param kerberosConfig
     * @return
     */
    Integer insert(KerberosConfig kerberosConfig);

    Integer updateAllKrb5Content(String krb5Content);

    void deleteByComponentId(@Param("componentId") Long componentId);

    void deleteByComponent(@Param("clusterId") Long clusterId, @Param("componentTypeCode")Integer componentTypeCode,@Param("componentVersion")String componentVersion);

    KerberosConfig findById(@Param("id")Long id);
}
