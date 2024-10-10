package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.vo.console.ConsoleProjectAccountVO;
import com.dtstack.engine.dao.ConsoleProjectAccountDao;
import com.dtstack.engine.master.mapstruct.ConsoleProjectAccountStruct;
import com.dtstack.engine.po.ConsoleProjectAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 项目级账号绑定
 *
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-14 15:34
 */
@Component
public class ConsoleProjectAccountService {

    @Autowired
    private ConsoleProjectAccountDao consoleProjectAccountDao;

    @Autowired
    private ConsoleProjectAccountStruct consoleProjectAccountStruct;

    /**
     * 新增
     * @param projectAccountVO
     * @return 主键 id
     */
    @Transactional(rollbackFor = Exception.class)
    public Long add(ConsoleProjectAccountVO projectAccountVO) {
        projectAccountVO.setId(null);
        ConsoleProjectAccount consoleProjectAccount = consoleProjectAccountStruct.toEntity(projectAccountVO);
        consoleProjectAccountDao.insert(consoleProjectAccount);
        return consoleProjectAccount.getId();
    }

    /**
     * 修改
     * @param projectAccountVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean modify(ConsoleProjectAccountVO projectAccountVO) {
        ConsoleProjectAccount consoleProjectAccount = consoleProjectAccountStruct.toEntity(projectAccountVO);
        return consoleProjectAccountDao.modify(consoleProjectAccount);
    }

    /**
     * 查询
     * @return
     */
    public ConsoleProjectAccountVO findByProjectAndComponent(ConsoleProjectAccountVO projectAccountVO) {
        ConsoleProjectAccount projectAccount = consoleProjectAccountStruct.toEntity(projectAccountVO);
        ConsoleProjectAccount result = consoleProjectAccountDao.findByProjectAndComponent(projectAccount);
        return consoleProjectAccountStruct.toVO(result);
    }
}