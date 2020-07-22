package com.simacan.testframework.orchestrator.scheduler.model

import com.simacan.testframework.orchestrator.scheduler.tasks.TaskDescriptionFileLoader
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class TaskDescriptionFileSpec extends AnyWordSpec with Matchers {

  val jobScheduleFile: String =
    getClass.getResource("/jobSchedule.json").getPath

  "TaskDescriptionFile.tasks.size" should {

    "return 5" in {
      TaskDescriptionFileLoader
        .loadSchedule(jobScheduleFile)
        .tasks
        .size shouldBe 5
    }

  }

  "TaskDescriptionFile.tasks.isEmptyOrInvalid" should {
    "return false" in {
      TaskDescriptionFileLoader
        .loadSchedule(jobScheduleFile)
        .tasks
        .isEmpty shouldBe false
    }

    "return true" in {
      TaskDescriptionFileLoader
        .loadSchedule(jobScheduleFile + ".notexist")
        .tasks
        .isEmpty shouldBe true
    }
  }

  "Initialize TaskDescriptionFile with invalid input" should {
    "return a Seq with size 0 with inexisting file" in {
      val TaskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule(jobScheduleFile + ".notexist")
      TaskDescriptionFile.tasks.size shouldBe 0
      TaskDescriptionFile.tasks should have size 0
      TaskDescriptionFile.tasks.isEmpty shouldBe true
    }

    "return a Seq with size 0 with an incorrect file" in {
      val TaskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule("jobSchedule.incorrect.json")
      TaskDescriptionFile.tasks.size shouldBe 0
      TaskDescriptionFile.tasks should have size 0
      TaskDescriptionFile.tasks.isEmpty shouldBe true
    }
  }

  "Initialize TaskDescriptionFile with valid input" should {
    "return a Seq with size 5" in {
      val TaskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule(jobScheduleFile)
      TaskDescriptionFile.tasks.size shouldBe 5
      TaskDescriptionFile.tasks should have size 5
    }
  }

  "Initialize TaskDescriptionFile with proper input when parsed" should {

    val taskDescriptions =
      TaskDescriptionFileLoader.loadSchedule(jobScheduleFile).tasks

    "return a Sequence with size 5" in {
      taskDescriptions should have size 5
    }

    "the first element should be of type TaskDescription" in {
      taskDescriptions.head shouldBe a[TaskDescription]
    }

    "the first element should contain a field name: task0" in {
      taskDescriptions.head.name shouldBe "task0"
    }

    "this first element should contain a testJob" in {
      taskDescriptions.head.gatlingJob should not be empty
    }

    "the gatlingJob of the first element should be a GatlingJob" in {
      taskDescriptions.head.gatlingJob.get shouldBe a[GatlingJob]
    }

    "The gatlingJob of the first element should have a non empty field containerOverrides" in {
      taskDescriptions.head.gatlingJob.get.options should not be empty
    }

    "The gatlingJob of the first element should have description: description0" in {
      taskDescriptions.head.gatlingJob.get.description.get shouldBe "description0"
    }

    "The gatlingJob of taskDescriptions(1) should have an empty description field" in {
      taskDescriptions(1).gatlingJob.get.description shouldBe empty
    }

    "taskDescription(3) should not have a gatlingJob" in {
      taskDescriptions(3).gatlingJob shouldBe empty
    }

    "The gatlingJob in taskDescription(2) should have an empty field containerOverrides" in {
      taskDescriptions(2).gatlingJob.get.options shouldBe empty
    }

    "taskDescription(2).gatlingJob should have an empty field options" in {
      taskDescriptions(2).gatlingJob.get.options shouldBe empty
    }

    "the first element of taskDescriptions should contain a gatlingJob with two options" in {
      taskDescriptions.head.gatlingJob.get.options.get should have size 2
    }

    "this first options in the taskDescriptions(0).gatlingJob should be of type GatlingJobOption" in {
      taskDescriptions.head.gatlingJob.get.options.get.head shouldBe a[
        GatlingJobOption
      ]
    }

    "the gatlingJob of taskDescriptions(1) should contain a jobTemplate field" in {
      taskDescriptions(1).gatlingJob.get.jobTemplate should not be empty
    }

    "ContainerOptions of taskDescriptions.head (0) should be empty" in {
      taskDescriptions.head.gatlingJob.get.containerOptions.isEmpty shouldBe true
    }

    "vCPU in ContainerOptions of taskDescription(1) should be set" in {
      taskDescriptions(1).gatlingJob.get.containerOptions.get.vCpu.get shouldBe 5
    }

  }

  "TaskDescription.jobType" should {

    val taskDescriptions =
      TaskDescriptionFileLoader.loadSchedule(jobScheduleFile).tasks

    "contain 'gatling' for TaskDescriptions(0)" in {
      taskDescriptions.head.jobType shouldBe JobType.Gatling
    }

    "contain 'shell' for TaskDescriptions(3)" in {
      taskDescriptions(3).jobType shouldBe JobType.Shell
    }

    "contain 'other' for TaskDescriptions(4)" in {
      taskDescriptions(4).jobType shouldBe JobType.Other
    }

  }

  "Optional Data fields in JobSchedule file" should {

    "not cause errors when not existing" in {
      val taskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule("jobSchedule.json")

      taskDescriptionFile.tasks.size shouldBe 5
      taskDescriptionFile.defaultJobTemplate.isEmpty shouldBe true
      taskDescriptionFile.defaultJobQueue.isEmpty shouldBe true
    }

    "be available" in {
      val taskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule("jobScheduleWithOptData.json")

      taskDescriptionFile.tasks.size shouldBe 5
      taskDescriptionFile.defaultJobQueue.get shouldBe "MyDummyQueue"
      taskDescriptionFile.defaultJobTemplate.get shouldBe "MyDummyTemplate"
    }

    "have empty optional fields with inproper file" in {
      val taskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule("jobSchedule.incorrect.json")

      taskDescriptionFile.tasks shouldBe empty
      taskDescriptionFile.defaultJobTemplate.isEmpty shouldBe true
      taskDescriptionFile.defaultJobQueue.isEmpty shouldBe true
    }

    "have empty optional fields with non existing file" in {
      val taskDescriptionFile =
        TaskDescriptionFileLoader.loadSchedule("jobSchedule.notexisting.json")

      taskDescriptionFile.tasks shouldBe empty
      taskDescriptionFile.defaultJobTemplate.isEmpty shouldBe true
      taskDescriptionFile.defaultJobQueue.isEmpty shouldBe true
    }

  }

  "validateContent" should {
    "return an empty tasks sequence" in {
      val taskDescriptionFile = TaskDescriptionFileLoader.loadSchedule("jobSchedule.json")

      taskDescriptionFile.validateContent.tasks shouldBe empty
    }

    "return tasks with invalid cron-expression" in {
      val taskDescriptionFile = TaskDescriptionFileLoader.loadSchedule("jobScheduleInvalidCron.json")

      taskDescriptionFile.validateContent.tasks.size shouldBe 1
      taskDescriptionFile.validateContent.tasks.head.cron shouldBe "*/1 * * * * * *"
    }
  }

}
