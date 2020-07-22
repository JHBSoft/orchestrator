package com.simacan.testframework.orchestrator.webhook

import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.simacan.base.auth.AuthorizationProvider
import com.simacan.base.http.RouteProvider
import com.simacan.testframework.orchestrator.config.GithubHookHandlerServiceConfiguration
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

import scala.util.{Failure, Success}

class GitHubHookApiRouteProvider(apiConfig: GithubHookHandlerServiceConfiguration)
    extends RouteProvider
    with PlayJsonSupport {

  private[this] def extractExampleHeader(
    expectedHeader: String
  ): HttpHeader => Option[String] = {
    case HttpHeader(actualHeader, value) if actualHeader == expectedHeader =>
      Some(value)
    case _ => None
  }

  override def route(authorizationProvider: AuthorizationProvider): Route =
    pathPrefix("api" / "v1") {
      path("github") {
        (post & entity(as[GithubWebHookMessage])) { data =>
          headerValue(
            extractExampleHeader(
              GitHubHookHandlerService.signatureHeader.toLowerCase()
            )
          ) { value =>
            new GitHubHookHandlerService(apiConfig)
              .handleGitHubMessage(data, value) match {
              case Success(_) => complete("Handled")
              case Failure(e) =>
                complete(e match {
                  case invalidMessageException: InvalidMessageException =>
                    (StatusCodes.Forbidden, invalidMessageException.getMessage)
                  case _: Exception => (StatusCodes.InternalServerError, e.getMessage)
                })
              //          }
              //      }
              //    } else {
              //      logger.warn("invalid githubhook message received")
              //      (StatusCodes.Forbidden, "Message not accepted")
              //    }

            }

          }
        }
      }
    }
}
