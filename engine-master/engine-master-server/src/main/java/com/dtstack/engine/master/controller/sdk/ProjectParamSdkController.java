package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleProjectParamDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.master.impl.ScheduleProjectParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/node/sdk/project/param"})
public class ProjectParamSdkController {

    @Autowired
    private ScheduleProjectParamService scheduleProjectParamService;

    @PostMapping(value = "/addOrUpdate")
    public Long addOrUpdate(@RequestBody ScheduleProjectParamDTO projectParamDTO) {
        return scheduleProjectParamService.addOrUpdate(projectParamDTO);
    }

    @PostMapping(value = "/remove")
    public Boolean remove(@RequestParam("ids")List<Long> ids) {
        return scheduleProjectParamService.remove(ids);
    }

    @PostMapping(value = "/findById")
    public ScheduleProjectParamDTO findById(@RequestParam("id")Long id) {
        return scheduleProjectParamService.findById(id);
    }

    @PostMapping(value = "/page")
    public PageResult<List<ScheduleProjectParamDTO>> page(@RequestParam("currentPage")Integer currentPage, @RequestParam("pageSize")Integer pageSize, ScheduleProjectParamDTO projectParamDTO) {
        return scheduleProjectParamService.page(currentPage, pageSize, projectParamDTO);
    }

    @PostMapping(value = "/findByProjectId")
    public List<ScheduleProjectParamDTO> findByProjectId(@RequestParam("projectId")Long projectId) {
        return scheduleProjectParamService.findByProjectId(projectId);
    }

    @PostMapping(value = "/findTasksByProjectParamId")
    public List<ScheduleTaskShade> findTasksByProjectParamId(@RequestParam("projectParamId")Long projectParamId) {
        return scheduleProjectParamService.findTasksByProjectParamId(projectParamId);
    }
}