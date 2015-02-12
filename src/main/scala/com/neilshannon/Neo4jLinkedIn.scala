package com.neilshannon

import com.neilshannon.routes.{AdminRoutes, LinkedInRoutes, Neo4jRoutes}
import org.clapper.avsl.Logger
import unfiltered.filter.Plan

class Neo4jLinkedIn extends Plan {

  val logger = Logger(classOf[Neo4jLinkedIn])

  def intent = {
    LinkedInRoutes.intent.onPass(Neo4jRoutes.intent).onPass(AdminRoutes.intent)
  }

}