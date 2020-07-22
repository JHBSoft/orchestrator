package com.simacan.testframework.orchestrator.config

import com.simacan.base.config.ConfigurationLoader
import pureconfig.generic.auto._

class AppConfigLoader extends ConfigurationLoader {
  val config: AppConfiguration = loadConfiguration[AppConfiguration]()
}

object AppConfigLoader {
  lazy val loaded = new AppConfigLoader()
}
