package com.jeeplus.modules.monitor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.modules.monitor.entity.ScheduleJobLog;
import com.jeeplus.modules.monitor.service.ScheduleJobLogService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/monitor/jobLog")
public class ScheduleJobLogController {

    @Autowired
    private ScheduleJobLogService jobLogService;

    /**
     * 查询定时任务调度日志列表
     */
    @GetMapping("/list")
    public AjaxJson list(ScheduleJobLog queries, HttpServletRequest request) {
        String pageNo = StringUtils.defaultIfBlank(request.getParameter("pageNo"), "1");
        String pageSize = StringUtils.defaultIfBlank(request.getParameter("pageSize"), "20");
        LambdaQueryChainWrapper<ScheduleJobLog> wrapper = jobLogService.lambdaQuery();
        if (!Objects.isNull(queries.getJobId())) {
            wrapper.eq(ScheduleJobLog::getJobId, queries.getJobId());
        }
        if (StringUtils.isNotBlank(queries.getJobName())) {
            wrapper.likeRight(ScheduleJobLog::getJobName, queries.getJobName());
        }
        if (StringUtils.isNotBlank(queries.getJobGroup())) {
            wrapper.eq(ScheduleJobLog::getJobGroup, queries.getJobGroup());
        }
        if (StringUtils.isNotBlank(queries.getStatus())) {
            wrapper.eq(ScheduleJobLog::getStatus, queries.getStatus());
        }
        wrapper.orderByDesc(ScheduleJobLog::getCreateDate);
        IPage<ScheduleJobLog> page = wrapper.page(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)));
        return AjaxJson.success().data(page);
    }

    /**
     * 导出定时任务调度日志列表
     */
    @GetMapping("/export")
    public AjaxJson export(ScheduleJobLog sysJobLog) {

        return AjaxJson.success();
    }

    /**
     * 根据调度编号获取详细信息
     */
    @GetMapping(value = "/get/{id}")
    public AjaxJson getInfo(@PathVariable("id") String id) {
        return AjaxJson.success().data(jobLogService.getById(id));
    }


    /**
     * 删除定时任务调度日志
     */
    @PostMapping("/delete")
    public AjaxJson remove(@RequestBody List<Serializable> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            jobLogService.removeByIds(ids);
        }
        return AjaxJson.success();
    }

    /**
     * 清空定时任务调度日志
     */
    @GetMapping("/clean")
    public AjaxJson clean(ScheduleJobLog jobLog) {
        jobLogService.cleanByJobId(jobLog.getJobId());
        return AjaxJson.success();
    }

    /**
     * 清空定时任务调度日志
     */
    @GetMapping("/cleanALl")
    public AjaxJson cleanAll() {
        jobLogService.clean();
        return AjaxJson.success();
    }

}
