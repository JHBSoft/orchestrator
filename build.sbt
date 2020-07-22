import sbtbuildinfo.BuildInfoOption._

val akkaHttpV = "10.1.11"
val akkaV = "2.6.4"
val authApiClientV = "5.3.0"
val baseLibV = "4.1.0"
val baseUtilsV = "4.1.0"
val bouncyCastleV = "1.64"
val monitoringUtilsV = "3.0.0"
val pureConfigV = "0.12.3"
val playJsonV = "2.8.1"
val jGitV = "[5.7,)"
val awsBatchSdkV = "1.11.761"
val quartzV = "2.3.2"
val scalaMockV = "4.4.0"
val scalaTestVersion = "3.1.1"
val scalaCheckV = "1.14.3"
val akkaStreamTestkitV = "2.6.4"
val akkaHttpTestkitV = "10.1.11"

lazy val testframeworkOrchestrator = project
  .in(file("."))
  .enablePlugins(ServicesParentPlugin)
  .settings(
    name := "testframework-orchestrator",
    scalaVersion := "2.12.10",
    libraryDependencies ++= Seq(
      "com.simacan" %% "monitoring-utils" % monitoringUtilsV,
      "com.simacan" %% "base-utils" % baseUtilsV,
      "com.simacan" %% "base-service-library" % baseLibV,
      "com.simacan" %% "auth-api-client" % authApiClientV,
      "com.typesafe.akka" %% "akka-actor" % akkaV,
      "com.typesafe.akka" %% "akka-stream" % akkaV,
      "com.typesafe.akka" %% "akka-slf4j" % akkaV,
      "com.typesafe.akka" %% "akka-http" % akkaHttpV,
      "com.typesafe.play" %% "play-json" % playJsonV,
      "com.github.pureconfig" %% "pureconfig" % pureConfigV,
      "org.eclipse.jgit" % "org.eclipse.jgit" % jGitV,
      "org.quartz-scheduler" % "quartz" % quartzV,
      "com.amazonaws" % "aws-java-sdk-batch" % awsBatchSdkV,
      "org.bouncycastle" % "bcprov-jdk15on" % bouncyCastleV,
    ),
    mainClass in Compile := Some("com.simacan.testframework.orchestrator.Main"),
    buildInfoOptions ++= Seq(
      BuildTime,
      Traits("com.simacan.base.about.BuildInfoCommon")
    ),
    // Test settings
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
      "org.scalamock" %% "scalamock" % scalaMockV % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaStreamTestkitV % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpTestkitV % Test,
    ),
    Test / testOptions += Tests.Argument("-l", "com.simacan.tags.CodeShipSkip"),
    fork in Test := true,
    envVars in Test := Map(
      "HTTP_PORT_GIT" -> "7000",
      "ENCRYPTION_KEY" -> "0102030405060708090a0b0c0d0e0f10",
      "GITHUB_HOOK" -> "123456",
      "ORCH_USER" -> "testuser",
      "ORCH_SECRET" -> "testpw")
  )
