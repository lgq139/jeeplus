package com.jeeplus.modules.monitor.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.jeeplus.modules.monitor.constant.ScheduleConstants;
import com.jeeplus.modules.monitor.entity.ScheduleJob;
import com.jeeplus.modules.monitor.entity.ScheduleJobLog;
import com.jeeplus.modules.monitor.service.ScheduleJobLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 定时任务工作类
 *
 * @author FEAR
 */
@Slf4j
public abstract class AbstractQuartzJob implements Job {

    /**
     * 系统通知bean
     *
     * @return
     */
//    @Bean
//    public SystemInfoSocketHandler systemInfoSocketHandler() {
//        return new SystemInfoSocketHandler();
//    }

    /**
     * 线程本地变量
     */
    private static ThreadLocal<Date> threadLocal = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Object scheduleJob = context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        ScheduleJob job = new ScheduleJob();
        BeanUtils.copyProperties(scheduleJob, job);
        try {
            before(context, job);
            doExecute(context, job);
            after(context, job, null);
        } catch (Exception e) {
            log.error("任务执行异常  - ：", e);
            after(context, job, e);
        }
        log.debug("任务名称 = [" + job.getName() + "]" + " 在 " + dateFormat.format(new Date()) + " 时运行");
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param job  系统计划任务
     */
    protected void before(JobExecutionContext context, ScheduleJob job) {
        threadLocal.set(new Date());
    }

    /**
     * 执行后
     *
     * @param context 工作执行上下文对象
     * @param job  系统计划任务
     */
    protected void after(JobExecutionContext context, ScheduleJob job, Exception e) {
        Date startTime = threadLocal.get();
        threadLocal.remove();

        final ScheduleJobLog jobInfo = new ScheduleJobLog();
        jobInfo.setJobId(job.getId());
        jobInfo.setJobName(job.getName());
        jobInfo.setJobGroup(job.getJobGroup());
        jobInfo.setInvokeTarget(job.getInvokeTarget());
        jobInfo.setStartTime(startTime);
        jobInfo.setStopTime(new Date());
        long runMs = DateUtil.between(jobInfo.getStartTime(), jobInfo.getStopTime(), DateUnit.MS);
        jobInfo.setJobMessage(jobInfo.getJobName() + " 总共耗时：" + runMs + "毫秒");
        if (e != null) {
            jobInfo.setStatus("1");
            String errorMsg = StringUtils.substring(ExceptionUtil.getMessage(e), 0, 2000);
            jobInfo.setExceptionInfo(errorMsg);
        } else {
            jobInfo.setStatus("0");
        }

        // 写入数据库当中
        SpringUtil.getBean(ScheduleJobLogService.class).save(jobInfo);
    }

    protected abstract void doExecute(JobExecutionContext context, ScheduleJob job) throws Exception;
}
