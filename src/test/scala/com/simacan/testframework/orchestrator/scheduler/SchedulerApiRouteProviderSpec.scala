package com.simacan.testframework.orchestrator.scheduler

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.simacan.base.auth.AlwaysAuthorizedProvider
import com.simacan.testframework.orchestrator.config.AppConfigurationStub
import com.simacan.testframework.orchestrator.generic.EncryptionUtils
import com.simacan.testframework.orchestrator.helpers._
import com.simacan.testframework.orchestrator.scheduler.messages._
import com.simacan.testframework.orchestrator.scheduler.model.{TaskDescription, TaskDescriptionFile}
import com.simacan.testframework.orchestrator.scheduler.tasks.TaskDescriptionFileLoader
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.scalatest.Outcome
import org.scalatest.wordspec.FixtureAnyWordSpec
import org.scalatest.matchers.should.Matchers

class SchedulerApiRouteProviderSpec
    extends FixtureAnyWordSpec
    with Matchers
    with PlayJsonSupport
    with ScalatestRouteTest {

  case class FixtureParam(schedulerService: SchedulerService)

  def withFixture(test: OneArgTest): Outcome = {
    val fixtureData: FixtureParam = FixtureParam(
      SchedulerService
        .create(AppConfigurationStub.scheduler)
    )

    try {
      withFixture(test.toNoArgTest(fixtureData))
    } finally {
      fixtureData.schedulerService.stop()
      SchedulerService.destroy()
    }
  }

  // Start scheduler and create SchedulerApiRouteProvider

  val properCredentials: BasicHttpCredentials =
    BasicHttpCredentials(AppConfigurationStub.scheduler.auth.username, AppConfigurationStub.scheduler.auth.password)
  val wrongCredentials: BasicHttpCredentials = BasicHttpCredentials("notright", "wrongagain")

  val schedulerRoutes: Route =
    new SchedulerApiRouteProvider(AppConfigurationStub.scheduler).route(AlwaysAuthorizedProvider)

  "The SchedulerApiRouteProvider (generic responses)" should {

    "return  a 404 Not Found for the root path" in { _ =>
      // tests:
      Get() ~> Route.seal(schedulerRoutes) ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    "leave GET requests to other paths unhandled,  return 404" in { _ =>
      // tests:
      Get("/kermit") ~> Route.seal(schedulerRoutes) ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    "return a NotFound" in { _ =>
      // tests:
      Put() ~> Route.seal(schedulerRoutes) ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }
  }

  "SchedulerApiRouteProvider job-count call" should {
    "return an StatusCode Unauthorized with improper credentials" in { fp =>
      fp.schedulerService.setTasks()
      Get("/api/v1/scheduler/job-count") ~> addCredentials(wrongCredentials) ~> Route.seal(schedulerRoutes) ~> check {
        status shouldEqual StatusCodes.Unauthorized
      }
    }

    "return jobCount = 5 with proper setup" in { fp =>
      fp.schedulerService.setTasks()
      Get("/api/v1/scheduler/job-count") ~> addCredentials(properCredentials) ~> schedulerRoutes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldBe ContentTypes.`application/json`
        responseAs[JobCountResponse].jobCount shouldBe 5
      }
    }

    "return jobCount = 0 with scheduler started but no tasks set" in { _ =>
      Get("/api/v1/scheduler/job-count") ~> addCredentials(properCredentials) ~> schedulerRoutes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[JobCountResponse].jobCount shouldBe 0
        contentType shouldBe ContentTypes.`application/json`
      }
    }

    "return InternalServerError and JobCount = 0 when scheduler is not created" in { _ =>
      SchedulerService.destroy()
      Get("/api/v1/scheduler/job-count") ~> addCredentials(properCredentials) ~> schedulerRoutes ~> check {
        status shouldEqual StatusCodes.InternalServerError
        responseAs[JobCountResponse].jobCount shouldBe 0
        contentType shouldBe ContentTypes.`application/json`
      }
    }

  }

  "SchedulerApiRouteProvider.get-all-tasks" should {

    "return an StatusCode Unauthorized with improper credentials" in { fp =>
      fp.schedulerService.setTasks()
      Get("/api/v1/scheduler/get-all-tasks") ~> addCredentials(wrongCredentials) ~> Route.seal(schedulerRoutes) ~> check {
        status shouldEqual StatusCodes.Unauthorized
      }
    }

    "return success and all tasks of scheduler" in { fp =>
      fp.schedulerService.setTasks()

      Get("/api/v1/scheduler/get-all-tasks") ~> addCredentials(properCredentials) ~> schedulerRoutes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldBe ContentTypes.`application/json`

        val allTasksResponse: AllTasksResponse = responseAs[AllTasksResponse]
        allTasksResponse.tasks should have size 5

        allTasksResponse.tasks.head.gatlingJob.get.jobTemplate.get shouldBe "PTC_MegaSim"
        allTasksResponse
          .tasks(1)
          .gatlingJob
          .get
          .jobTemplate
          .get shouldBe "Perf-JobDef"
      }
    }

    "return success and empty tasks list with no tasks set" in { _ =>
      Get("/api/v1/scheduler/get-all-tasks") ~> addCredentials(properCredentials) ~> schedulerRoutes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldBe ContentTypes.`application/json`

        val allTasksResponse: AllTasksResponse = responseAs[AllTasksResponse]
        allTasksResponse.tasks should have size 0
      }
    }

    "return proper jobTemplate" in { fp =>
      fp.schedulerService.setTasks(
        TaskDescriptionFileLoader.loadSchedule("jobScheduleWithOptData.json")
      )

      Get("/api/v1/scheduler/get-all-tasks") ~> addCredentials(properCredentials) ~> schedulerRoutes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldBe ContentTypes.`application/json`

        val allTasksResponse: AllTasksResponse = responseAs[AllTasksResponse]
        allTasksResponse.tasks should have size 5

        allTasksResponse.tasks.head.gatlingJob.get.jobTemplate.get shouldBe "PTC_MegaSim"
        allTasksResponse
          .tasks(1)
          .gatlingJob
          .get
          .jobTemplate
          .get shouldBe "MyDummyTemplate"
      }

    }

    "return error and empty tasks list with scheduler not initialized" in { _ =>
      SchedulerService.destroy()
      Get("/api/v1/scheduler/get-all-tasks") ~> addCredentials(properCredentials) ~> schedulerRoutes ~> check {
        status shouldEqual StatusCodes.InternalServerError
        contentType shouldBe ContentTypes.`application/json`

        val allTasksResponse: AllTasksResponse = responseAs[AllTasksResponse]
        allTasksResponse.tasks should have size 0
      }

    }
  }

  "SchedulerApiRouteProvider Restart call" should {

    "return an StatusCode Unauthorized with improper credentials" in { fp =>
      fp.schedulerService.setTasks()
      Get("/api/v1/scheduler/restart") ~> addCredentials(wrongCredentials) ~> Route.seal(schedulerRoutes) ~> check {
        status shouldEqual StatusCodes.Unauthorized
      }
    }

    "return Success and jobcount of new scheduler without JobSchedule specification" in { fp =>
      val firstScheduler: SchedulerService = fp.schedulerService
      firstScheduler.setTasks()

      Post(
        "/api/v1/scheduler/restart",
        HttpEntity(MediaTypes.`application/json`, ByteString(""))
      ) ~> addCredentials(properCredentials) ~> schedulerRoutes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[JobCountResponse].jobCount shouldBe 5
        contentType shouldBe ContentTypes.`application/json`

        // Test Scheduler has been restarted
        firstScheduler should not equal SchedulerService.getInstance
      }
    }

    "return Success and jobcount of new scheduler with JobSchedule2 specification" in { fp =>
      val firstScheduler: SchedulerService = fp.schedulerService
      firstScheduler.setTasks()

      Post(
        "/api/v1/scheduler/restart",
        HttpEntity(
          MediaTypes.`application/json`,
          ByteString("{\"scheduleFile\":\"jobSchedule2.json\"}")
        )
      ) ~> addCredentials(properCredentials) ~> schedulerRoutes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[JobCountResponse].jobCount shouldBe 1
        contentType shouldBe ContentTypes.`application/json`

        // Test Scheduler has been restarted
        firstScheduler should not equal SchedulerService.getInstance
      }
    }

    "return InternalServerError and jobcount = 0  when scheduler is not initialized with parameter" in { _ =>
      SchedulerService.destroy()

      Post(
        "/api/v1/scheduler/restart",
        HttpEntity(
          MediaTypes.`application/json`,
          ByteString("{\"scheduleFile\":\"jobSchedule2.json\"}")
        )
      ) ~> addCredentials(properCredentials) ~> schedulerRoutes ~> check {
        status shouldEqual StatusCodes.InternalServerError
        responseAs[JobCountResponse].jobCount shouldBe 0
        contentType shouldBe ContentTypes.`application/json`

        // Test Scheduler has been restarted
        SchedulerService.getInstance shouldBe null

      }
    }

    "return InternalServerError and jobcount = 0  when scheduler is not initialized without parameter" in { _ =>
      SchedulerService.destroy()

      Post("/api/v1/scheduler/restart") ~> addCredentials(properCredentials) ~> schedulerRoutes ~> check {
        status shouldEqual StatusCodes.InternalServerError
        responseAs[JobCountResponse].jobCount shouldBe 0
        contentType shouldBe ContentTypes.`application/json`

        // Test Scheduler has been restarted
        SchedulerService.getInstance shouldBe null

      }
    }

  }

  "SchedulerApiRouteProvider submit-gatling-job call" should {

    "return an StatusCode Unauthorized with improper credentials" in { _ =>
      Post(
        "/api/v1/scheduler/submit-gatling-job",
        HttpEntity(
          MediaTypes.`application/json`,
          """ { "name": "test script1:1", "gatlingJob" : {"script" : "testscript" } } """
        )
      ) ~> addCredentials(wrongCredentials) ~> Route.seal(schedulerRoutes) ~> check {
        status shouldEqual StatusCodes.Unauthorized
      }
    }

    "return a success and TaskDescription with proper gatling script and default AWS JobTemplate and Queue" taggedAs (AwsConnectedTag, CodeShipSkip) in {
      _ =>
        Post(
          "/api/v1/scheduler/submit-gatling-job",
          HttpEntity(
            MediaTypes.`application/json`,
            """ { "name": "test script1:1", "gatlingJob" : {"script" : "testscript" } } """
          )
        ) ~> addCredentials(properCredentials) ~> schedulerRoutes ~> check {
          status shouldEqual StatusCodes.OK
          val taskDescription = responseAs[TaskDescription]
          taskDescription.name shouldBe "test_script11"
          taskDescription.gatlingJob.get.script shouldBe "testscript"
          taskDescription.gatlingJob.get.jobTemplate
            .getOrElse(None) shouldBe "Perf-JobDef"
          taskDescription.gatlingJob.get.jobQueue
            .getOrElse(None) shouldBe "BatchJQ"
        }
    }

    "return a success and TaskDescription with proper gatling script and default name" taggedAs (AwsConnectedTag, CodeShipSkip) in {
      _ =>
        Post(
          "/api/v1/scheduler/submit-gatling-job",
          HttpEntity(
            MediaTypes.`application/json`,
            """ { "gatlingJob" : {"script" : "testscript" } } """
          )
        ) ~> addCredentials(properCredentials) ~> schedulerRoutes ~> check {
          status shouldEqual StatusCodes.OK
          val taskDescription = responseAs[TaskDescription]
          taskDescription.name should startWith("Task")
          taskDescription.gatlingJob.get.script shouldBe "testscript"
          taskDescription.gatlingJob.get.jobTemplate
            .getOrElse(None) shouldBe "Perf-JobDef"
          taskDescription.gatlingJob.get.jobQueue
            .getOrElse(None) shouldBe "BatchJQ"
        }
    }

  }

  "SchedulerApiRouteProvider.encrypt-keys" should {

    "return an StatusCode Unauthorized with improper credentials" in { _ =>
      Post(
        "/api/v1/scheduler/encrypt-keys",
        HttpEntity(
          MediaTypes.`application/json`,
          """ { "items" : [ { "value" : "first value" }, {"name" : "name1", "value" : "second value" } ] }  """
        )
      ) ~> addCredentials(wrongCredentials) ~> Route.seal(schedulerRoutes) ~> check {
        status shouldEqual StatusCodes.Unauthorized
      }
    }

    "return all values encrypted" in { _ =>
      Post(
        "/api/v1/scheduler/encrypt-keys",
        HttpEntity(
          MediaTypes.`application/json`,
          """ { "items" : [ { "value" : "first value" }, {"name" : "name1", "value" : "second value" } ] }  """
        )
      ) ~> addCredentials(properCredentials) ~> schedulerRoutes ~> check {
        status shouldEqual StatusCodes.OK
        val responseValue = responseAs[EncryptKeysRequestResponse]
        responseValue.items should have size 2

        EncryptionUtils
          .decrypt(responseValue.items.head.value)
          .get shouldBe "first value"
        responseValue.items.head.name shouldBe empty
        EncryptionUtils
          .decrypt(responseValue.items(1).value)
          .get shouldBe "second value"
        responseValue.items(1).name.get shouldBe "name1"
      }
    }
  }

  "SchedulerApiRouteProvider validate-jobschedule-json" should {

    "return an StatusCode Unauthorized with improper credentials" in { _ =>
      Post(
        "/api/v1/scheduler/validate-jobschedule-json",
        HttpEntity(MediaTypes.`application/json`, TaskDescriptionFileStub.text)
      ) ~> addCredentials(wrongCredentials) ~> Route.seal(schedulerRoutes) ~>
        check {
          status shouldEqual StatusCodes.Unauthorized
        }
    }

    "return OK" in { _ =>
      Post(
        "/api/v1/scheduler/validate-jobschedule-json",
        HttpEntity(MediaTypes.`application/json`, TaskDescriptionFileStub.text)
      ) ~> addCredentials(properCredentials) ~> schedulerRoutes ~>
        check {
          status shouldEqual StatusCodes.OK
        }
    }

    "return an error" in { _ =>
      Post(
        "/api/v1/scheduler/validate-jobschedule-json",
        HttpEntity(
          MediaTypes.`application/json`,
          TaskDescriptionFileStub.invalidText
        )
      ) ~> addCredentials(properCredentials) ~> schedulerRoutes ~>
        check {
          status shouldEqual StatusCodes.BadRequest
          responseAs[String] should include("Invalid message received: ")
        }
    }

    "return wrong tasks" in { _ =>
      Post(
        "/api/v1/scheduler/validate-jobschedule-json",
        HttpEntity(
          MediaTypes.`application/json`,
          TaskDescriptionFileStub.invalidCronText
        )
      ) ~> addCredentials(properCredentials) ~> schedulerRoutes ~>
        check {
          status shouldEqual StatusCodes.NotAcceptable

          val responseData = responseAs[TaskDescriptionFile]
          responseData.tasks.head.cron shouldBe "0 */5 * * * * *"
          responseData.tasks.size shouldBe 1
        }

    }
  }

  "SchedulerApiRouteProvider get-all-crondata" should {
    "return a list with all crondata of scheduled jobs" in { fp =>
      fp.schedulerService.setTasks()

      Get("/api/v1/scheduler/get-all-crondata") ~> addCredentials(properCredentials) ~> schedulerRoutes ~> check {
        status shouldBe StatusCodes.OK
        val allCronData: GetAllCronDataResponse =
          responseAs[GetAllCronDataResponse]

        allCronData.tasks should have size 5
        allCronData.tasks.head.task shouldBe "task1"
        allCronData.tasks.head.cronExpression shouldBe "*/2 * * * * ? *"
      }
    }
  }

  "SchedulerApiRouteProvider" should {
    "handle incorrect messages correctly" in { _ =>
      Post(
        "/api/v1/scheduler/validate-jobschedule-json",
        HttpEntity(
          MediaTypes.`application/json`,
          TaskDescriptionFileStub.invalidText
        )
      ) ~> addCredentials(properCredentials) ~> schedulerRoutes ~>
        check {
          status shouldEqual StatusCodes.BadRequest
          responseAs[String] should include("Invalid message received: ")
        }
    }

    "handle malformed messages correctly" in { _ =>
      Post(
        "/api/v1/scheduler/encrypt-keys",
        HttpEntity(
          MediaTypes.`application/json`,
          """ { "value" : "first value" }, {"name" : "name1", "value" : "second value" } """
        )
      ) ~> addCredentials(properCredentials) ~> schedulerRoutes ~>
        check {
          status shouldEqual StatusCodes.BadRequest
          responseAs[String] should include("Malformed message received: ")
        }
    }

  }

  "SchedulerApiRouteProvider" should {
    "return the swagger file" in { _ =>
      Get("/api/v1/scheduler/swagger.yaml") ~> schedulerRoutes ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }
}
