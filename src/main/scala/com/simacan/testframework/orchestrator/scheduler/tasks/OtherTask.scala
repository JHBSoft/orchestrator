package com.simacan.testframework.orchestrator.scheduler.tasks

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import com.simacan.testframework.orchestrator.scheduler.model.TaskDescription
import org.slf4j.LoggerFactory

private[scheduler] case class OtherTask(
  override val taskDescription: TaskDescription = TaskDescription.empty
) extends Task[OtherTask] {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  override def execute(): Boolean = {
    logger.info(
      s"EmptyJob: $name -> $cron -> " +
        OffsetDateTime
          .now()
          .format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss"))
    )
    true
  }

  override def withTaskDescription(
    taskDescription: TaskDescription
  ): OtherTask =
    copy(taskDescription = taskDescription)
}
