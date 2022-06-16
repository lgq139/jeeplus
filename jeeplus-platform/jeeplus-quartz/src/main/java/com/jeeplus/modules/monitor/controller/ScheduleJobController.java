package com.jeeplus.modules.monitor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.monitor.entity.ScheduleJob;
import com.jeeplus.modules.monitor.service.ScheduleJobService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/quartz/scheduleJob")
public class ScheduleJobController extends BaseController {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @ModelAttribute
    public ScheduleJob get(@RequestParam(required = false) String id) {
        ScheduleJob entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = scheduleJobService.getById(id);
        }
        if (entity == null) {
            entity = new ScheduleJob();
        }
        return entity;
    }


    /**
     * 定时任务列表数据
     */
    @RequiresPermissions("quartz:scheduleJob:list")
    @GetMapping("list")
    public AjaxJson data(ScheduleJob scheduleJob, HttpServletRequest request, HttpServletResponse response, Model model) {
        String pageNo = StringUtils.defaultIfBlank(request.getParameter("pageNo"), "1");
        String pageSize = StringUtils.defaultIfBlank(request.getParameter("pageSize"), "20");
        LambdaQueryChainWrapper<ScheduleJob> wrapper = scheduleJobService.lambdaQuery();
        wrapper.like(StringUtils.isNotBlank(scheduleJob.getName()), ScheduleJob::getName, scheduleJob.getName());
        wrapper.like(StringUtils.isNotBlank(scheduleJob.getClassName()), ScheduleJob::getClassName, scheduleJob.getClassName());
        wrapper.eq(StringUtils.isNotBlank(scheduleJob.getJobGroup()), ScheduleJob::getJobGroup, scheduleJob.getJobGroup());
        wrapper.eq(StringUtils.isNotBlank(scheduleJob.getStatus()), ScheduleJob::getStatus, scheduleJob.getStatus());
        wrapper.orderByDesc(ScheduleJob::getUpdateDate);
        IPage<ScheduleJob> page = wrapper.page(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)));
        return AjaxJson.success().data(page);
    }

    /**
     * 查看，增加，编辑定时任务表单页面
     */
    @GetMapping("queryById")
    public AjaxJson queryById(ScheduleJob scheduleJob) {
        return AjaxJson.success().data(scheduleJobService.getById(scheduleJob.getId()));
    }

    /**
     * 保存定时任务
     */
    @RequiresPermissions(value = {"quartz:scheduleJob:add", "quartz:scheduleJob:edit"}, logical = Logical.OR)
    @PostMapping("save")
    public AjaxJson save(ScheduleJob scheduleJob) throws Exception {
        /**
         * 后台hibernate-validation插件校验
         */
        String errMsg = beanValidator(scheduleJob);
        if (StringUtils.isNotBlank(errMsg)) {
            return AjaxJson.error(errMsg);
        }

        //验证cron表达式
        if (!CronExpression.isValidExpression(scheduleJob.getCronExpression())) {
            return AjaxJson.error("新增任务 '" + scheduleJob.getName() + "' 失败，Cron表达式不正确");
        } else if (StringUtils.containsIgnoreCase(scheduleJob.getInvokeTarget(), "rmi://")) {
            return AjaxJson.error("新增任务'" + scheduleJob.getName() + "'失败，目标字符串不允许'rmi://'调用");
        } else if (StringUtils.containsIgnoreCase(scheduleJob.getInvokeTarget(), "ldap://")) {
            return AjaxJson.error("新增任务'" + scheduleJob.getName() + "'失败，目标字符串不允许'ldap://'调用");
        } else if (StringUtils.containsAnyIgnoreCase(scheduleJob.getInvokeTarget(), new String[] { "http://", "https://" })) {
            return AjaxJson.error("新增任务'" + scheduleJob.getName() + "'失败，目标字符串不允许'http(s)//'调用");
        }

        scheduleJobService.saveOrUpdate(scheduleJob);//保存

        return AjaxJson.success("保存定时任务成功!");
    }


    /**
     * 批量删除定时任务
     */
    @RequiresPermissions("quartz:scheduleJob:del")
    @PostMapping("delete")
    public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) throws SchedulerException {
        AjaxJson j = new AjaxJson();
        String[] idArray = ids.split(",");
        for (String id : idArray) {
            scheduleJobService.delete(scheduleJobService.getById(id));
        }
        return AjaxJson.success("删除定时任务成功");
    }


    /**
     * 暂停任务
     */
    @RequiresPermissions("quartz:scheduleJob:stop")
    @PostMapping("stop")
    public AjaxJson stop(ScheduleJob scheduleJob) throws SchedulerException {
        scheduleJobService.stopJob(scheduleJob);
        return AjaxJson.success("暂停成功!");
    }


    /**
     * 立即运行一次
     */
    @RequiresPermissions("quartz:scheduleJob:startNow")
    @PostMapping("startNow")
    public AjaxJson startNow(ScheduleJob scheduleJob) throws SchedulerException {
        scheduleJobService.startNowJob(scheduleJob);
        return AjaxJson.success("运行成功");
    }

    /**
     * 恢复
     */
    @RequiresPermissions("quartz:scheduleJob:resume")
    @PostMapping("resume")
    public AjaxJson resume(ScheduleJob scheduleJob) throws SchedulerException {
        scheduleJobService.restartJob(scheduleJob);
        return AjaxJson.success("恢复成功");
    }

    /**
     * 验证类任务类是否存在
     */
    @GetMapping("existsClass")
    public AjaxJson existsClass(String className) {
        Class job = null;
        try {
            job = Class.forName(className);
            return AjaxJson.success();
        } catch (ClassNotFoundException e1) {
            return AjaxJson.error("类不存在");
        }
    }

}
