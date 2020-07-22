package com.simacan.testframework.orchestrator.scheduler.tasks

import java.util.{Date, TimeZone}

import org.quartz._
import org.slf4j.LoggerFactory

import scala.util.{Failure, Try}

/**
  * The Quartz scheduler needs Job(s) which are scheduled and executed.
  * The TaskJob provides an interface between the task defined in the Orchestrator and
  * the Jobs required by the Quartz Scheduler
  *
  *
  */
class TaskJob extends Job {

  /**
    * Executes a Task provided through the JobDataMap. The JobDataMap is provided within the context
    * by the Quartz Scheduler.
    *
    * @param context JobExecutionContext as provided by the QuartzScheduler
    * @throws JobExecutionException if the job fails to execute.
    */
  @throws[JobExecutionException]
  override def execute(context: JobExecutionContext): Unit = {
    val jobDataMap: JobDataMap = context.getJobDetail.getJobDataMap
    val task: Task[_] =
      jobDataMap.get(TaskJob.jobDataMapKey).asInstanceOf[Task[_]]

    if (!task.execute()) {
      LoggerFactory.getLogger(getClass).error(s"jobExecution failed for: ${task.name}")
      throw new JobExecutionException("jobExecution Failed for: " + task.name)
    }
  }
}

object TaskJob {

  private[scheduler] val schedulerGroupName = "testframework-orchestrator_group"
  private[scheduler] val jobDataMapKey = "taskData"

  private[this] val triggerSuffix = "_trigger"

  /**
    * Return the triggerName associated with a taskName
    * @param taskName name of the task
    * @return
    */
  private[scheduler] def getTriggerName(taskName: String): String =
    taskName + triggerSuffix

  /**
    * Creates a CronScheduleBuilder for a Quartz Job
    *
    * @param cronExpression cronExpression
    * @param timeZone time zone of the times in the cron expression
    * @return
    */
  private[scheduler] def getCronSchedule(
    cronExpression: String,
    timeZone: TimeZone = TimeZone.getDefault
  ): CronScheduleBuilder = {
    CronScheduleBuilder.cronSchedule(cronExpression).inTimeZone(timeZone)
  }

  /**
    * Creates Quartz Scheduler Trigger based on Cron Expression
    * Optionally the timezone can be defined in which the cronExpression is written.
    *
    * @param triggerName Name of the trigger
    * @param cronExpression Cron Expression
    * @param timeZone TimeZone used to define the Cron Expression in
    * @return
    */
  private[scheduler] def getCronTrigger(
    triggerName: String,
    cronExpression: String,
    timeZone: TimeZone = TimeZone.getDefault
  ): CronTrigger = {

    TriggerBuilder
      .newTrigger()
      .withIdentity(triggerName, schedulerGroupName)
      .startNow()
      .withSchedule(getCronSchedule(cronExpression, timeZone))
      .build()
  }

  /**
    * Create a data exchange object for a Quartz Scheduler Job.
    * The JobDataMap is used to provide the task data to the scheduled TaskJob
    * which can be executed by the Quartz Scheduler
    *
    * @param task The Task to execute
    * @return a JobDataMap
    */
  def createJobDataMapFromTask(task: Task[_]): JobDataMap = {
    val jobDataMap: JobDataMap = new JobDataMap()
    jobDataMap.put(jobDataMapKey, task)

    jobDataMap
  }

  /**
    * Gets the actual plan-able activity for the Quartz Scheduler. This jobDetail can together
    * with the Trigger (from getCronTrigger) be submitted to the Quartz Scheduler with
    * submitTaskToScheduler
    *
    * @param task the task to be scheduled
    * @return
    */
  def getJobDetail(task: Task[_]): JobDetail = {
    JobBuilder
      .newJob(classOf[TaskJob])
      .withIdentity(task.name, schedulerGroupName)
      .setJobData(createJobDataMapFromTask(task))
      .build
  }

  /**
    * Generates a JobKey based on the taskName
    * @param taskName the name of the Task
    * @return JobKey
    */
  def getJobKey(taskName: String): JobKey =
    new JobKey(taskName, schedulerGroupName)

  /**
    * Submits a task to a given Quartz Scheduler
    *
    * @param task The task to be scheduled
    * @param scheduler The scheduler to add the task to
    * @return
    */
  def submitTaskToScheduler(task: Task[_], scheduler: Scheduler): Try[Date] = {

    Try(
      scheduler.scheduleJob(
        getJobDetail(task),
        getCronTrigger(getTriggerName(task.name), task.cron)
      )) recoverWith {
      case e: Throwable =>
        LoggerFactory.getLogger(getClass).warn(s"Adding job ${task.name} failed.", e)
        Failure(e)
    }
  }
}
