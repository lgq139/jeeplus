package com.jeeplus.modules.monitor.util;

import com.jeeplus.modules.monitor.constant.ScheduleConstants;
import com.jeeplus.modules.monitor.entity.ScheduleJob;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 定时任务工具类
 *
 * @author FEAR
 */
public class ScheduleUtils {

    /**
     * 得到quartz任务类
     *
     * @param job 执行计划
     * @return 具体执行任务类
     */
    private static Class<? extends Job> getQuartzJobClass(ScheduleJob job) {
        boolean isConcurrent = "1".equals(job.getConcurrent());
        return isConcurrent ? QuartzJobExecution.class : QuartzDisallowConcurrentExecution.class;
    }

    /**
     * 构建任务触发对象
     */
    public static TriggerKey getTriggerKey(String jobId, String jobGroup) {
        return TriggerKey.triggerKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 构建任务键对象
     */
    public static JobKey getJobKey(String jobId, String jobGroup) {
        return JobKey.jobKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 创建定时任务
     */
    public static void createScheduleJob(Scheduler scheduler, ScheduleJob job) throws SchedulerException {
        Class<? extends Job> jobClass = getQuartzJobClass(job);
        // 构建job信息
        String jobId = job.getId();
        String jobGroup = job.getJobGroup();
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(jobId, jobGroup)).build();

        // 表达式调度构建器
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
        cronScheduleBuilder = handleCronScheduleMisfirePolicy(job, cronScheduleBuilder);

        // 按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobId, jobGroup))
                .withSchedule(cronScheduleBuilder).build();

        // 放入参数，运行时的方法可以获取
        jobDetail.getJobDataMap().put(ScheduleConstants.TASK_PROPERTIES, job);

        // 判断是否存在
        if (scheduler.checkExists(getJobKey(jobId, jobGroup))) {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(getJobKey(jobId, jobGroup));
        }

        scheduler.scheduleJob(jobDetail, trigger);

        // 暂停任务
        if (job.getStatus().equals(ScheduleConstants.Status.PAUSE.getValue())) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
    }

    /**
     * 设置定时任务策略
     */
    public static CronScheduleBuilder handleCronScheduleMisfirePolicy(ScheduleJob job, CronScheduleBuilder cb) {
        switch (job.getMisfirePolicy()) {
            case ScheduleConstants.MISFIRE_DEFAULT:
                return cb;
            case ScheduleConstants.MISFIRE_IGNORE_MISFIRES:
                return cb.withMisfireHandlingInstructionIgnoreMisfires();
            case ScheduleConstants.MISFIRE_FIRE_AND_PROCEED:
                return cb.withMisfireHandlingInstructionFireAndProceed();
            case ScheduleConstants.MISFIRE_DO_NOTHING:
                return cb.withMisfireHandlingInstructionDoNothing();
            default:
                throw new RuntimeException("The task misfire policy '" + job.getMisfirePolicy()
                        + "' cannot be used in cron schedule tasks");
        }
    }


    /**
     * 获取所有JobDetail
     *
     * @return 结果集合
     */
    public static List<JobDetail> getJobs(Scheduler scheduler) throws SchedulerException {
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        List<JobDetail> jobDetails = new ArrayList<>();
        for (JobKey key : jobKeys) {
            jobDetails.add(scheduler.getJobDetail(key));
        }
        return jobDetails;
    }

    /**
     * 获取所有计划中的任务
     *
     * @return 结果集合
     */
    public static List<ScheduleJob> getAllScheduleJob(Scheduler scheduler) throws SchedulerException {
        List<ScheduleJob> scheduleJobList = new ArrayList<>();;
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        for (JobKey jobKey : jobKeys) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers) {
                ScheduleJob scheduleJob = new ScheduleJob();
                scheduleJob.setId(jobKey.getName());
                scheduleJob.setJobGroup(jobKey.getGroup());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                scheduleJob.setStatus(triggerState.name());
                //获取要执行的定时任务类名
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                scheduleJob.setClassName(jobDetail.getJobClass().getName());
                //判断trigger
                if (trigger instanceof SimpleTrigger) {
                    SimpleTrigger simple = (SimpleTrigger) trigger;
                    scheduleJob.setCronExpression("重复次数:" + (simple.getRepeatCount() == -1 ?
                            "无限" : simple.getRepeatCount()) + ",重复间隔:" + (simple.getRepeatInterval() / 1000L));
                    scheduleJob.setDescription(simple.getDescription());
                }
                if (trigger instanceof CronTrigger) {
                    CronTrigger cron = (CronTrigger) trigger;
                    scheduleJob.setCronExpression(cron.getCronExpression());
                    scheduleJob.setDescription(cron.getDescription() == null ? ("触发器:" + trigger.getKey()) : cron.getDescription());
                }
                scheduleJobList.add(scheduleJob);
            }
        }
        return scheduleJobList;
    }

    /**
     * 获取所有运行中的任务
     *
     * @return 结果集合
     */
    public static List<ScheduleJob> getAllRunningScheduleJob(Scheduler scheduler) throws SchedulerException {
        List<ScheduleJob> scheduleJobList = null;
        List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
        scheduleJobList = new ArrayList<ScheduleJob>(executingJobs.size());
        for (JobExecutionContext executingJob : executingJobs) {
            ScheduleJob scheduleJob = new ScheduleJob();
            JobDetail jobDetail = executingJob.getJobDetail();
            JobKey jobKey = jobDetail.getKey();
            Trigger trigger = executingJob.getTrigger();
            scheduleJob.setName(jobKey.getName());
            scheduleJob.setJobGroup(jobKey.getGroup());
            //scheduleJob.setDescription("触发器:" + trigger.getKey());
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            scheduleJob.setStatus(triggerState.name());
            //获取要执行的定时任务类名
            scheduleJob.setClassName(jobDetail.getJobClass().getName());
            //判断trigger
            if (trigger instanceof SimpleTrigger) {
                SimpleTrigger simple = (SimpleTrigger) trigger;
                scheduleJob.setCronExpression("重复次数:" + (simple.getRepeatCount() == -1 ?
                        "无限" : simple.getRepeatCount()) + ",重复间隔:" + (simple.getRepeatInterval() / 1000L));
                scheduleJob.setDescription(simple.getDescription());
            }
            if (trigger instanceof CronTrigger) {
                CronTrigger cron = (CronTrigger) trigger;
                scheduleJob.setCronExpression(cron.getCronExpression());
                scheduleJob.setDescription(cron.getDescription());
            }
            scheduleJobList.add(scheduleJob);
        }
        return scheduleJobList;
    }

    /**
     * 获取所有的触发器
     *
     * @return 结果集合
     */
    public List<ScheduleJob> getTriggersInfo(Scheduler scheduler) throws SchedulerException {
        GroupMatcher<TriggerKey> matcher = GroupMatcher.anyTriggerGroup();
        Set<TriggerKey> Keys = scheduler.getTriggerKeys(matcher);
        List<ScheduleJob> triggers = new ArrayList<>();

        for (TriggerKey key : Keys) {
            Trigger trigger = scheduler.getTrigger(key);
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setId(trigger.getJobKey().getName());
            scheduleJob.setJobGroup(trigger.getJobKey().getGroup());
            scheduleJob.setStatus(scheduler.getTriggerState(key).toString());
            if (trigger instanceof SimpleTrigger) {
                SimpleTrigger simple = (SimpleTrigger) trigger;
                scheduleJob.setCronExpression("重复次数:" + (simple.getRepeatCount() == -1 ?
                        "无限" : simple.getRepeatCount()) + ",重复间隔:" + (simple.getRepeatInterval() / 1000L));
                scheduleJob.setDescription(simple.getDescription());
            }
            if (trigger instanceof CronTrigger) {
                CronTrigger cron = (CronTrigger) trigger;
                scheduleJob.setCronExpression(cron.getCronExpression());
                scheduleJob.setDescription(cron.getDescription());
            }
            triggers.add(scheduleJob);
        }
        return triggers;
    }

    /**
     * 修改触发器时间
     *
     */
    public static void modifyTrigger(Scheduler scheduler, ScheduleJob scheduleJob) throws SchedulerException {
        TriggerKey key = TriggerKey.triggerKey(scheduleJob.getId(), scheduleJob.getJobGroup());
        CronTrigger newTrigger = TriggerBuilder.newTrigger()
                .withIdentity(key)
                .withSchedule(CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression()))
                .build();
        scheduler.rescheduleJob(key, newTrigger);
    }

    /**
     * 暂停调度器
     */
    public static void stopScheduler(Scheduler scheduler) throws SchedulerException {
        if (!scheduler.isInStandbyMode()) {
            scheduler.standby();
        }
    }
}
