package com.neilshannon.routes

import unfiltered.response.Pass
import unfiltered.filter.Intent
import unfiltered.request.{GET, Path}
import unfiltered.scalate.Scalate

object AdminRoutes extends {
  def intent = Intent {
    case req @ GET(Path("/index")) => {
      Scalate(
        req,
        "templates/index.jade",
        ("pageTitle", "Welcome!")
      )
    }
    case _ => Pass
  }
}