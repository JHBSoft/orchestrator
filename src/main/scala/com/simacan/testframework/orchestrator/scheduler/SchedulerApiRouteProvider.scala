package com.simacan.testframework.orchestrator.scheduler

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.Credentials
import com.amazonaws.services.batch.model.SubmitJobResult
import com.simacan.base.auth.AuthorizationProvider
import com.simacan.base.http.RouteProvider
import com.simacan.testframework.orchestrator.config.SchedulerServiceConfiguration
import com.simacan.testframework.orchestrator.scheduler.messages._
import com.simacan.testframework.orchestrator.scheduler.model._
import com.simacan.testframework.orchestrator.scheduler.tasks._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

import scala.util.{Failure, Success, Try}

class SchedulerApiRouteProvider(
  schedulerServiceConfig: SchedulerServiceConfiguration
) extends RouteProvider
    with PlayJsonSupport {

  private val rejectionHandler: RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle {
        case ValidationRejection(msg, _) =>
          complete((StatusCodes.BadRequest, "Invalid message received: " + msg))
        case MalformedRequestContentRejection(msg, _) =>
          complete(
            (StatusCodes.BadRequest, "Malformed message received: " + msg)
          )
      }
      .result()

  def myUserPassAuthenticator(credentials: Credentials): Option[String] =
    credentials match {
      case p @ Credentials.Provided(id)
          if id == schedulerServiceConfig.auth.username && p.verify(schedulerServiceConfig.auth.password) =>
        Some(id)
      case _ => None
    }

  override def excludeContentSecurityPolicyHeader: Boolean = true

  override def route(authorizationProvider: AuthorizationProvider): Route = cors() {
    pathPrefix("api" / "v1" / "scheduler") {
      path("swagger.yaml") {
        get {
          getFromResource("api/swagger.yaml")
        }
      } ~
        handleRejections(rejectionHandler) {
          authenticateBasic(realm = schedulerServiceConfig.auth.realm, myUserPassAuthenticator) { userName =>
            path("job-count") {
              get {
                Try {
                  JobCountResponse(SchedulerService.getInstance.getJobCount)
                } match {
                  case Success(v) => complete(v)
                  case Failure(_) =>
                    complete((StatusCodes.InternalServerError, JobCountResponse(0)))
                }
              }
            } ~
              path("restart") {
                (post & entity(as[RestartRequest])) { data =>
                  SchedulerService.tryRestartWithScheduleCheck(data.scheduleFile) match {
                    case Success(v) => complete(JobCountResponse(v.getJobCount))
                    case Failure(_) => complete((StatusCodes.InternalServerError, JobCountResponse(0)))
                  }

                } ~
                  post {
                    SchedulerService.tryRestartWithScheduleCheck() match {
                      case Success(v) => complete(JobCountResponse(v.getJobCount))
                      case Failure(_) => complete((StatusCodes.InternalServerError, JobCountResponse(0)))
                    }
                  }
              } ~
              path("get-all-tasks") {
                get {
                  Try {
                    AllTasksResponse(SchedulerService.getInstance.getAllTaskData)
                  } match {
                    case Success(v) => complete(v)
                    case Failure(_) =>
                      complete(
                        (
                          StatusCodes.InternalServerError,
                          AllTasksResponse(Seq.empty[TaskDescription])
                        )
                      )
                  }
                }
              } ~
              path("submit-gatling-job") {
                (post & entity(as[SubmitGatlingJobRequest])) { data =>
                  val awsBatchTask: Task[_ <: Task[_]] =
                    TaskFactory
                      .createFromTaskDescription(
                        data.convertToTaskDescription(),
                        schedulerServiceConfig
                      )

                  val submitResult: Try[SubmitJobResult] =
                    awsBatchTask.asInstanceOf[AwsBatchTask].submitJob()

                  submitResult match {
                    case Success(_) =>
                      complete(awsBatchTask.taskDescription)
                    case Failure(e) =>
                      complete((StatusCodes.NotAcceptable, e.getMessage))
                  }
                }
              } ~
              path("encrypt-keys") {
                (post & entity(as[EncryptKeysRequestResponse])) { data =>
                  complete(data.encrypted)
                }
              } ~
              path("validate-jobschedule-json") {
                (post & entity(as[TaskDescriptionFile])) { data: TaskDescriptionFile =>
                  val invalidTasks: TaskDescriptionFile = data.validateContent
                  if (invalidTasks.tasks.isEmpty)
                    complete(data)
                  else complete((StatusCodes.NotAcceptable, invalidTasks))
                }
              } ~
              path("get-all-crondata") {
                get {
                  complete(SchedulerService.getInstance.getAllCronData)
                }
              }
          }
        }
    }

  }

}
