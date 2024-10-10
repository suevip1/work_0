package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dto.ConsoleJobRestrictQueryDTO;
import com.dtstack.engine.master.impl.ConsoleJobRestrictService;
import com.dtstack.engine.master.router.permission.Authenticate;
import com.dtstack.engine.master.router.util.CookieUtil;
import com.dtstack.engine.master.vo.ConsoleJobRestrictVO;
import com.dtstack.engine.po.ConsoleJobRestrict;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 实例限制提交配置
 *
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-02 17:08
 */
@RestController
@RequestMapping("/node/console/job/restrict")
@Api(value = "/node/console/job/restrict", tags = {"实例限制提交配置"})
public class ConsoleJobRestrictController {

    @Autowired
    private ConsoleJobRestrictService consoleJobRestrictService;

    /**
     * 新增管控规则
     *
     * @param jobRestrictQueryDTO
     * @return
     */
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @Authenticate(all = "console_job_restrict_update_all")
    public Long add(@RequestBody ConsoleJobRestrictQueryDTO jobRestrictQueryDTO, HttpServletRequest request) {
        checkParam(jobRestrictQueryDTO);
        jobRestrictQueryDTO.setCreateUserId(CookieUtil.getUserId(request.getCookies()));
        return consoleJobRestrictService.add(jobRestrictQueryDTO);
    }

    /**
     * 分页列表展示
     *
     * @param jobRestrictQueryDTO
     * @return
     */
    @RequestMapping(value="/pageQuery", method = {RequestMethod.POST})
    @Authenticate(all = "console_job_restrict_view_all")
    public PageResult<List<ConsoleJobRestrictVO>> pageQuery(@RequestBody ConsoleJobRestrictQueryDTO jobRestrictQueryDTO) {
        return consoleJobRestrictService.pageQuery(jobRestrictQueryDTO);
    }

    /**
     * 关闭规则
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/close", method = {RequestMethod.POST})
    @Authenticate(all = "console_job_restrict_update_all")
    public boolean close(@RequestParam("id") Long id) {
        return consoleJobRestrictService.close(id);
    }

    /**
     * 开启规则
     *
     * @param id 规则 id
     * @return
     */
    @RequestMapping(value = "/open", method = {RequestMethod.POST})
    @Authenticate(all = "console_job_restrict_update_all")
    public boolean open(@RequestParam("id") Long id) {
        ConsoleJobRestrict restrict = consoleJobRestrictService.getById(id);
        if (restrict == null) {
            return false;
        }
        Date restrictEndTime = restrict.getRestrictEndTime();
        if (!restrictEndTime.after(new Date())) {
            throw new RdosDefineException("要开启的规则，计划结束时间必须晚于当前时间");
        }

        return consoleJobRestrictService.open(id);
    }

    /**
     * 删除规则
     *
     * @param id 规则 id
     * @return
     */
    @RequestMapping(value = "/remove", method = {RequestMethod.POST})
    @Authenticate(all = "console_job_restrict_update_all")
    public boolean remove(@RequestParam("id") Long id) {
        return consoleJobRestrictService.remove(id);
    }

    /**
     * 是否存在等待或运行中的规则
     *
     * @return
     */
    @RequestMapping(value = "/existWaitOrRunning", method = {RequestMethod.POST})
    @Authenticate(all = "console_job_restrict_view_all")
    public Boolean existWaitOrRunning() {
        return consoleJobRestrictService.existWaitOrRunning();
    }

    private void checkParam(ConsoleJobRestrictQueryDTO jobRestrictQueryDTO) {
        if (StringUtils.isBlank(jobRestrictQueryDTO.getRestrictStartTime())) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        if (StringUtils.isBlank(jobRestrictQueryDTO.getRestrictEndTime())) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        Date restrictStartTime = DateUtil.strictParseDate(jobRestrictQueryDTO.getRestrictStartTime(), DateUtil.STANDARD_DATETIME_FORMAT);
        Date restrictEndTime = DateUtil.strictParseDate(jobRestrictQueryDTO.getRestrictEndTime(), DateUtil.STANDARD_DATETIME_FORMAT);
        if (!restrictEndTime.after(restrictStartTime)) {
            throw new RdosDefineException("结束时间必须晚于开始时间");
        }
        if (!restrictEndTime.after(new Date())) {
            throw new RdosDefineException("结束时间必须晚于当前时间");
        }
    }

}