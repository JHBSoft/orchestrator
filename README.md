TestFramework - Orchestrator
============================

Introduction
------------

The orchestrator is, as the name includes, the organizer in the TestFramework. 
It has currently two main functions:

* It offers a port to Github to listen to Github webhook messages<br>
 As soon as it receives such a webhook message, the orchestrator will (GIT) pull the 
 most recent version of the Develop branch into the framework.<br>
 As soon as the new version is pulled, the orchestrator will restart the scheduler 
 function to load any new configuration file. 
* Scheduler.<br>
 The second function of the orchestrator is to submit test tasks to the 
 framework's AWS Batch component. Therefore it reads a job schedule file, 
 which can be part of the scripts repository.<br>
 
API
---
To support those two main functions, the orchestrator also offers a small API 
with supportive functions. More information about this API can be found in the
swagger file (to be found at: src/main/resources/swagger.yaml)<br> 
To increase security, the Webhook service API of the Orchestrator is published 
on a different port (port 8000) than the other API calls (port 9000). 

Other Components
----------------
The Orchestrator is just one component of the TestFramework. The diagram below shows
an overview of the entire TestFramework. 

![TestFramework-Components](docs/images/test%20framework.png)

Local Development
-----------------
For local development and unit-testing purposes at least a few local environment
variables have to be set (when excluding all CodeShipSkip tagged tests):
- GITHUB_HOOK=123456
- HTTP_PORT_GIT=7000
- ENCRYPTION_KEY=0102030405060708090a0b0c0d0e0f10

Optional Environment Settings:
- ENABLE_LOGGING_TO_CONSOLE=true

The unittests are setup with those values and will fail if not set. 

To control the execution of unit-tests, a few tags are used:
- AwsConnectedTag <br>
These tests are using a live connection to AwsBatch and are more used during development 
to see the actual connection working. 
- GitHubConnectedTag<br>
These tests are using a real repository on Github (configured through a local repo) 
and are mainly used to see the actual connection during development.
- WithRunningScheduler<br>
These tests are using a running instance of the Quartz Scheduler. Skip them to speed up
the unit-tests a little.
- CodeShipSkip<br>
The tests marked as CodeShipSkip are mostly causing the CodeShip build to fail due to 
live connections to AwsBatch or Github. 

