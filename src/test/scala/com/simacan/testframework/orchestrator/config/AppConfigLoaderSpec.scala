package com.simacan.testframework.orchestrator.config

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class AppConfigLoaderSpec extends AnyWordSpec with Matchers {

  "AppConfigLoader" should {
    "return the configuration" in {
      val config: AppConfiguration = AppConfigLoader.loaded.config

      config.scheduler.scheduleResource shouldBe "jobSchedule.json"
    }
  }
}
