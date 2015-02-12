package com.neilshannon.routes

import com.neilshannon.db.DB
import unfiltered.Cookie
import unfiltered.response.{Redirect, SetCookies, Pass}
import unfiltered.filter.Intent
import unfiltered.request._
import unfiltered.request.{Cookies, GET, Path}
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

    case req @ GET(Path("/logout") & Cookies(cookies)) => {
      cookies("linkedin_id") match {
        case Some(Cookie(_, linkedin_id, _, _, _, _, _, _)) => {
          DB.removeAccessToken(linkedin_id)
          SetCookies.discarding("linkedin_id") ~> Redirect("/index")
        }
        case _ => Redirect("/index")
      }
    }
    case _ => Pass
  }
}