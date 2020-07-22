package com.simacan.testframework.orchestrator.helpers

import scala.collection.JavaConverters._
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.slf4j.{LoggerFactory, Logger => Slf4jLogger}

trait Slf4jLogTester {

  class TestLogger(logger: Slf4jLogger) {

    // Create a memory logger and add it to the logger
    private lazy val listAppender: ListAppender[ILoggingEvent] = new ListAppender
    listAppender.start()
    logger.asInstanceOf[ch.qos.logback.classic.Logger].addAppender(listAppender)
    def logsList: Seq[ILoggingEvent] = listAppender.list.asScala
    def clearList(): Unit = listAppender.list.clear()
  }

  object TestLoggerFactory {

    def getLogger(name: String): TestLogger = new TestLogger(LoggerFactory.getLogger(name))

    def getLogger(clazz: Class[_]): TestLogger =
      new TestLogger(LoggerFactory.getLogger(clazz))

  }
}
