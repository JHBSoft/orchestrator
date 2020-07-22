# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [0.1.0] RELEASED
### Added
- Initial version
- Github Webhook API 
- Git Pull Request 
- Orchestrator API
- Submit AWS Batch Jobs

## [0.1.1] RELEASED
### Updated
- Updated library dependencies
- Updated unittests
- Swaggerfile update

### Added
- Configurable vCpu and Memory per job

## [0.1.2] UNRELEASED
### Updated
- Minor updates in Swagger documentation
- LibraryDependecies updated
- validate-jobschedule-json extended with cron-expression validation

### Added
- Logging added

## [0.2.0] UNRELEASED
### Updated 
- CORS enabled
- Security-Content-Header adjusted 

### Added
- Authentication added to all scheduler API calls
- Additional route to return swagger file (due to CORS issues with DocApi)
