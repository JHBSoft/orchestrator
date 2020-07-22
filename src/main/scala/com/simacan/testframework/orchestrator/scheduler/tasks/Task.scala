package com.simacan.testframework.orchestrator.scheduler.tasks

import com.simacan.testframework.orchestrator.scheduler.model.TaskDescription

private[scheduler] abstract class Task[T <: Task[T]](
  val taskDescription: TaskDescription = TaskDescription.empty
) {
  def cron: String = taskDescription.cron
  def name: String = taskDescription.name

  def withTaskDescription(taskDescription: TaskDescription): Task[T]
  def execute(): Boolean
}
