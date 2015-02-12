package com.neilshannon.routes

import com.neilshannon.api.LinkedIn
import com.neilshannon.db.DB
import com.neilshannon.graph.{Graph, Neo4j}
import unfiltered.Cookie
import unfiltered.filter.Intent
import unfiltered.request._
import unfiltered.response.{Redirect, ResponseString, _}
import unfiltered.scalate.Scalate

object LinkedInRoutes {
  import unfiltered.request.QParams._

  def intent = Intent {
    case GET(Path("/linkedin_auth")) & Cookies(cookies) => {
      getAccessToken(cookies) match {
        case Some(token) => {
          Redirect("/load_connections")
        }
        case _ => Redirect(LinkedIn.redirectUrl)
      }
    }

    case req @ GET(Path("/load_connections")) & Cookies(cookies) => {
      cookies("linkedin_id") match {
        case Some(Cookie(_, linkedin_id, _, _, _, _, _, _)) => {
          DB.getAccessToken(linkedin_id).map { token =>
            val person = LinkedIn.getProfile(token.auth_token).get
            val connections = LinkedIn.getConnections(token.auth_token).flatten

            Neo4j.createNetwork(person, connections) //todo ajaxify

            val stats = Graph.getRelationshipCountsByDepthForUser(linkedin_id)

            Ok ~> Scalate(
              req,
              "templates/home.jade",
              ("success", "Connections loaded successfully!"),
              ("pageTitle", "Home"),
              ("person", person),
              ("stats", stats)
            )
          }.getOrElse(
            InternalServerError ~> ResponseString("Could not load your connections from LinkedIn")
          )
        }
        case _ => Redirect(LinkedIn.redirectUrl)
      }
    }

    case req @ GET(Path("/home")) & Cookies(cookies) => {
      cookies("linkedin_id") match {
        case Some(Cookie(_, linkedin_id, _, _, _, _, _, _)) => {
          DB.getAccessToken(linkedin_id).map { token =>
            val person = LinkedIn.getProfile(token.auth_token).get
            val stats = Graph.getRelationshipCountsByDepthForUser(linkedin_id)
            Ok ~> Scalate(
              req,
              "templates/home.jade",
              ("pageTitle", "Home"),
              ("person", person),
              ("stats", stats)
            )
          }.getOrElse(
              Redirect("/index") //user not logged in
            )
        }
        case _ => Redirect("/index")
      }
    }

    case req @ GET(Path("/authorize") & Params(params) & Cookies(cookies)) => {
      val expected = for {
        code <- lookup("code") is
          nonempty("Access code not returned from LinkedIn") is
          required("Missing access code. Unauthorized.")
        state <- lookup("state") is
          nonempty("State not returned from LinkedIn") is
          required("Missing state.  Possible CSRF.")
      }
      yield {
        getAccessTokenWithCode(cookies, code) match {
          case Some(token) => {
            val person = LinkedIn.getProfile(token).get //add caching
            SetCookies(
              Cookie("linkedin_id", person.linkedin_id)
            )~>
            Scalate(
              req,
              "templates/loadConnections.jade",
              ("pageTitle", "Load your Connections"),
              ("person", person)
            )
          }
          case None => InternalServerError ~> ResponseString("Could not get access token from LinkedIn")
        }
      }
      expected(params) orFail { fails =>
        val linkedInErrors = for {
          error <- params.get("error")
          errorDescription <- params.get("error_description")
        }
        yield{
          "LinkedIn Error: [%s]. Reason: [%s]." format (error.head, errorDescription.head)
        }
        Unauthorized ~> ResponseString(
          "Failed Authorization " + linkedInErrors.map("\n\n" + _).getOrElse("") + "\n" + fails.map(_.error).mkString("\n")
        )
      }
    }

    case _ => Pass

  }

  private def getAccessToken(cookies: Map[String, Option[Cookie]]) = {
    cookies("linkedin_id") match {
      case Some(Cookie(_, linkedin_id, _, _, _, _, _, _)) => {
        DB.getAccessToken(linkedin_id).map(_.auth_token)
      }
      case _ => None
    }
  }

  private def getAccessTokenWithCode(cookies: Map[String, Option[Cookie]], code: Option[String]) = {
    cookies("linkedin_id") match {
      case Some(Cookie(_, linkedin_id, _, _, _, _, _, _)) => {
        DB.getAccessToken(linkedin_id).map(_.auth_token).orElse(
          exchangeCodeForToken(code.get)
        )
      }
      case _ => {
        val accessCode = code.get
        exchangeCodeForToken(accessCode)
      }
    }
  }

  private def exchangeCodeForToken(accessCode: String): Option[String] = {
    val accessToken = LinkedIn.getAccessToken(accessCode)
    accessToken.map(token =>
      LinkedIn.getProfile(token).map(person =>
        DB.saveAccessToken(person.linkedin_id, token)
      )
    )
    accessToken
  }
}