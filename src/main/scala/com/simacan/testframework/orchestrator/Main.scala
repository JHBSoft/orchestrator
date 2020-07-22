package com.simacan.testframework.orchestrator

import akka.actor.ActorSystem
import akka.stream.Materializer
//import com.simacan.auth.client.CachedAkkaAuthClient

import com.simacan.base.{BaseMicroService, ServiceMain}
import com.simacan.base.auth.{AlwaysAuthorizedProvider, AuthorizationProvider}
import com.simacan.base.config.ConfigurationLoader
import com.simacan.base.about.BuildInfoCommon
import com.simacan.base_service_library.buildinfo.BuildInfo
import com.simacan.testframework.orchestrator.config.AppConfigLoader
import com.simacan.testframework.orchestrator.scheduler.{SchedulerApiRouteProvider, SchedulerService}
import com.simacan.testframework.orchestrator.webhook.GitHubHookApiRouteProvider
import com.simacan.testframework.orchestrator.doc.DocAPI

object Main extends ServiceMain {
  override def service: BaseMicroService = new Service()
}

class Service(implicit as: ActorSystem, mat: Materializer) extends BaseMicroService with ConfigurationLoader {

  override def buildInfo: BuildInfoCommon = BuildInfo
  private[this] val apiConfiguration = AppConfigLoader.loaded.config
  //private[this] val authClient = new CachedAkkaAuthClient(apiConfiguration.auth)

  //Start the scheduler
  SchedulerService.startNewWithTasks(apiConfiguration.scheduler)

  // Enable communication over two ports for public and non-public components.
  // This should be configured in the Load Balancers.
  // Public Available
  bindRoutes(
    Seq(new GitHubHookApiRouteProvider(apiConfiguration.githubHandler)),
    apiConfiguration.httpGithub
  )

  // Internal Available
  bindRoutes(
    Seq(new SchedulerApiRouteProvider(apiConfiguration.scheduler), new DocAPI),
    apiConfiguration.http
  )

  // This one is not used yet, later custom authentication will be added
  override def authorizationProvider: AuthorizationProvider = AlwaysAuthorizedProvider

}
