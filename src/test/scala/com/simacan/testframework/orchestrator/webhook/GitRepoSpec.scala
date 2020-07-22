package com.simacan.testframework.orchestrator.webhook

import java.io.File

import com.simacan.testframework.orchestrator.config.GithubHookHandlerServiceConfiguration
import org.scalatest.wordspec.FixtureAnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.Outcome

class GitRepoSpec extends FixtureAnyWordSpec with Matchers {

  case class FixtureParam(gitRepoMock: GitRepositoryMock)

  def withFixture(test: OneArgTest): Outcome = {
    val repo: GitRepositoryMock = GitRepositoryMockFactory.create()

    try {
      withFixture(test.toNoArgTest(FixtureParam(repo)))
    } finally {
      repo.cleanUp()
    }
  }

  "return false if configuration is not correct" in { f: FixtureParam =>
    lazy val localRepoConfiguration: GithubHookHandlerServiceConfiguration =
      GithubHookHandlerServiceConfiguration(
        "123456",
        "emptytoken",
        f.gitRepoMock.localRepository.toString,
        f.gitRepoMock.localRepository.getBranch,
        ""
      )

    val gitRepo =
      new GitRepo(localRepoConfiguration)
    gitRepo.tryPull().isSuccess shouldBe false
  }

  "return true because everything is correct" in { f: FixtureParam =>
    lazy val localRepoConfiguration: GithubHookHandlerServiceConfiguration =
      GithubHookHandlerServiceConfiguration(
        "123456",
        "emptytoken",
        f.gitRepoMock.localRepository.toString,
        f.gitRepoMock.localRepository.getBranch,
        f.gitRepoMock.localRepoPath.toString
      )

    //Add a file to Remote Repo
    val newRemoteFile =
      File.createTempFile("test", "", f.gitRepoMock.remoteRepoPath)
    f.gitRepoMock
      .stageAndCommitFileToRepository(
        f.gitRepoMock.remoteRepository,
        newRemoteFile,
        "testcommit"
      )

    val pulledLocalFile =
      new File(f.gitRepoMock.localRepoPath + "/" + newRemoteFile.getName)

    pulledLocalFile should not(exist)
    new GitRepo(localRepoConfiguration).tryPull().isSuccess shouldBe true
    pulledLocalFile should exist

  }

}
