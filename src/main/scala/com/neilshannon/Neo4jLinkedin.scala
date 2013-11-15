package com.neilshannon

import unfiltered.filter.Plan
import com.neilshannon.routes.{AdminRoutes, Neo4jRoutes, LinkedInRoutes}
import unfiltered.request.QParams
import org.clapper.avsl.Logger

class Neo4jLinkedIn extends Plan {
  import QParams._

  val logger = Logger(classOf[Neo4jLinkedIn])

  def intent = {
    LinkedInRoutes.intent.onPass(Neo4jRoutes.intent).onPass(AdminRoutes.intent)
  }

}