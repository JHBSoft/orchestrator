package com.simacan.testframework.orchestrator.webhook

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.simacan.base.auth.AlwaysAuthorizedProvider
import com.simacan.testframework.orchestrator.config.AppConfigLoader
import com.simacan.testframework.orchestrator.helpers._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GitHubHookApiRouteProviderSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  val githubHookServiceRoutes: Route =
    new GitHubHookApiRouteProvider(AppConfigLoader.loaded.config.githubHandler)
      .route(AlwaysAuthorizedProvider)

  "The GitHubHookApiRouteProvider" should {

    "return 404 Not Found for GET requests to the root path" in {
      // tests:
      Get() ~> Route.seal(githubHookServiceRoutes) ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    "GET requests to other paths return 404 Not Found" in {
      // tests:
      Get("/kermit") ~> Route.seal(githubHookServiceRoutes) ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    "return a 403 (Forbidden) when no incorrect data send" in {
      // tests:
      val headers =
        RawHeader("X-Hub-Signature", MessageBodyStubs.faulty.signature)

      Post(
        "/api/v1/github",
        HttpEntity(
          MediaTypes.`application/json`,
          MessageBodyStubs.faulty.byteString
        )
      ).withHeaders(headers) ~> Route
        .seal(githubHookServiceRoutes) ~> check {
        status shouldEqual StatusCodes.Forbidden
      }
    }

    "return a 200 when correct data send" taggedAs (CodeShipSkip, GitHubConnectedTag) in {
      //GithubHook used: GITHUB_HOOK=123456
      // tests:
      val headers =
        RawHeader("X-Hub-Signature", MessageBodyStubs.correct.signature)

      Post(
        "/api/v1/github",
        HttpEntity(
          MediaTypes.`application/json`,
          MessageBodyStubs.correct.byteString
        )
      ).withHeaders(headers) ~> Route
        .seal(githubHookServiceRoutes) ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

  }
}
