package com.simacan.testframework.orchestrator.helpers
import ch.qos.logback.classic.Level
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.LoggerFactory

class Slf4jLogTesterSpec extends AnyWordSpec with Matchers with Slf4jLogTester {

  class DummyClass {

    def logSomethingC(msg: String, exceptionMessage: String): Unit =
      LoggerFactory.getLogger(getClass).info(msg, new Throwable(exceptionMessage))
  }

  object DummyClass {

    def logSomethingO(msg: String, exceptionMessage: String): Unit =
      LoggerFactory.getLogger(getClass).error(msg, new Throwable(exceptionMessage))
  }

  "The Slf4jLogTester" should {
    "capture the logMessage from a Class" in {

      val dummyInstance = new DummyClass

      val testLogger = TestLoggerFactory.getLogger(dummyInstance.getClass)
      import testLogger._
      dummyInstance.logSomethingC("class message", "class exception")
      logsList.size shouldBe 1
      logsList.head.getMessage shouldBe "class message"
      logsList.head.getThrowableProxy.getMessage shouldBe "class exception"
      logsList.head.getLevel shouldBe Level.INFO
    }

    "capture the logMessage from an Object" in {

      val testLogger = TestLoggerFactory.getLogger(DummyClass.getClass)
      import testLogger._
      DummyClass.logSomethingO("object message", "object exception")
      logsList.size shouldBe 1
      logsList.head.getMessage shouldBe "object message"
      logsList.head.getThrowableProxy.getMessage shouldBe "object exception"
      logsList.head.getLevel shouldBe Level.ERROR

    }

    "clear the logMessage List" in {
      val testLogger = TestLoggerFactory.getLogger(DummyClass.getClass)
      import testLogger._

      DummyClass.logSomethingO("object Message 1", "object Exception 1")
      DummyClass.logSomethingO("object Message 2", "object Exception 2")

      logsList.size shouldBe 2
      clearList()

      DummyClass.logSomethingO("object Message 3", "object Exception 3")
      logsList.size shouldBe 1
      logsList.head.getMessage shouldBe "object Message 3"

      clearList()
      logsList.size shouldBe 0
    }
  }

}
