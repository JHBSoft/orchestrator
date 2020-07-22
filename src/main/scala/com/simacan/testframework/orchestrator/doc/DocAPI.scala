package com.simacan.testframework.orchestrator.doc

import com.simacan.base.swagger.ApiSpecificationRouteProvider
import com.simacan.base_service_library.buildinfo.BuildInfo

class DocAPI
    extends ApiSpecificationRouteProvider(
      serviceName = BuildInfo.name,
      apiVersion = 1,
      buildVersion = BuildInfo.version,
      //swaggerResourcePath = "githubHandler/swagger.yaml",
      //indexHtmlResourcePath = "githubHandler/redoc.html"
    )
