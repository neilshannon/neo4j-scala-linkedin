package com.neilshannon.api

import org.specs2.mutable.Specification
import com.neilshannon.config.Configuration
import net.liftweb.json._

class LinkedInSpec extends Specification {
  implicit val formats = DefaultFormats

  "the LinkedIn object" should {
    "build a valid api login url" in {
      LinkedIn.redirectUrl must contain ("https://www.linkedin.com/uas/oauth2/authorization")
      LinkedIn.redirectUrl must contain (Configuration.authRedirectUri)
      LinkedIn.redirectUrl must contain (Configuration.apiKey)
    }
    "be able to extract a list of Persons from a json connection response" in {
      val jsonString = io.Source.fromInputStream(getClass.getResourceAsStream("/connections.json")).mkString
      val json = parse(jsonString)
      val people = LinkedIn.extractConnections(json)
      people.size mustEqual(356)
    }
    "be able to extract the current user into a Person" in {
      val jsonString = io.Source.fromInputStream(getClass.getResourceAsStream("/profile.json")).mkString
      val json = parse(jsonString)
      val person = LinkedIn.extractCurrentUser(json).get
      person.firstName must_== "Neil"
    }
  }
}