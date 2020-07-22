package com.simacan.testframework.orchestrator.scheduler.tasks

import com.simacan.testframework.orchestrator.config.SchedulerServiceConfiguration
import com.simacan.testframework.orchestrator.scheduler.model.{JobType, TaskDescription}

object TaskFactory {

  def createFromTaskDescription(
    taskDescription: TaskDescription,
    config: SchedulerServiceConfiguration
  ): Task[_ <: Task[_]] = {
    if (taskDescription.jobType == JobType.Gatling)
      AwsBatchTask
        .create(config.gatlingJob)
        .withTaskDescription(taskDescription)
    else OtherTask().withTaskDescription(taskDescription)

  }

}
