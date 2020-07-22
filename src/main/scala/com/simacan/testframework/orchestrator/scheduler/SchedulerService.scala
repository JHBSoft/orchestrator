package com.simacan.testframework.orchestrator.scheduler

import java.util.Date

import collection.JavaConverters._
import com.simacan.testframework.orchestrator.config.SchedulerServiceConfiguration
import com.simacan.testframework.orchestrator.scheduler.messages._
import com.simacan.testframework.orchestrator.scheduler.model._
import com.simacan.testframework.orchestrator.scheduler.tasks._
import org.quartz._
import org.quartz.impl.StdSchedulerFactory
import org.quartz.impl.matchers.GroupMatcher
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success, Try}

/**
  * This Service provides the scheduling service to the orchestrator. It provides all the
  * required interaction between the Orchestrator and the Quartz Scheduler.
  *
  * The class has an companion object which offers access to the SchedulerService class.
  * To get an instance call SchedulerService.create(schedulerServiceConfiguration)
  *
  * To have a full restart of the scheduler (including reloading of the tasks) the Quartz Scheduler\
  * needs to be recreated. For this, the companion object offers several restart methods
  *
  * @param config The configuration for the SchedulerService
  */
class SchedulerService private[SchedulerService] (
  config: SchedulerServiceConfiguration
) {

  private[this] lazy val logger: Logger = LoggerFactory.getLogger(getClass)

  private[this] lazy val scheduler: Scheduler =
    new StdSchedulerFactory().getScheduler()

  private[scheduler] def getSchedulerName: String = scheduler.getSchedulerName
  def getJobCount: Int = scheduler.getJobKeys(GroupMatcher.anyJobGroup()).size()

  private[scheduler] def getTaskDataByTaskName[T <: Task[_]](
    taskName: String
  ): T =
    getTaskDataByJobKey[T](TaskJob.getJobKey(taskName))

  private[scheduler] def getTaskDataByJobKey[T <: Task[_]](jobKey: JobKey): T =
    scheduler
      .getJobDetail(jobKey)
      .getJobDataMap
      .get(TaskJob.jobDataMapKey)
      .asInstanceOf[T]

  def getCronDataByTrigger(trigger: Trigger): (String, Date) = {
    val cronExpression =
      trigger match {
        case trigger1: CronTrigger => trigger1.getCronExpression
        case _ => "no cron"
      }
    val nextFireTime = trigger.getNextFireTime

    (cronExpression, nextFireTime)
  }

  def getCronDataByJobKey(jobKey: JobKey): TaskCronData = {
    val (cronExpression, nextFiringTime) = getCronDataByTrigger(
      scheduler
        .getTriggersOfJob(jobKey)
        .get(0)
    )
    TaskCronData(jobKey.getName, cronExpression, nextFiringTime)
  }

  def getAllCronData: GetAllCronDataResponse =
    GetAllCronDataResponse(
      getAllJobKeySeq.map(jobKey => getCronDataByJobKey(jobKey))
    )

  def getAllJobKeySeq: Seq[JobKey] =
    scheduler.getJobKeys(GroupMatcher.anyGroup()).asScala.toSeq

  def getAllTaskData: Seq[TaskDescription] =
    getAllJobKeySeq
      .map(jobKey => getTaskDataByJobKey[Task[_]](jobKey).taskDescription)

  def getConfig: SchedulerServiceConfiguration = config

  def isRunning: Boolean =
    scheduler.isStarted && !(scheduler.isShutdown || scheduler.isInStandbyMode)
  def isStopped: Boolean = scheduler.isShutdown || !scheduler.isStarted

  def setTasks(): SchedulerService = {
    setTasks(
      TaskDescriptionFileLoader
        .loadSchedule(config.scheduleResource)
    )
  }

  def setTasks(jobFile: TaskDescriptionFile): SchedulerService = {
    val mergedConfig: SchedulerServiceConfiguration = config.merge(jobFile)
    jobFile.tasks.foreach(
      taskDescription =>
        TaskJob.submitTaskToScheduler(
          TaskFactory.createFromTaskDescription(taskDescription, mergedConfig),
          scheduler
        ) match {
          case Success(_) => logger.info(s"Task ${taskDescription.name} scheduled for ${taskDescription.cron}")
          case Failure(e) => logger.warn(s"Task ${taskDescription.name} failed scheduling", e)
      }
    )
    this
  }

  def start(): SchedulerService = {
    scheduler.start()
    this
  }

  def stop(waitForTasksToFinish: Boolean = true): SchedulerService = {
    scheduler.shutdown(waitForTasksToFinish)
    this
  }

}

object SchedulerService {

  private var schedulerServiceOption: Option[SchedulerService] = None

  def create(config: SchedulerServiceConfiguration): SchedulerService = {
    schedulerServiceOption = schedulerServiceOption.orElse(Some(new SchedulerService(config)))
    getInstance
  }

  def getInstance: SchedulerService = schedulerServiceOption.orNull

  def recreate(): SchedulerService = recreate(waitForTasksToFinish = true)

  def recreate(waitForTasksToFinish: Boolean): SchedulerService = {
    val config =
      if (schedulerServiceOption.nonEmpty) getInstance.getConfig else null
    recreate(config, waitForTasksToFinish)
  }

  def recreate(config: SchedulerServiceConfiguration): SchedulerService =
    recreate(config, waitForTasksToFinish = true)

  def recreate(config: SchedulerServiceConfiguration, waitForTasksToFinish: Boolean): SchedulerService = {
    destroy(false)
    create(config)
  }

  def startNewWithTasks(
    config: SchedulerServiceConfiguration
  ): SchedulerService =
    create(config).setTasks().start()

  def tryRestartWithScheduleCheck(): Try[SchedulerService] =
    tryPerformRestartWithScheduleCheck((config: SchedulerServiceConfiguration) => config)

  def tryRestartWithScheduleCheck(scheduleFile: String): Try[SchedulerService] = {
    tryPerformRestartWithScheduleCheck(
      (config: SchedulerServiceConfiguration) => config.withScheduleResource(scheduleFile))
  }

  def tryRestartWithScheduleCheck(
    config: SchedulerServiceConfiguration
  ): Try[SchedulerService] =
    tryPerformRestartWithScheduleCheck(_ => config)

  private[this] def tryPerformRestartWithScheduleCheck(
    configModifier: SchedulerServiceConfiguration => SchedulerServiceConfiguration
  ): Try[SchedulerService] =
    Try {
      val configToUse = configModifier(getInstance.getConfig)
      val currentlyRunning = getInstance.isRunning
      val newTaskFile =
        TaskDescriptionFileLoader.loadSchedule(configToUse.scheduleResource)

      if (newTaskFile.tasks.nonEmpty) {
        recreate(configToUse).setTasks(newTaskFile)
        if (currentlyRunning) getInstance.start()
      }
      getInstance
    }

  private[scheduler] def destroy(waitForTasksToFinish: Boolean = true): Unit = {
    if (schedulerServiceOption.nonEmpty)
      getInstance.stop(waitForTasksToFinish)
    schedulerServiceOption = None
  }

}
