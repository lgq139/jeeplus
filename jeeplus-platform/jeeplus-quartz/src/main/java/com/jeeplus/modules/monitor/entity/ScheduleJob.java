package com.jeeplus.modules.monitor.entity;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jeeplus.core.ext.persistence.DataEntity;
import com.jeeplus.modules.monitor.constant.ScheduleConstants;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

/**
 * 定时任务Entity
 *
 * @author lgf
 * @version 2017-02-04
 */
@TableName("sys_schedule")
public class ScheduleJob extends DataEntity<ScheduleJob> {

    private static final long serialVersionUID = 1L;
    /** 任务名 */
    private String name;
    /** 任务组 */
    private String jobGroup;
    /** 定时规则 */
    private String cronExpression;
    /** 启用状态 */
    private String status;
    /** 通知用户 */
    private String isInfo;
    /** 任务类 */
    private String className;
    /** 触发目标 */
    private String invokeTarget;
    /**
     * Cron计划策略:
     *  0=默认
     *  1=立即触发执行
     *  2=触发一次执行
     *  3=不触发立即执行
     */
    private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT;

    /** 是否并发执行（1允许 0禁止） */
    private String concurrent = "0";
    /** 描述 */
    private String description;

    public ScheduleJob() {
        super();
    }

    public ScheduleJob(String id) {
        super(id);
    }

    @NotNull(message = "任务名不能为空")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull(message = "任务组不能为空")
    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    @NotNull(message = "定时规则不能为空")
    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @NotNull(message = "启用状态不能为空")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsInfo() {
        return isInfo;
    }

    public void setIsInfo(String isInfo) {
        this.isInfo = isInfo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @NotNull(message = "任务方法不能为空")
    public String getInvokeTarget() {
        return invokeTarget;
    }

    public void setInvokeTarget(String invokeTarget) {
        this.invokeTarget = invokeTarget;
    }

    public String getMisfirePolicy() {
        return misfirePolicy;
    }

    public void setMisfirePolicy(String misfirePolicy) {
        this.misfirePolicy = misfirePolicy;
    }

    public String getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(String concurrent) {
        this.concurrent = concurrent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 枚举下次执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getNextValidTime() {
        if (StringUtils.isNotEmpty(cronExpression)) {
            try {
                CronExpression cron = new CronExpression(cronExpression);
                return cron.getNextValidTimeAfter(new Date(System.currentTimeMillis()));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        return null;
    }

}
