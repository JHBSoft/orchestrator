package com.simacan.testframework.orchestrator.scheduler

import ch.qos.logback.classic.Level
import com.simacan.testframework.orchestrator.config.{
  AppConfigurationStub,
  AuthConfiguration,
  SchedulerServiceConfiguration
}
import com.simacan.testframework.orchestrator.helpers._
import com.simacan.testframework.orchestrator.scheduler.model._
import com.simacan.testframework.orchestrator.scheduler.tasks._
import org.quartz.{JobKey, Scheduler}
import org.quartz.impl.StdSchedulerFactory
import org.scalatest.Outcome
import org.scalatest.wordspec.{AnyWordSpec, FixtureAnyWordSpec}
import org.scalatest.matchers.should.Matchers

class SchedulerServiceSpec extends FixtureAnyWordSpec with Matchers with Slf4jLogTester {

  case class FixtureParam(schedulerService: SchedulerService)

  def withFixture(test: OneArgTest): Outcome = {
    val fixtureData: FixtureParam = FixtureParam(
      SchedulerService
        .create(AppConfigurationStub.scheduler)
    )

    try {
      withFixture(test.toNoArgTest(fixtureData))
    } finally {
      fixtureData.schedulerService.stop()
      SchedulerService.destroy()
    }
  }

  "Initialization of SchedulerService" should {

    "return null after destroy" in { _ =>
      SchedulerService.destroy()
      SchedulerService.getInstance shouldBe null
    }

    "scheduler is stopped after destroy" in { fp =>
      val scheduler: Scheduler = new StdSchedulerFactory().getScheduler()

      fp.schedulerService.start()
      SchedulerService.destroy()

      scheduler.isShutdown shouldBe true
    }

    "created" in { fp =>
      fp.schedulerService shouldBe a[SchedulerService]
    }

    "return a valid instance when calling getInstance after creation" in { _ =>
      SchedulerService.getInstance shouldBe a[SchedulerService]
    }

    "getInstance should return the create Scheduler service" in { fp =>
      fp.schedulerService shouldEqual SchedulerService.getInstance
    }

    "return one and the same object while after creating twice" in { fp =>
      val myService2 = SchedulerService.create(AppConfigurationStub.scheduler)

      fp.schedulerService.equals(myService2) shouldBe true
    }

    "return same object for create and getInstance (after creation)" in { fp =>
      fp.schedulerService.equals(SchedulerService.getInstance) shouldBe true
    }

  }

  "The scheduler" should {
    "have proper settings (based on quartz.properties)" in { fp =>
      fp.schedulerService.getSchedulerName shouldBe "testframework-orchestrator"

    }
  }

  "scheduler.getTaskDataByName" should {

    "return the task identified by name" in { fp =>
      fp.schedulerService.setTasks()

      val task0: Task[_] =
        fp.schedulerService
          .getTaskDataByTaskName[Task[_]]("task0")

      task0.name shouldBe "task0"
    }
  }

  "scheduler.isRunning" should {

    "return false when not started" in { _ =>
      SchedulerService.getInstance.isRunning shouldBe false
    }

    "return true after call startNewWithTasks()" in { _ =>
      SchedulerService.getInstance.start().isRunning shouldBe true
    }

    "return false after call stop()" in { _ =>
      SchedulerService.getInstance.stop().isRunning shouldBe false
    }

  }

  "scheduler.isStopped" should {
    "return true when not started" in { _ =>
      SchedulerService.getInstance.isStopped shouldBe true
    }

    "return false when started" in { fp =>
      fp.schedulerService.start().isStopped shouldBe false
    }

    "return false when stopped" in { fp =>
      fp.schedulerService.start().stop().isStopped shouldBe true
    }

  }

  "Recreating a scheduler" should {
    "return a new scheduler object" in { fp =>
      val myService2: SchedulerService =
        SchedulerService.recreate(AppConfigurationStub.scheduler)

      fp.schedulerService.equals(myService2) shouldBe false
    }

    "return a new scheduler object with same configuration" in { fp =>
      val fpConfig = fp.schedulerService.getConfig

      val myService2: SchedulerService = SchedulerService.recreate()

      fp.schedulerService.equals(myService2) shouldBe false
      fpConfig.equals(myService2.getConfig) shouldBe true
    }

    "return a different jobCount when using another taskDescription file (scheduler not started)" in { fp =>
      val jobList5: TaskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule("jobSchedule.json")
      fp.schedulerService.setTasks(jobList5).getJobCount shouldBe 5

      val myService2: SchedulerService =
        SchedulerService.recreate(AppConfigurationStub.scheduler)
      val jobFile1: TaskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule("jobSchedule2.json")
      myService2.setTasks(jobFile1).getJobCount shouldBe 1
    }

  }

  "SchedulerService getAllTaskSeq" should {
    "return a sequence of JobKeys" in { fp =>
      val jobKeySeq: Seq[JobKey] =
        fp.schedulerService.setTasks().getAllJobKeySeq

      jobKeySeq should have size 5
    }
  }

  "SchedulerService getCronDataByJobKey" should {
    "return the cron schedule and next execution time" in { fp =>
      val allCronData = fp.schedulerService.setTasks().getAllCronData

      allCronData.tasks should have size 5
      allCronData.tasks.head.task shouldBe "task1"
      allCronData.tasks.head.cronExpression shouldBe "*/2 * * * * ? *"
    }
  }

  "SchedulerService getAllCronData" should {}

  "SchedulerService setTasks" should {

    "add 5 jobs to the Quartz scheduler while using the standard configuration in AppConfigurationStub" in { fp =>
      fp.schedulerService.setTasks().getJobCount shouldBe 5
    }

    "log a success message after loading" in { fp =>
      val testLogger = TestLoggerFactory.getLogger(fp.schedulerService.getClass)
      fp.schedulerService.setTasks().getJobCount shouldBe 5
      testLogger.logsList.size shouldBe 5
      testLogger.logsList(1).getMessage should include("scheduled for")
      testLogger.logsList(2).getLevel shouldBe Level.INFO
    }

    "log a failure message after loading jobScheduleInvalidCron" in { fp =>
      val testLogger = TestLoggerFactory.getLogger(fp.schedulerService.getClass)
      val jobFile: TaskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule("jobScheduleInvalidCron.json")

      fp.schedulerService.setTasks(jobFile).getJobCount shouldBe 4
      testLogger.logsList.size shouldBe 5
      testLogger.logsList.head.getLevel shouldBe Level.WARN
      testLogger.logsList.head.getThrowableProxy.getMessage should include("CronExpression")
      testLogger.logsList.head.getMessage should include("failed scheduling")
      testLogger.logsList(2).getLevel shouldBe Level.INFO
    }

    "has used jobTemplate from Application.conf file" in { fp =>
      val jobFile: TaskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule("jobSchedule.json")

      fp.schedulerService.setTasks(jobFile).getJobCount shouldBe 5
      val task: AwsBatchTask =
        fp.schedulerService.getTaskDataByTaskName[AwsBatchTask]("task0")

      task.submitJobRequest.getJobDefinition shouldBe "Perf-JobDef"
      task.submitJobRequest.getJobQueue shouldBe "BatchJQ"

      task.taskDescription.gatlingJob
        .getOrElse(GatlingJob.empty)
        .jobQueue
        .getOrElse("") shouldBe "BatchJQ"
      task.taskDescription.gatlingJob
        .getOrElse(GatlingJob.empty)
        .jobTemplate
        .getOrElse("") shouldBe "Perf-JobDef"

    }

    "has used jobTemplate from jobScheduleWithOptData.json file" in { fp =>
      val jobFile: TaskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule("jobScheduleWithOptData.json")

      fp.schedulerService.setTasks(jobFile).getJobCount shouldBe 5
      val task: AwsBatchTask =
        fp.schedulerService.getTaskDataByTaskName[AwsBatchTask]("task0")

      task.submitJobRequest.getJobDefinition shouldBe "MyDummyTemplate"
      task.submitJobRequest.getJobQueue shouldBe "MyDummyQueue"

      task.taskDescription.gatlingJob
        .getOrElse(GatlingJob.empty)
        .jobQueue
        .getOrElse("") shouldBe "MyDummyQueue"
      task.taskDescription.gatlingJob
        .getOrElse(GatlingJob.empty)
        .jobTemplate
        .getOrElse("") shouldBe "MyDummyTemplate"

    }

    "have used jobTemplate from task with config from Application.conf file" in { fp =>
      val jobFile: TaskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule("jobSchedule.json")

      fp.schedulerService.setTasks(jobFile).getJobCount shouldBe 5
      val task: AwsBatchTask =
        fp.schedulerService.getTaskDataByTaskName[AwsBatchTask]("task1")

      task.submitJobRequest.getJobDefinition shouldBe "PTC_MegaSim"
      task.submitJobRequest.getJobQueue shouldBe "PTC_TestQueue"

      task.taskDescription.gatlingJob
        .getOrElse(GatlingJob.empty)
        .jobQueue
        .getOrElse("") shouldBe "PTC_TestQueue"
      task.taskDescription.gatlingJob
        .getOrElse(GatlingJob.empty)
        .jobTemplate
        .getOrElse("") shouldBe "PTC_MegaSim"

    }

    "have used jobTemplate from task with config from jobScheduleWithOptData.json file" in { fp =>
      val jobFile: TaskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule("jobScheduleWithOptData.json")

      fp.schedulerService.setTasks(jobFile).getJobCount shouldBe 5
      val task: AwsBatchTask =
        fp.schedulerService.getTaskDataByTaskName[AwsBatchTask]("task1")

      task.submitJobRequest.getJobDefinition shouldBe "PTC_MegaSim"
      task.submitJobRequest.getJobQueue shouldBe "PTC_TestQueue"

    }

  }

  "Scheduler.getAllTaskData" should {
    "return an overview of all tasks currently loaded" in { fp =>
      val taskData = fp.schedulerService.setTasks().getAllTaskData

      taskData.head shouldBe an[TaskDescription]
      taskData(1) shouldBe an[TaskDescription]
      taskData(2) shouldBe an[TaskDescription]
      taskData(3) shouldBe an[TaskDescription]
      taskData(4) shouldBe an[TaskDescription]
    }

    "return proper AwsBatchTask TaskDescription" in { fp =>
      val taskData: Seq[TaskDescription] =
        fp.schedulerService.setTasks().getAllTaskData

      taskData.head.name should include("task")
      taskData.head.gatlingJob.get.script should include("script")
      taskData.head.shellJob shouldBe empty
    }

    "return proper ShellJob TaskDescription" in { fp =>
      val taskData = fp.schedulerService.setTasks().getAllTaskData

      taskData(3).name shouldBe "task3"
      taskData(3).gatlingJob shouldBe empty
      taskData(3).shellJob.get shouldBe "shell3"
    }

    "return proper OtherJob TaskDescription" in { fp =>
      val taskData = fp.schedulerService.setTasks().getAllTaskData

      taskData(4).name shouldBe "task4"
      taskData(4).gatlingJob shouldBe empty
      taskData(4).shellJob shouldBe empty
    }
  }

  "SchedulerRestart" should {

    "return a new scheduler" in { fp =>
      val currentScheduler: SchedulerService = fp.schedulerService.setTasks()
      currentScheduler.getJobCount shouldBe 5
      currentScheduler.isRunning shouldBe false

      val newScheduler = SchedulerService.tryRestartWithScheduleCheck()
      newScheduler.get.equals(currentScheduler) shouldBe false
      newScheduler.get.isRunning shouldBe false
      newScheduler.get.getJobCount shouldBe 5
    }

    "return the old scheduler" in { fp =>
      val currentScheduler: SchedulerService = fp.schedulerService.setTasks()
      currentScheduler.getJobCount shouldBe 5
      currentScheduler.isRunning shouldBe false

      val newScheduler = SchedulerService.tryRestartWithScheduleCheck(
        SchedulerServiceConfiguration(
          "jobSchedule.incorrect.json",
          "nokeyneeded",
          AuthConfiguration("nouser", "nopw", "myrealm"),
          AppConfigurationStub.scheduler.gatlingJob
        )
      )
      newScheduler.get.equals(currentScheduler) shouldBe true
      newScheduler.get.isRunning shouldBe false
      newScheduler.get.getJobCount shouldBe 5
    }

    "return a new scheduler with different jobcount set by SchedulerServiceConfig" in { fp =>
      val currentScheduler: SchedulerService = fp.schedulerService.setTasks()
      currentScheduler.getJobCount shouldBe 5
      currentScheduler.isRunning shouldBe false

      val newScheduler = SchedulerService.tryRestartWithScheduleCheck(
        SchedulerServiceConfiguration(
          "jobSchedule2.json",
          "nokeyneeded",
          AuthConfiguration("nouser", "nopw", "myrealm"),
          AppConfigurationStub.scheduler.gatlingJob
        )
      )
      newScheduler.get.equals(currentScheduler) shouldBe false
      newScheduler.get.isRunning shouldBe false
      newScheduler.get.getJobCount shouldBe 1
    }

    "return a new scheduler with different jobcount set by ScheduleFile" in { fp =>
      val currentScheduler: SchedulerService = fp.schedulerService.setTasks()
      currentScheduler.getJobCount shouldBe 5
      currentScheduler.isRunning shouldBe false

      val newScheduler = SchedulerService.tryRestartWithScheduleCheck(
        "jobSchedule2.json",
      )
      newScheduler.get.equals(currentScheduler) shouldBe false
      newScheduler.get.isRunning shouldBe false
      newScheduler.get.getJobCount shouldBe 1
    }

  }

  "scheduler" should {
    //This test doesn't return any test results. Just to be checked in AWS Batch to see jobs submitted.
    "Submit jobs to AWS" taggedAs (AwsConnectedTag, CodeShipSkip, WithRunningScheduler) in { _ =>
      val jobList: TaskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule(
          AppConfigurationStub.scheduler.scheduleResource
        )

      SchedulerService.getInstance.setTasks(jobList)
      SchedulerService.getInstance.start().isRunning shouldBe true
      Thread.sleep(10000)
      SchedulerService.getInstance.stop()

    }

    "Submit jobs to AWS and restart with different jobs" taggedAs WithRunningScheduler in { _ =>
      val jobList: TaskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule("jobScheduleOtherOnly.json")

      SchedulerService.getInstance.setTasks(jobList)
      SchedulerService.getInstance.start().isRunning shouldBe true
      SchedulerService.getInstance.getJobCount shouldBe 2
      Thread.sleep(2900)
      SchedulerService
        .tryRestartWithScheduleCheck(
          SchedulerServiceConfiguration(
            "jobScheduleOtherOnly2.json",
            "nokeyneeded",
            AuthConfiguration("nouser", "nopw", "myrealm"),
            AppConfigurationStub.scheduler.gatlingJob
          )
        )
      SchedulerService.getInstance.getJobCount shouldBe 3
      SchedulerService.getInstance.isRunning shouldBe true
      Thread.sleep(10000)
      SchedulerService.getInstance.stop()
    }

    "Submit jobs to AWS and restart with same jobs" taggedAs (AwsConnectedTag, CodeShipSkip, WithRunningScheduler) in {
      _ =>
        SchedulerService.getInstance.setTasks().start()
        SchedulerService.getInstance.isRunning shouldBe true
        SchedulerService.getInstance.getJobCount shouldBe 5
        Thread.sleep(2900)

        // Restart the running scheduler
        SchedulerService.tryRestartWithScheduleCheck()
        SchedulerService.getInstance.isRunning shouldBe true
        SchedulerService.getInstance.getJobCount shouldBe 5
        Thread.sleep(10000)
        SchedulerService.getInstance.stop()
    }

  }
}

/**
  * The testclass below is to test one particular situation where the Scheduler shouldn't be created
  * before any test is done.
  * Including the test in the testclass SchedulerService was not possible for this reason.
  */
class SchedulerServiceAdditionalSpec extends AnyWordSpec with Matchers {

  "SchedulerService (additional)" should {
    "return null while not initialized with create" taggedAs CodeShipSkip in {
      SchedulerService.getInstance shouldBe null
    }

    "be created, loaded and started after calling SchedulerService.startNewWithTasks()" in {
      SchedulerService.startNewWithTasks(AppConfigurationStub.scheduler)

      SchedulerService.getInstance.getJobCount shouldBe 5
      SchedulerService.getInstance.isRunning shouldBe true
      SchedulerService.destroy()
    }
  }
}
