package com.simacan.testframework.orchestrator.webhook

import com.simacan.testframework.orchestrator.config.GithubHookHandlerServiceConfiguration
import org.eclipse.jgit.api.{Git, PullResult}
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

import scala.util.Try

class GitRepo(apiConfig: GithubHookHandlerServiceConfiguration) {

  private[webhook] def getLocalRepositoryFromPath(
    path: String
  ): FileRepository =
    new FileRepository(path + "/.git")

  private[orchestrator] def createLocalGitRepo(
    localRepository: FileRepository
  ): Git =
    new Git(localRepository)

  def tryPull(): Try[PullResult] =
    Try {
      createLocalGitRepo(
        getLocalRepositoryFromPath(apiConfig.localrepositorypath)
      ).pull()
        .call()
    }

  def tryPullWithAuthentication(): Try[PullResult] =
    Try {
      createLocalGitRepo(
        getLocalRepositoryFromPath(apiConfig.localrepositorypath)
      ).pull()
        .setCredentialsProvider(
          new UsernamePasswordCredentialsProvider(apiConfig.githubtoken, "")
        )
        .call()
    }

}
