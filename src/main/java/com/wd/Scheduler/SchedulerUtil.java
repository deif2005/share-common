package com.wd.Scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

/**
 * Created by defi on 2017-10-17.
 * 任务调度工具类
 */

public class SchedulerUtil {

    private String jobName;

    private String jobGroupName;

    private String triggerName;

    private String triggerGroupName;

    private Integer hours=0;

    private Integer minutes=0;

    private Integer seconds=0;

    private Class <? extends Job> jobClass;

    //任务开始时间
    private Date startDatetime;

    private boolean isStartNow=true;

    private String cronExpression="";

    private JobDataMap jobDataMap;

    private boolean isRepeatForever=true;

    private Integer repeatCount=0;

    private Scheduler scheduler;

    private Trigger shareTrigger;

    /**
     * 指定时间执行一次任务
     * @param startDatetime
     * @throws Exception
     */
    public void executeJobWithSimpleTrigger(Date startDatetime){
        setStartDatetime(startDatetime);
        SchedulerExecutor();
    }

    /**
     * 指定间隔：x时x分x秒执行一次
     * @param hours
     * @param minutes
     * @param seconds
     * @throws Exception
     */
    public void executeJobWithRepeatForever(int hours, int minutes, int seconds) {
        setHours(hours); setMinutes(minutes); setSeconds(seconds);
        this.isRepeatForever = true;
        SchedulerExecutor();
    }

    /**
     * 指定job名称
     * @param jobName
     * @param jobGroupName
     * @throws Exception
     */
    public void executeJobWithSimpleTriggerByName(String jobName,String jobGroupName){
        this.jobName = jobName;
        this.jobGroupName = jobGroupName;
        if ("".equals(cronExpression) && (hours==0 && minutes==0 && seconds==0)){
            throw new RuntimeException("未指定执行规则");
        }
        SchedulerExecutor();
    }

    /**
     * 使用cron触发器执行
     * @throws Exception
     */
    public void executeJobWithCronTrigger(String cronExpression){
        this.cronExpression = cronExpression;
        SchedulerExecutor();
    }

    /**
     * 使用cron触发器 指定名称执行
     * @param triggerName
     * @param triggerGroupName
     * @param cronExpression
     * @throws Exception
     */
    public void executeJobWithCronTriggerByName(String triggerName, String triggerGroupName, String cronExpression){
        this.triggerName = triggerName;
        this.triggerGroupName = triggerGroupName;
        this.cronExpression = cronExpression;
        SchedulerExecutor();
    }

    private ScheduleBuilder getScheduleBuilder(){
        CronScheduleBuilder cronScheduleBuilder = null;
        if (!"".equals(cronExpression))
            cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        if (cronScheduleBuilder != null) {
//            cronScheduleBuilder.withMisfireHandlingInstructionDoNothing();
            return cronScheduleBuilder;
        }
        //必须制定触发时间间隔，使用isRepeatForever禁止重复执行
        if (hours == 0 & minutes == 0 & seconds == 0)
            throw new RuntimeException("未指定触发器规则");

        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
        if (isRepeatForever)
            simpleScheduleBuilder.repeatForever();
        if (repeatCount > 0)
            simpleScheduleBuilder.withRepeatCount(repeatCount);
        if (hours != null && hours > 0)
            simpleScheduleBuilder.withIntervalInHours(hours);
        if (minutes != null && minutes > 0)
            simpleScheduleBuilder.withIntervalInMinutes(minutes);
        if (seconds != null && seconds >0)
            simpleScheduleBuilder.withIntervalInSeconds(seconds);
        simpleScheduleBuilder.withMisfireHandlingInstructionNextWithRemainingCount();
        return simpleScheduleBuilder;
    }

    /**
     * 设置触发器
     * @return
     */
    private Trigger getTrigger(){
        if (shareTrigger != null && triggerName.equals(shareTrigger.getKey().getName())
                && triggerGroupName.equals(shareTrigger.getKey().getGroup()))
            return shareTrigger;

        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        if (startDatetime != null)
            triggerBuilder.startAt(startDatetime);
        else if (isStartNow)
            triggerBuilder.startNow();
        else
            throw new RuntimeException("触发器未指定开始时间");

        TriggerKey triggerKey;
        if (triggerName != null && !"".equals(triggerName)){
            if (triggerGroupName != null && !"".equals(triggerGroupName))
                triggerKey = new TriggerKey(triggerName,triggerGroupName);
            else
                triggerKey = new TriggerKey(triggerName);
            triggerBuilder.withIdentity(triggerKey);
        }

        ScheduleBuilder scheduleBuilder = getScheduleBuilder();
        triggerBuilder.withSchedule(scheduleBuilder);

        shareTrigger = triggerBuilder.build();

        return shareTrigger;
    }

    /**
     * 设置执行类
     * @return
     */
    private JobDetail getJobDetail(){
        if (jobClass == null)
            throw new RuntimeException("jobClass未指定");

        JobBuilder jobBuilder = JobBuilder.newJob(jobClass);
        JobKey jobKey;
        if (jobName != null && !"".equals(jobName)){
            if (jobGroupName != null && !"".equals(jobGroupName))
                jobKey = new JobKey(jobName,jobGroupName);
            else
                jobKey = new JobKey(jobName);
            jobBuilder.withIdentity(jobKey);
        }
        JobDetail job = jobBuilder.build();
        return job;
    }

    private void SchedulerExecutor(){
        if (scheduler==null)
            instanceScheduler();
        try {
            JobDetail job = getJobDetail();
            Trigger trigger = getTrigger();
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        }catch (SchedulerException e){
            e.printStackTrace();
        }
    }

    private void instanceScheduler(){
        try {
            if (scheduler==null)
                this.scheduler = new StdSchedulerFactory().getScheduler();
        }catch (SchedulerException e){
            e.printStackTrace();
        }
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroupName() {
        return jobGroupName;
    }

    public void setJobGroupName(String jobGroupName) {
        this.jobGroupName = jobGroupName;
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

    public boolean isRepeatForever() {
        return isRepeatForever;
    }

    public void setRepeatForever(boolean repeatForever) {
        isRepeatForever = repeatForever;
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(Integer repeatCount) {
        this.repeatCount = repeatCount;
    }

    public boolean isStartNow() {
        return isStartNow;
    }

    public void setStartNow(boolean startNow) {
        isStartNow = startNow;
    }

    public String getTriggerGroupName() {
        return triggerGroupName;
    }

    public void setTriggerGroupName(String triggerGroupName) {
        this.triggerGroupName = triggerGroupName;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

}
