package com.usi.Scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

/**
 * Created by defi on 2017-10-17.
 * 任务调度工具类
 */

public class SchedulerUtil {

    private String jobName;

    private String groupName;

    private String triggerName;

    private Integer hours;

    private Integer minutes;

    private Integer seconds;

    private Class <? extends Job> jobClass;

    //任务开始时间
    private Date startDatetime;

    private String cronExpression;

    private JobDataMap jobDataMap;

    /**
     * 指定时间执行一次任务
     * @param startDatetime
     * @throws Exception
     */
    public void executeJobWithSimpleTrigger(Date startDatetime) throws SchedulerException{
        setStartDatetime(startDatetime);
        JobDetail job = JobBuilder.newJob(jobClass).usingJobData(jobDataMap).build();
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .startAt(startDatetime)
                .build();

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job,trigger);
    }

    /**
     * 指定间隔：x时x分x秒执行一次
     * @param hours
     * @param minutes
     * @param seconds
     * @throws Exception
     */
    public void executeJobWithRepeatForever(Integer hours, Integer minutes, Integer seconds)
            throws Exception{

        setHours(hours);
        setMinutes(minutes);
        setSeconds(seconds);

        JobDetail job = JobBuilder.newJob(jobClass).usingJobData(jobDataMap).build();
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInHours(hours)
                .withIntervalInMinutes(minutes)
                .withIntervalInSeconds(seconds)
                .repeatForever())
                .startNow()
                .build();

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job,trigger);
    }

    /**
     * 指定名称
     * @param jobName
     * @param groupName
     * @throws Exception
     */
    public void executeJobWithSimpleTriggerByName(String jobName,String groupName) throws Exception{

        setJobName(jobName);
        setGroupName(groupName);

        JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobName,groupName).build();
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(triggerName, groupName)
                .startAt(startDatetime)
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
//                .withIntervalInSeconds(5)
//                .repeatForever())
                .build();

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job,trigger);
    }

    /**
     * 使用cron触发器执行
     * @throws Exception
     */
    public void executeJobWithCronTrigger() throws Exception{

        JobDetail job = JobBuilder.newJob(jobClass).build();

        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }

    /**
     * 使用cron触发器 指定名称执行
     * @param jobName
     * @param groupName
     * @throws Exception
     */
    public void executeJobWithCronTriggerByName(String jobName, String groupName) throws Exception{

        setJobName(jobName);
        setGroupName(groupName);

        JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobName, groupName).build();

        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(triggerName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public Class<? extends Job> getJobClass() {
        return jobClass;
    }

    public void setJobClass(Class<? extends Job> jobClass) {
        this.jobClass = jobClass;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    public Date getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public JobDataMap getJobDataMap() {
        return jobDataMap;
    }

    public void setJobDataMap(JobDataMap jobDataMap) {
        this.jobDataMap = jobDataMap;
    }
}
