package com.simacan.testframework.orchestrator.webhook

import com.simacan.testframework.orchestrator.config.GithubHookHandlerServiceConfiguration
import com.simacan.testframework.orchestrator.helpers._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

/**
  * This test class is an addition to the GitRepoSpec class
  * This will test the GitRepo class with a real connection to Github to test authentication
  * Tests will be skipped in CodeShip testing
  *
  * The test expects a localRepo cloned from https://github.com/simacan/TestFramework-Scripts.git
  * with a personal access key configured.
  * Add those to the test via environment variables:
  *    GITHUB_TOKEN
  *    GIT_LOCAL_REPO_PATH
  */
class GitRepoLiveSpec extends AnyWordSpec with Matchers {

  val localRepo: String =
    scala.util.Properties.envOrElse("GIT_LOCAL_REPO_PATH", "invaliddir")

  "TryPull with authentication" should {

    "fail without Authentication" taggedAs (GitHubConnectedTag, CodeShipSkip) in {
      lazy val localRepoConfiguration: GithubHookHandlerServiceConfiguration =
        GithubHookHandlerServiceConfiguration(
          "123456",
          "emptytoken",
          "simacan/TestFramework-Scripts",
          "refs/heads/develop",
          localRepo
        )

      new GitRepo(localRepoConfiguration).tryPull().isSuccess shouldBe false
    }

    "succeed with correct authentication" taggedAs (GitHubConnectedTag, CodeShipSkip) in {
      lazy val localRepoConfiguration: GithubHookHandlerServiceConfiguration =
        GithubHookHandlerServiceConfiguration(
          "123456",
          scala.util.Properties.envOrElse("GITHUB_TOKEN", "invalidkey"),
          "simacan/TestFramework-Scripts",
          "refs/heads/develop",
          localRepo
        )
      new GitRepo(localRepoConfiguration)
        .tryPullWithAuthentication()
        .isSuccess shouldBe true
    }

    "succeed with invalid authentication" taggedAs (GitHubConnectedTag, CodeShipSkip) in {
      lazy val localRepoConfiguration: GithubHookHandlerServiceConfiguration =
        GithubHookHandlerServiceConfiguration(
          "123456",
          "emptytoken",
          "simacan/TestFramework-Scripts",
          "refs/heads/develop",
          localRepo
        )

      new GitRepo(localRepoConfiguration)
        .tryPullWithAuthentication()
        .isSuccess shouldBe false
    }

  }
}
