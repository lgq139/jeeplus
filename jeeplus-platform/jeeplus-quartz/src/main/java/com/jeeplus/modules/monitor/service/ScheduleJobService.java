package com.jeeplus.modules.monitor.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.monitor.constant.ScheduleConstants;
import com.jeeplus.modules.monitor.util.ScheduleUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.modules.monitor.entity.ScheduleJob;
import com.jeeplus.modules.monitor.mapper.ScheduleJobMapper;

import javax.annotation.PostConstruct;

/**
 * 定时任务Service
 *
 * @author FEAR
 */
@Service
@Transactional
public class ScheduleJobService extends ServiceImpl<ScheduleJobMapper, ScheduleJob> {

    @Autowired
    private Scheduler scheduler;

    /**
     * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
     */
    @PostConstruct
    public void init() throws SchedulerException {
        scheduler.clear();
        List<ScheduleJob> jobList = list();
        for (ScheduleJob job : jobList) {
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(ScheduleJob scheduleJob) {
        boolean res = super.saveOrUpdate(scheduleJob);
        if (res) {
            try {
                if (StringUtils.isNotBlank(scheduleJob.getId())) {
                    ScheduleJob job = getById(scheduleJob.getId());
                    scheduler.deleteJob(ScheduleUtils.getJobKey(job.getId(), job.getJobGroup()));
                }
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }
        return res;
    }

    /**
     * 删除任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(ScheduleJob scheduleJob) throws SchedulerException {
        if (removeById(scheduleJob)) {
            scheduler.deleteJob(ScheduleUtils.getJobKey(scheduleJob.getId(), scheduleJob.getJobGroup()));
        }
    }

    /**
     * 暂停任务
     *
     */
    @Transactional(rollbackFor = Exception.class)
    public void stopJob(ScheduleJob scheduleJob) throws SchedulerException {
        scheduleJob.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        if (updateById(scheduleJob)) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(scheduleJob.getId(), scheduleJob.getJobGroup()));
        }
    }

    /**
     * 恢复任务
     *
     */
    @Transactional(rollbackFor = Exception.class)
    public void restartJob(ScheduleJob scheduleJob) throws SchedulerException {
        scheduleJob.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        if (updateById(scheduleJob)) {
            scheduler.resumeJob(ScheduleUtils.getJobKey(scheduleJob.getId(), scheduleJob.getJobGroup()));
        }
    }

    /**
     * 立马执行一次任务
     */
    public void startNowJob(ScheduleJob scheduleJob) throws SchedulerException {
        ScheduleJob properties = getById(scheduleJob);
        // 参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, properties);
        scheduler.triggerJob(ScheduleUtils.getJobKey(scheduleJob.getId(), scheduleJob.getJobGroup()), dataMap);
    }


}
