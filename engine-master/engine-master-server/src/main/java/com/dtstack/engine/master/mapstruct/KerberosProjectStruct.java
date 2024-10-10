package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.po.ConsoleKerberosProject;
import com.dtstack.engine.api.vo.console.KerberosProjectVO;
import org.mapstruct.Mapper;

/**
 * @Auther: dazhi
 * @Date: 2022/3/14 2:28 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface KerberosProjectStruct {

    ConsoleKerberosProject toConsoleKerberosProject(KerberosProjectVO kerberosProjectVO);

    KerberosProjectVO toKerberosProjectVO(ConsoleKerberosProject consoleKerberosProject);
}
