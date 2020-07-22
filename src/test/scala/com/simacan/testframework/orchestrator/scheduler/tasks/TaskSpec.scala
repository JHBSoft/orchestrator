package com.simacan.testframework.orchestrator.scheduler.tasks

import com.simacan.testframework.orchestrator.scheduler.model.{TaskDescription, TaskDescriptionFile}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class TaskSpec extends AnyWordSpec with Matchers {

  "All standard Tasks" should {

    val taskDescriptionFile: TaskDescriptionFile =
      TaskDescriptionFileLoader.loadSchedule("jobSchedule.json")

    "have all standard fields (name / cron)" in {
      case class MyTask(
        override val taskDescription: TaskDescription = TaskDescription.empty
      ) extends Task[MyTask] {
        override def execute(): Boolean = true

        override def withTaskDescription(
          taskDescription: TaskDescription
        ): Task[MyTask] =
          copy(taskDescription = taskDescription)
      }

      for (js <- taskDescriptionFile.tasks) {
        val task: Task[_] = MyTask().withTaskDescription(js)
        task.name.isEmpty shouldBe false
        task.cron.isEmpty shouldBe false
        task.taskDescription should not be empty
      }

    }
  }
}
