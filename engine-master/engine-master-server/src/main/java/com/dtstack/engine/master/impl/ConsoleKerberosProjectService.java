package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.po.ConsoleKerberosProject;
import com.dtstack.engine.api.enums.OpenKerberosEnum;
import com.dtstack.engine.api.vo.console.KerberosProjectVO;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.dao.ConsoleKerberosProjectDao;
import com.dtstack.engine.master.mapstruct.KerberosProjectStruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2022/3/14 2:17 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class ConsoleKerberosProjectService {

    @Autowired
    private KerberosProjectStruct kerberosProjectStruct;

    @Autowired
    private ConsoleKerberosProjectDao consoleKerberosProjectDao;

    public Boolean addOrUpdateKerberosProject(KerberosProjectVO kerberosProjectVO) {
        // 判断KerberosProject是否存在
        Long projectId = kerberosProjectVO.getProjectId();
        Integer appType = kerberosProjectVO.getAppType();
        ConsoleKerberosProject consoleKerberosProject = consoleKerberosProjectDao.
                selectByProjectId(projectId, appType);

        ConsoleKerberosProject kerberosProject = kerberosProjectStruct.
                toConsoleKerberosProject(kerberosProjectVO);

        kerberosProject.setGmtModified(new Timestamp(System.currentTimeMillis()));
        int count = 0;
        if (consoleKerberosProject == null) {
            // 插入
            kerberosProject.setGmtCreate(new Timestamp(System.currentTimeMillis()));
            kerberosProject.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
            kerberosProject.setOpenKerberos(OpenKerberosEnum.OPEN.getCode());
            if (StringUtils.isBlank(kerberosProject.getRemotePath())) {
                kerberosProject.setRemotePath("");
            }
            count = consoleKerberosProjectDao.insert(kerberosProject);
        } else {
            // 更新
            kerberosProject.setId(consoleKerberosProject.getId());
            count = consoleKerberosProjectDao.updateByPrimaryKeySelective(kerberosProject);
        }

        if (count>0) {
             return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public ConsoleKerberosProject findKerberosProject(Long projectId, Integer appType) {
       return consoleKerberosProjectDao.selectByProjectId(projectId, appType);
    }

}
