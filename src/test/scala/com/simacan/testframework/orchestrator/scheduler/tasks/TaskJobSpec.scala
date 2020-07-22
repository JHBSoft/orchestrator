package com.simacan.testframework.orchestrator.scheduler.tasks

import java.util.{Date, TimeZone}

import ch.qos.logback.classic.Level
import com.simacan.testframework.orchestrator.helpers.Slf4jLogTester
import com.simacan.testframework.orchestrator.config.AppConfigurationStub
import com.simacan.testframework.orchestrator.helpers.CodeShipSkip
import com.simacan.testframework.orchestrator.scheduler.model.TaskDescription
import org.quartz.impl.StdSchedulerFactory
import org.quartz._
import org.scalatest.TryValues
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class TaskJobSpec extends AnyWordSpec with Matchers with TryValues with Slf4jLogTester {

  "getTriggerName" should {
    "return taskName + triggerSuffix" in {
      TaskJob.getTriggerName("myJobName") shouldBe "myJobName_trigger"
    }
  }

  "getCronSchedule" should {
    "return proper CronSchedule" taggedAs CodeShipSkip in {
      val execDate: Date = TaskJob
        .getCronSchedule("0/5 * 8 * * ? *")
        .build()
        .getFireTimeAfter(new Date(4102444800000L)) //After 2100-01-01 00:00:00 CET

      execDate shouldBe new Date(4102470000000L) // Wil be 2100-01-01 08:00:00 CET
    }

    "return proper CronSchedule with TimeZone set" in {
      val execDate: Date = TaskJob
        .getCronSchedule("0/5 * 8 * * ? *", TimeZone.getTimeZone("UTC"))
        .build()
        .getFireTimeAfter(new Date(4102444800000L)) //After 2100-01-01 00:00:00 CET

      execDate shouldBe new Date(4102473600000L) // Will be 2100-01-01 09:00:00 CET (UTC +1)
    }

    "handle incorrect CronSchedule" in {
      assertThrows[Exception] {
        TaskJob
          .getCronSchedule("0/5 * 8 * * * *")
          .build()
          .getFireTimeAfter(new Date(4102444800000L))
      }
    }
  }

  "getCronTrigger" should {
    "return a proper CronTrigger" taggedAs CodeShipSkip in {
      val cronTrigger: CronTrigger =
        TaskJob.getCronTrigger("myJob", "0/5 * 8 * * ? *")

      cronTrigger.getFireTimeAfter(new Date(4102444800000L)) shouldBe new Date(
        4102470000000L
      )
      cronTrigger.getKey.toString shouldBe "testframework-orchestrator_group.myJob"
    }
  }

  "getJobKey" should {

    "return the JobKey" in {
      TaskJob.getJobKey("task0") shouldEqual new JobKey(
        "task0",
        TaskJob.schedulerGroupName
      )
    }
  }
  "createJobDataMapFromTask" should {
    "return a JobDataMap with a Task inside it" in {
      val originalTask: Task[_] =
        TaskFactory.createFromTaskDescription(
          TaskDescription(
            "myName",
            "0/5 * 8 * * ? *",
            None,
            Some("exec my shell job")
          ),
          AppConfigurationStub.scheduler
        )

      val jobDataMap: JobDataMap =
        TaskJob.createJobDataMapFromTask(originalTask)

      val retrievedTask: Task[_] =
        jobDataMap.get(TaskJob.jobDataMapKey).asInstanceOf[Task[_]]

      retrievedTask.cron shouldBe "0/5 * 8 * * ? *"
      retrievedTask.name shouldBe "myName"
    }
  }

  "getJobDetail" should {
    "return a JobDetail with a Task inside it" in {
      val originalTask: Task[_] =
        TaskFactory.createFromTaskDescription(
          TaskDescription(
            "myName",
            "0/5 * 8 * * ? *",
            None,
            Some("exec my shell job")
          ),
          AppConfigurationStub.scheduler
        )

      val jobDetail: JobDetail = TaskJob.getJobDetail(originalTask)

      jobDetail.getKey.toString shouldBe "testframework-orchestrator_group.myName"
    }
  }

  "submitTaskToSchedule" should {
    "return next firing time within 5000 milliseconds from now" in {
      val scheduler: Scheduler =
        StdSchedulerFactory.getDefaultScheduler

      val originalTask: Task[_] =
        TaskFactory.createFromTaskDescription(
          TaskDescription(
            "myName",
            "*/5 * * * * ? *",
            None,
            Some("exec my shell job")
          ),
          AppConfigurationStub.scheduler
        )

      val nextFireTime: Date =
        TaskJob.submitTaskToScheduler(originalTask, scheduler).success.get

      (nextFireTime.getTime - new Date().getTime) should be < 5000L

      // Shutdown the scheduler
      scheduler.shutdown()

    }

    "return a failure when submitting an improper item" in {
      val scheduler: Scheduler =
        StdSchedulerFactory.getDefaultScheduler

      val originalTask: Task[_] =
        TaskFactory.createFromTaskDescription(
          TaskDescription(
            "myName",
            "*/5 * * * * * *",
            None,
            Some("exec my shell job")
          ),
          AppConfigurationStub.scheduler
        )

      val testLogger = TestLoggerFactory.getLogger(TaskJob.getClass)
      TaskJob.submitTaskToScheduler(originalTask, scheduler).failure.exception.getMessage should include(
        "CronExpression")
      testLogger.logsList.size shouldBe 1
      testLogger.logsList.head.getLevel shouldBe Level.WARN
      testLogger.logsList.head.getMessage shouldBe "Adding job myName failed."
      testLogger.logsList.head.getThrowableProxy.getMessage should include("CronExpression")

      // Shutdown the scheduler
      scheduler.shutdown()

    }

  }
}
