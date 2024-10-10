package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.vo.console.ConsoleProjectAccountVO;
import com.dtstack.engine.po.ConsoleProjectAccount;
import org.mapstruct.Mapper;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-14 15:47
 */
@Mapper(componentModel = "spring")
public interface ConsoleProjectAccountStruct {
    ConsoleProjectAccount toEntity(ConsoleProjectAccountVO consoleProjectAccountVO);

    ConsoleProjectAccountVO toVO(ConsoleProjectAccount consoleProjectAccount);
}
