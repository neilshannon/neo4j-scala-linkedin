package com.neilshannon.api

import com.neilshannon.security.MD5
import scala.collection.mutable
import com.neilshannon.config.Configuration
import com.neilshannon.json.LiftJsonParsing
import net.liftweb.json.JsonAST._
import net.liftweb.json.{JsonAST, DefaultFormats}
import org.clapper.avsl.Logger
import net.liftweb.json.JsonAST.JField
import com.neilshannon.model.Person
import net.liftweb.json.JsonAST.JString
import scala.Some
import net.liftweb.json.MappingException
import net.liftweb.json.JsonAST.JArray
import dispatch.classic.{Http, url}

object LinkedIn extends LiftJsonParsing {
  implicit val formats = DefaultFormats

  val logger = Logger(classOf[LinkedIn])

  private val linkedInAuthUri = Configuration.oauth2AuthUri
  private val redirectUri = Configuration.authRedirectUri
  private val apiKey = Configuration.apiKey
  private val stateSecret = "sdf8972lsdkd"
  private val scopes = "r_basicprofile%20r_network"

  private var validStates = mutable.Set[String]()

  def redirectUrl = {
    val state = generateState
    validateState(state)
    linkedInAuthUri.format(apiKey, scopes, generateState, redirectUri)
  }

  def getAccessToken(authCode: String) = {
    Http(parseJson(url(Configuration.accessTokenUri(authCode))){ json =>
      json \ "access_token" match {
        case JString(token) => Some(token)
        case _ => None
      }
    })
  }

  def getProfile(accessToken: String) = {
    Http(parseJson(url(Configuration.profileUri(accessToken))){ json =>
      extractCurrentUser(json)
    })
  }

  def getConnections(accessToken: String) = {
    Http(parseJson(url(Configuration.connectionsUri(accessToken))){ json =>
      extractConnections(json)
    })
  }

  def extractConnections(json: JValue): List[Option[Person]] = {
    for{
      JField("values", JArray(people)) <- json
      person <- people
      JField("firstName", JString(firstName)) <- person
    }
    yield {
      extractPerson(firstName, person)
    }
  }

  def extractCurrentUser(json: JValue): Option[Person] = {
    extractPerson(json)
  }


  /**
   * Converts a JSON person representation to a Person
   * @param firstName the Person's first name, or "private" if LinkedIn is returning bogus profile representations
   * @param person a JSON representation of a Person
   * @return the extracted Person, or Nil if there was some problem extracting
   */
  def extractPerson(firstName: String, person: JsonAST.JValue): Option[Person] = {
    try {
      firstName match {
        case "private" => None
        case _ => {
          val transformedPerson = transformLinkedInFields(person)
          Some(transformedPerson.extract[Person])
        }
      }
    }
    catch {
      case ex: MappingException => {
        logger.error(ex)
        None
      }
    }
  }

  def extractPerson(person: JsonAST.JValue): Option[Person] = {
    try {
      val transformedPerson = transformLinkedInFields(person)
      val personModel = transformedPerson.extract[Person]
      Some(personModel)
    }
    catch {
      case ex: MappingException => {
        logger.error(ex)
        None
      }
    }
  }


  /**
   * Neo4j internally stores a field with the name "id" already.  In order to keep track
   * of a person's LinkedIn id (also named "id" in LinkedIn's Connections API response)
   * we must transform the field name to "linkedin_id"
   * @param person a parsed JSON representation of a Person
   * @return the transformed JSON
   */
  def transformLinkedInFields(person: JsonAST.JValue): JValue = {
    person transform {
      case JField("id", x) => JField("linkedin_id", x)
      case JField("positions", JObject(List(JField("_total", JInt(count)), JField("values", JArray(positions))))) => JField("positions", JArray(positions))
    }
  }

  def validateState(state: String) {
    validStates += state
  }

  def invalidateState(state: String) {
    validStates -= state
  }

  private def generateState = {
    val secret = System.currentTimeMillis() + stateSecret
    MD5.hash(secret)
  }

}

class LinkedIn {}