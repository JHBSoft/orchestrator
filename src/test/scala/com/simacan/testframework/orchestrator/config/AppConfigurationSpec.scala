package com.simacan.testframework.orchestrator.config

import com.simacan.base.config.ConfigurationLoader
import com.simacan.testframework.orchestrator.scheduler.model.{TaskDescription, TaskDescriptionFile}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.generic.auto._

class AppConfigurationSpec extends AnyWordSpec with Matchers with ConfigurationLoader {

  "AppConfiguration" should {

    val appConfiguration = loadConfiguration[AppConfiguration]()

    "have an ApiConfiguration" in {
      appConfiguration.githubHandler.githubhook shouldBe "123456"
      appConfiguration.githubHandler.gitbranchref shouldBe "refs/heads/develop"
      appConfiguration.githubHandler.gitrepository shouldBe "simacan/TestFramework-Scripts"
    }

    "have a httpConfiguration" in {
      appConfiguration.http.port shouldBe 9000
      appConfiguration.http.interface shouldBe "0.0.0.0"
    }

    "have a httpConfiguration for github" in {
      appConfiguration.httpGithub.port shouldBe 7000
      appConfiguration.httpGithub.interface shouldBe "0.0.0.0"
    }
    "have an authConfiguration" in {
      appConfiguration.scheduler.auth.realm shouldBe "Orchestrator"
    }

    "have an schedulerServiceConfiguration" in {
      appConfiguration.scheduler.scheduleResource shouldBe "jobSchedule.json"
      appConfiguration.scheduler.gatlingJob.defaultAwsJobQueue shouldBe "BatchJQ"
      appConfiguration.scheduler.gatlingJob.defaultAwsJobDefinition shouldBe "Perf-JobDef"
    }

  }

  "withScheduleResource data" should {
    val appConfiguration = loadConfiguration[AppConfiguration]()

    "have a properly set scheduleResource and no changes in other settings" in {
      val schedulerServiceConfiguration = appConfiguration.scheduler.withScheduleResource("testfile")

      schedulerServiceConfiguration.scheduleResource shouldBe "testfile"
      schedulerServiceConfiguration.gatlingJob.defaultAwsJobDefinition shouldBe "Perf-JobDef"
    }

    "keep original scheduleResource with empty data set" in {
      val schedulerServiceConfiguration = appConfiguration.scheduler.withScheduleResource("")

      schedulerServiceConfiguration.scheduleResource shouldBe "jobSchedule.json"
      schedulerServiceConfiguration.gatlingJob.defaultAwsJobDefinition shouldBe "Perf-JobDef"
    }
  }

  "Merging data" should {

    val appConfiguration = loadConfiguration[AppConfiguration]()

    "have a properly merged GatlingJobConfiguration (with data to update)" in {
      val gatlingJob: GatlingJobConfiguration =
        appConfiguration.scheduler.gatlingJob.merge(
          TaskDescriptionFile(
            Seq.empty[TaskDescription],
            Some("testTemplate"),
            Some("testQueue")
          )
        )

      gatlingJob.defaultAwsJobDefinition shouldBe "testTemplate"
      gatlingJob.defaultAwsJobQueue shouldBe "testQueue"

    }

    "have a properly merged GatlingJobConfiguration (without data to update)" in {
      val gatlingJob: GatlingJobConfiguration =
        appConfiguration.scheduler.gatlingJob
          .merge(TaskDescriptionFile(Seq.empty[TaskDescription], None, None))

      gatlingJob.defaultAwsJobDefinition shouldBe "Perf-JobDef"
      gatlingJob.defaultAwsJobQueue shouldBe "BatchJQ"

    }

    "have a properly merged schedulerServiceConfiguration (with data to update)" in {
      val newSchedulerConfig: SchedulerServiceConfiguration =
        appConfiguration.scheduler.merge(
          TaskDescriptionFile(
            Seq.empty[TaskDescription],
            Some("testTemplate"),
            Some("testQueue")
          )
        )

      newSchedulerConfig.gatlingJob.defaultAwsJobDefinition shouldBe "testTemplate"
      newSchedulerConfig.gatlingJob.defaultAwsJobQueue shouldBe "testQueue"

    }

    "have a properly merged schedulerServiceConfiguration (without data to update)" in {
      val newSchedulerConfig: SchedulerServiceConfiguration =
        appConfiguration.scheduler.merge(
          TaskDescriptionFile(Seq.empty[TaskDescription], None, None)
        )

      newSchedulerConfig.gatlingJob.defaultAwsJobDefinition shouldBe "Perf-JobDef"
      newSchedulerConfig.gatlingJob.defaultAwsJobQueue shouldBe "BatchJQ"

    }

  }
}
