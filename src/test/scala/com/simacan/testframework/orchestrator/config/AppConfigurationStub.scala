package com.simacan.testframework.orchestrator.config

import com.simacan.base.http.HttpConfiguration

object AppConfigurationStub {

  val http: HttpConfiguration =
    HttpConfiguration(port = 9000, interface = "0.0.0.0")

  val httpGitHub: HttpConfiguration =
    HttpConfiguration(port = 8000, interface = "0.0.0.0")

  val githubHook: GithubHookHandlerServiceConfiguration =
    GithubHookHandlerServiceConfiguration(
      "123456",
      scala.util.Properties.envOrElse("GITHUB_TOKEN", "emptytoken"),
      "simacan/TestFramework-Scripts",
      "refs/heads/develop",
      scala.util.Properties.envOrElse(
        "GIT_LOCAL_REPO_PATH",
        "/Users/jhb/VirtualBoxShare/TestFramework"
      )
    )

  val gatlingJobConfiguration: GatlingJobConfiguration =
    GatlingJobConfiguration(
      "Perf-JobDef",
      "BatchJQ",
      scala.util.Properties.envOrElse("AWSBATCH_KEY_ID", "novalidkeyid"),
      scala.util.Properties
        .envOrElse("AWSBATCH_SECRET_KEY", "novalidkeysecret"),
      "eu-west-1"
    )

  val scheduler: SchedulerServiceConfiguration =
    SchedulerServiceConfiguration(
      "jobSchedule.json",
      "0102030405060708090a0b0c0d0e0f10",
      AuthConfiguration("testuser", "testpw", "Orchestrator"),
      gatlingJobConfiguration
    )
}
