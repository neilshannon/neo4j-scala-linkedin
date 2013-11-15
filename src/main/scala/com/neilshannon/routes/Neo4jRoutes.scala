package com.neilshannon.routes

import unfiltered.response.Pass
import unfiltered.filter.Intent

object Neo4jRoutes extends {
  def intent = Intent {
    case _ => Pass
  }
}