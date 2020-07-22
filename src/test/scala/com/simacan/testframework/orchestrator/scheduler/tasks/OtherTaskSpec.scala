package com.simacan.testframework.orchestrator.scheduler.tasks
import ch.qos.logback.classic.Level
import com.simacan.testframework.orchestrator.helpers.Slf4jLogTester
import com.simacan.testframework.orchestrator.scheduler.model.TaskDescription
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class OtherTaskSpec extends AnyWordSpec with Matchers with Slf4jLogTester {

  "Execute an OtherTask" should {
    "create a LogEntry" in {

      val taskDescription = TaskDescription("OtherTaskToDo", "* */1 * * * ? *", None, None)
      val otherTask = OtherTask(taskDescription)
      val testLogger = TestLoggerFactory.getLogger(otherTask.getClass)
      otherTask.execute()
      testLogger.logsList.size shouldBe 1
      testLogger.logsList.head.getMessage should include(
        s"EmptyJob: ${taskDescription.name} -> ${taskDescription.cron} -> ")
      testLogger.logsList.head.getLevel shouldBe Level.INFO
    }
  }
}
