package com.simacan.testframework.orchestrator.webhook

import com.simacan.testframework.orchestrator.config.{AppConfigurationStub, GithubHookHandlerServiceConfiguration}
import com.simacan.testframework.orchestrator.scheduler.SchedulerService
import org.scalatest.wordspec.FixtureAnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{TryValues, Outcome}
import play.api.libs.json._

class GitHubHookHandlerServiceSpec extends FixtureAnyWordSpec with Matchers with TryValues {

  case class FixtureParam(localRepoConfiguration: GithubHookHandlerServiceConfiguration, localRepoName: String)

  def withFixture(test: OneArgTest): Outcome = {
    val repo: GitRepositoryMock = GitRepositoryMockFactory.create()
    val configuration: GithubHookHandlerServiceConfiguration = GithubHookHandlerServiceConfiguration(
      "123456",
      "emptytoken",
      repo.localRepository.toString,
      repo.localRepository.getBranch,
      repo.localRepoPath.toString
    )

    try {
      withFixture(
        test.toNoArgTest(
          FixtureParam(configuration, repo.localRepository.toString)
        )
      )
    } finally {
      repo.cleanUp()
    }
  }

  val jsonObj: JsObject = Json.obj("name" -> "Hans")

  val githubSignature: String =
    "sha1=2b5430390071a8965398808d77d4e7f9059a9470"

  "IsValidGithubMessage" should {

    "return true when a valid Github Hook message / gitHubSignature combination is provided" in { f: FixtureParam =>
      {
        val webHookMessage: GithubWebHookMessage =
          GithubWebHookMessage("master", f.localRepoName, jsonObj)
        new GitHubHookHandlerService(f.localRepoConfiguration)
          .isValidGithubMessage(webHookMessage, githubSignature)
      }
    }

    "return false when an invalid Github Hook message / gitHubSignature combination is provided" in { f: FixtureParam =>
      {
        val webHookMessage: GithubWebHookMessage =
          GithubWebHookMessage("master", f.localRepoName, jsonObj)
        new GitHubHookHandlerService(f.localRepoConfiguration)
          .isValidGithubMessage(webHookMessage, "")
      }
    }

  }

  "HandleGitHubMessage" should {

    "return a Failure when a valid Github Hook message and invalid githubSignature is provided" in { f: FixtureParam =>
      {
        val webHookMessage: GithubWebHookMessage =
          GithubWebHookMessage("master", f.localRepoName, jsonObj)

        val invalidStatusCode =
          new GitHubHookHandlerService(f.localRepoConfiguration)
            .handleGitHubMessage(webHookMessage, "")
        invalidStatusCode.isFailure shouldBe true
      }
    }

    "return a Success with a proper Github Hook message and proper Git settings" in { f: FixtureParam =>
      {

        val webHookMessage: GithubWebHookMessage =
          GithubWebHookMessage("master", f.localRepoName, jsonObj)

        val validStatusCode =
          new GitHubHookHandlerService(f.localRepoConfiguration)
            .handleGitHubMessage(webHookMessage, githubSignature)
        validStatusCode.isSuccess shouldBe true
      }
    }

    "return a Success with a proper Github Hook message and proper Git settings and SchedulerReloaded" in {
      f: FixtureParam =>
        {
          val initialScheduler: SchedulerService =
            SchedulerService.create(AppConfigurationStub.scheduler)

          val webHookMessage: GithubWebHookMessage =
            GithubWebHookMessage("master", f.localRepoName, jsonObj)

          val validStatusCode =
            new GitHubHookHandlerService(f.localRepoConfiguration)
              .handleGitHubMessage(webHookMessage, githubSignature)
          validStatusCode.isSuccess shouldBe true

          // Check whether the Scheduler has been reinitialized
          initialScheduler.equals(SchedulerService.getInstance) shouldBe false
        }
    }

    "return a Failure when Github Hook message / GithubSignature combination is invalid. The scheduler shouldn't be reloaded." in {
      f: FixtureParam =>
        {
          val initialScheduler: SchedulerService =
            SchedulerService.create(AppConfigurationStub.scheduler)

          val webHookMessage: GithubWebHookMessage =
            GithubWebHookMessage("master", f.localRepoName, jsonObj)

          val validStatusCode =
            new GitHubHookHandlerService(f.localRepoConfiguration)
              .handleGitHubMessage(webHookMessage, "")
          validStatusCode.isFailure shouldBe true

          // Check whether the Scheduler has been reinitialized
          initialScheduler.equals(SchedulerService.getInstance) shouldBe true
        }
    }

  }

  "isExpectedRepository" should {
    "succeed" in { f: FixtureParam =>
      {

        val webHookMessage: GithubWebHookMessage =
          GithubWebHookMessage("master", f.localRepoName, jsonObj)

        new GitHubHookHandlerService(f.localRepoConfiguration)
          .isExpectedRepository(webHookMessage) shouldBe true
      }
    }

    "fail" in { f: FixtureParam =>
      {

        val webHookMessage = GithubWebHookMessage("master", "", jsonObj)
        new GitHubHookHandlerService(f.localRepoConfiguration)
          .isExpectedRepository(webHookMessage) shouldBe false
      }
    }
  }

  "isExpectedBranch" should {
    "succeed" in { f: FixtureParam =>
      {
        val webHookMessage =
          GithubWebHookMessage("master", f.localRepoName, jsonObj)
        new GitHubHookHandlerService(f.localRepoConfiguration)
          .isExpectedBranchRef(webHookMessage) shouldBe true
      }
    }

    "fail" in { f: FixtureParam =>
      {
        val webHookMessage =
          GithubWebHookMessage("develop", f.localRepoName, jsonObj)
        new GitHubHookHandlerService(f.localRepoConfiguration)
          .isExpectedBranchRef(webHookMessage) shouldBe false

      }
    }
  }

}
