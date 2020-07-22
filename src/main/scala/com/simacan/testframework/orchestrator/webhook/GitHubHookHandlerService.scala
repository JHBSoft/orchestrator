package com.simacan.testframework.orchestrator.webhook

import com.simacan.testframework.orchestrator.config.GithubHookHandlerServiceConfiguration
import com.simacan.testframework.orchestrator.scheduler.SchedulerService
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.eclipse.jgit.api.PullResult
import org.slf4j.LoggerFactory

import scala.util.{Failure, Try}

/**
  * This Service handles the actions which are triggered by a Github Webhook message send to
  * the orchestrator. In essence it will confirm the received Webhook message is originating
  * from the defined Repository and Branch. If so, it will submit a pull request in the configured
  * local Git repository directory so the latest version is retrieved.
  *
  * @param apiConfiguration The configuration parameters for this Service
  */
class GitHubHookHandlerService(apiConfiguration: GithubHookHandlerServiceConfiguration) {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  private[this] val hashAlgorithm: String = "HmacSHA1"

  private[this] def signGithubMessage(
    githubMessage: GithubWebHookMessage
  ): String = {
    val mac = Mac.getInstance(hashAlgorithm)
    mac.init(
      new SecretKeySpec(apiConfiguration.githubhook.getBytes(), hashAlgorithm)
    )
    mac
      .doFinal(githubMessage.getRawJsonBytes)
      .map("%02x".format(_))
      .mkString
  }

  def isValidGithubMessage(githubMessage: GithubWebHookMessage, githubSignature: String): Boolean =
    s"sha1=${signGithubMessage(githubMessage)}" == githubSignature

  def isExpectedBranchRef(githubMessage: GithubWebHookMessage): Boolean =
    githubMessage.ref == apiConfiguration.gitbranchref

  def isExpectedRepository(githubMessage: GithubWebHookMessage): Boolean =
    githubMessage.repository == apiConfiguration.gitrepository

  def isValid(githubMessage: GithubWebHookMessage, githubSignature: String): Boolean =
    isValidGithubMessage(githubMessage, githubSignature) &&
      isExpectedBranchRef(githubMessage) &&
      isExpectedRepository(githubMessage)

  def handleGitHubMessage(githubMessage: GithubWebHookMessage, githubSignature: String): Try[PullResult] =
    if (isValid(githubMessage, githubSignature)) {
      val gitRepoPull: Try[PullResult] =
        new GitRepo(apiConfiguration).tryPullWithAuthentication()

      if (gitRepoPull.isSuccess) SchedulerService.tryRestartWithScheduleCheck()

      gitRepoPull
    } else {
      logger.warn("invalid githubhook message received")
      new Failure[PullResult](InvalidMessageException("Message not accepted"))
    }

//      gitRepoPull match {
//        case Success(_) =>
//          logger.info("Data pulled from Github")
//
//          // Restart the scheduler to load new schedule file
//          SchedulerService.tryRestartWithScheduleCheck()
//
//          (StatusCodes.OK, "Handled")
//        case Failure(exception) =>
//          logger.error(exception.getMessage)
//          logger.warn(apiConfiguration.toString)
//
//          exception match {
//            case ioe: IOException =>
//              (StatusCodes.InternalServerError, ioe.getMessage)
//            case gae: GitAPIException =>
//              (StatusCodes.InternalServerError, gae.getMessage)
//            case ex: Exception =>
//              (StatusCodes.InternalServerError, ex.getMessage)
//            case _: Throwable =>
//              (StatusCodes.NotImplemented, "Unexpected Error")
//          }
//      }
//    } else {
//      logger.warn("invalid githubhook message received")
//      (StatusCodes.Forbidden, "Message not accepted")
//    }

}

object GitHubHookHandlerService {
  val signatureHeader: String = "X-Hub-Signature"
}
