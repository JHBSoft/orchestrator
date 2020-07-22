package com.simacan.testframework.orchestrator.scheduler.messages

object TaskDescriptionFileStub {

  val text: String =
    """
      |{
      |  "defaultJobTemplate": "ptc-gatlingrunner:10",
      |  "defaultJobQueue": "simacan-performance-testing-job-queue",
      |  "tasks": [
      |    {
      |      "name": "PCNLGApiSimulation",
      |      "cron": "0 */5 * * * ? *",
      |      "gatlingJob": {
      |        "script": "com.simacan.services.postcodenlgeocoder.PCNLGApiSimulation",
      |        "description": "PCNLGApiSimulation",
      |        "options": [
      |          {
      |            "name": "AUTH_API_URI",
      |            "value": "https://auth-service-dev.services.simacan.com"
      |          },
      |          {
      |            "name": "GS_USERCOUNT",
      |            "value": "2"
      |          },
      |          {
      |            "name": "GS_REPEATCOUNT",
      |            "value": "5"
      |          }
      |        ]
      |      }
      |    },
      |    {
      |      "name": "MegaSimRun",
      |      "cron": "0 */5 * * * ? *",
      |      "gatlingJob": {
      |        "script": "com.simacan.sct.MegaSim",
      |        "description": "MegaSimRun",
      |        "options": [
      |          {
      |            "name": "AUTH_API_URI",
      |            "value": "https://auth-service-dev.services.simacan.com"
      |          },
      |          {
      |            "name": "GS_USERCOUNT",
      |            "value": "2"
      |          },
      |          {
      |            "name": "GS_REPEATCOUNT",
      |            "value": "5"
      |          },
      |          { "name": "GENERIC_REPEAT_COUNT",
      |            "value":  "5"
      |          },
      |          {"name": "USER_LOAD_TEST_PASSWORD",
      |          "value":  "v1:01ddac712bed66b58cebcb2e64adc54a:97149ba59f0fb6dbee4ce206f1810d69959e64b569e992a90fe0d145487406ba",
      |          "encrypted" :  true},
      |          {"name": "GHOST_USER_SECRET",
      |          "value": "v1:d9fdff18f293c8bc6cee94dd97401414:e025d989dc00cc53d7cfb063da32ee658b957260bf951d6650d4ea935e524067b457ee20b4b41eda6865c3789cde6ddc",
      |          "encrypted": true},
      |          {"name":  "GENERIC_RAMP_UP_USER",
      |          "value":  "1"},
      |          {"name": "GENERIC_INITIAL_USER",
      |          "value": "1"},
      |          {"name": "USER_LOAD_TEST_USERNAME",
      |          "value":  "control-tower-load-tester@simacan.com"},
      |          {"name": "GHOST_USER_PASSWORD",
      |          "value": "v1:79f55b955b2bb7ea333b579b37dfba85:7ed18d03ad1d2cb58a812ceb258cd6913da6502fbc6eb4352b7dd326d1eba6ac",
      |          "encrypted":  true},
      |          {"name":  "SCT_WEB_API_URI",
      |          "value": "https://sct-web-api-dev.simacan.com"},
      |          {"name": "GHOST_USER_USERNAME",
      |          "value": "ghost.user@simacan.com"},
      |          {"name" :  "RAMP_UP_DURATION",
      |          "value": "5"}
      |        ]
      |      }
      |    },
      |    {
      |      "name": "ptc-refreshtoken",
      |      "cron": "0 */10 * * * ? *",
      |      "gatlingJob": {
      |        "script": "com.simacan.services.authapi.AuthApiSimulation",
      |        "description": "Refresh Token Call",
      |        "options": [
      |          {
      |            "name": "AUTH_API_REALMS",
      |            "value": "1"
      |          },
      |          {
      |            "name": "AUTH_API_PROFS",
      |            "value": "1"
      |          },
      |          {
      |            "name": "AUTH_API_USERS",
      |            "value": "1"
      |          }
      |        ]
      |      }
      |    },
      |    {
      |      "name": "TestTask",
      |      "cron": "0 */5 * * * ? *",
      |      "shellJob": "shell3"
      |    }
      |  ]
      |}
      |""".stripMargin

  val invalidText: String =
    """
      |{
      |  "defaultJobTemplate": "ptc-gatlingrunner:10",
      |  "defaultJobQueue": "simacan-performance-testing-job-queue",
      |  "tasks": [
      |    {
      |      "name": "PCNLGApiSimulation",
      |      "cron": "0 */5 * * * ? *",
      |      "gatlingJob": {
      |        "script": "com.simacan.services.postcodenlgeocoder.PCNLGApiSimulation",
      |        "description": "PCNLGApiSimulation",
      |        "options": [
      |          {
      |            "name": "AUTH_API_URI",
      |            "value": "https://auth-service-dev.services.simacan.com"
      |          },
      |          {
      |            "name": "GS_USERCOUNT",
      |            "value": "2"
      |          },
      |          {
      |            "name": "GS_REPEATCOUNT",
      |            "value": "5"
      |          }
      |        ]
      |      }
      |    },
      |    {
      |      "name": "MegaSimRun",
      |      "cron": "0 */5 * * * ? *",
      |      "gatlingJob": {
      |        "script": "com.simacan.sct.MegaSim",
      |        "description": "MegaSimRun",
      |        "options": [
      |          {
      |            "AUTH_API_URI": "AUTH_API_URI",
      |            "value": "https://auth-service-dev.services.simacan.com"
      |          },
      |          {
      |            "name": "GS_USERCOUNT",
      |            "value": "2"
      |          },
      |          {
      |            "name": "GS_REPEATCOUNT",
      |            "value": "5"
      |          },
      |          { "name": "GENERIC_REPEAT_COUNT",
      |            "value":  "5"
      |          },
      |          {"name": "USER_LOAD_TEST_PASSWORD",
      |          "value":  "v1:01ddac712bed66b58cebcb2e64adc54a:97149ba59f0fb6dbee4ce206f1810d69959e64b569e992a90fe0d145487406ba",
      |          "encrypted" :  true},
      |          {"name": "GHOST_USER_SECRET",
      |          "value": "v1:d9fdff18f293c8bc6cee94dd97401414:e025d989dc00cc53d7cfb063da32ee658b957260bf951d6650d4ea935e524067b457ee20b4b41eda6865c3789cde6ddc",
      |          "encrypted": true},
      |          {"name":  "GENERIC_RAMP_UP_USER",
      |          "value":  "1"},
      |          {"name": "GENERIC_INITIAL_USER",
      |          "value": "1"},
      |          {"name": "USER_LOAD_TEST_USERNAME",
      |          "value":  "control-tower-load-tester@simacan.com"},
      |          {"name": "GHOST_USER_PASSWORD",
      |          "value": "v1:79f55b955b2bb7ea333b579b37dfba85:7ed18d03ad1d2cb58a812ceb258cd6913da6502fbc6eb4352b7dd326d1eba6ac",
      |          "encrypted":  true},
      |          {"name":  "SCT_WEB_API_URI",
      |          "value": "https://sct-web-api-dev.simacan.com"},
      |          {"name": "GHOST_USER_USERNAME",
      |          "value": "ghost.user@simacan.com"},
      |          {"name" :  "RAMP_UP_DURATION",
      |          "value": "5"}
      |        ]
      |      }
      |    },
      |    {
      |      "name": "ptc-refreshtoken",
      |      "cron": "0 */10 * * * ? *",
      |      "gatlingJob": {
      |        "script": "com.simacan.services.authapi.AuthApiSimulation",
      |        "description": "Refresh Token Call",
      |        "options": [
      |          {
      |            "name": "AUTH_API_REALMS",
      |            "value": "1"
      |          },
      |          {
      |            "name": "AUTH_API_PROFS",
      |            "value": "1"
      |          },
      |          {
      |            "name": "AUTH_API_USERS",
      |            "value": "1"
      |          }
      |        ]
      |      }
      |    },
      |    {
      |      "name": "TestTask",
      |      "cron": "0 */5 * * * ? *",
      |      "shellJob": "shell3"
      |    }
      |  ]
      |}
      |""".stripMargin

  // Invalid Cron in TestTask
  val invalidCronText: String =
    """
      |{
      |  "defaultJobTemplate": "ptc-gatlingrunner:10",
      |  "defaultJobQueue": "simacan-performance-testing-job-queue",
      |  "tasks": [
      |    {
      |      "name": "PCNLGApiSimulation",
      |      "cron": "0 */5 * * * ? *",
      |      "gatlingJob": {
      |        "script": "com.simacan.services.postcodenlgeocoder.PCNLGApiSimulation",
      |        "description": "PCNLGApiSimulation",
      |        "options": [
      |          {
      |            "name": "AUTH_API_URI",
      |            "value": "https://auth-service-dev.services.simacan.com"
      |          },
      |          {
      |            "name": "GS_USERCOUNT",
      |            "value": "2"
      |          },
      |          {
      |            "name": "GS_REPEATCOUNT",
      |            "value": "5"
      |          }
      |        ]
      |      }
      |    },
      |    {
      |      "name": "MegaSimRun",
      |      "cron": "0 */5 * * * ? *",
      |      "gatlingJob": {
      |        "script": "com.simacan.sct.MegaSim",
      |        "description": "MegaSimRun",
      |        "options": [
      |          {
      |            "name": "AUTH_API_URI",
      |            "value": "https://auth-service-dev.services.simacan.com"
      |          },
      |          {
      |            "name": "GS_USERCOUNT",
      |            "value": "2"
      |          },
      |          {
      |            "name": "GS_REPEATCOUNT",
      |            "value": "5"
      |          },
      |          { "name": "GENERIC_REPEAT_COUNT",
      |            "value":  "5"
      |          },
      |          {"name": "USER_LOAD_TEST_PASSWORD",
      |          "value":  "v1:01ddac712bed66b58cebcb2e64adc54a:97149ba59f0fb6dbee4ce206f1810d69959e64b569e992a90fe0d145487406ba",
      |          "encrypted" :  true},
      |          {"name": "GHOST_USER_SECRET",
      |          "value": "v1:d9fdff18f293c8bc6cee94dd97401414:e025d989dc00cc53d7cfb063da32ee658b957260bf951d6650d4ea935e524067b457ee20b4b41eda6865c3789cde6ddc",
      |          "encrypted": true},
      |          {"name":  "GENERIC_RAMP_UP_USER",
      |          "value":  "1"},
      |          {"name": "GENERIC_INITIAL_USER",
      |          "value": "1"},
      |          {"name": "USER_LOAD_TEST_USERNAME",
      |          "value":  "control-tower-load-tester@simacan.com"},
      |          {"name": "GHOST_USER_PASSWORD",
      |          "value": "v1:79f55b955b2bb7ea333b579b37dfba85:7ed18d03ad1d2cb58a812ceb258cd6913da6502fbc6eb4352b7dd326d1eba6ac",
      |          "encrypted":  true},
      |          {"name":  "SCT_WEB_API_URI",
      |          "value": "https://sct-web-api-dev.simacan.com"},
      |          {"name": "GHOST_USER_USERNAME",
      |          "value": "ghost.user@simacan.com"},
      |          {"name" :  "RAMP_UP_DURATION",
      |          "value": "5"}
      |        ]
      |      }
      |    },
      |    {
      |      "name": "ptc-refreshtoken",
      |      "cron": "0 */10 * * * ? *",
      |      "gatlingJob": {
      |        "script": "com.simacan.services.authapi.AuthApiSimulation",
      |        "description": "Refresh Token Call",
      |        "options": [
      |          {
      |            "name": "AUTH_API_REALMS",
      |            "value": "1"
      |          },
      |          {
      |            "name": "AUTH_API_PROFS",
      |            "value": "1"
      |          },
      |          {
      |            "name": "AUTH_API_USERS",
      |            "value": "1"
      |          }
      |        ]
      |      }
      |    },
      |    {
      |      "name": "TestTask",
      |      "cron": "0 */5 * * * * *",
      |      "shellJob": "shell3"
      |    }
      |  ]
      |}
      |""".stripMargin

}
