package com.neilshannon.graph

import org.specs2.mutable.Specification
import net.liftweb.json._
import com.neilshannon.api.LinkedIn
import com.neilshannon.util.CleanupUtil

class Neo4jSpec extends Specification {
  implicit val formats = DefaultFormats

  sequential

  "the neo4j interface" should {
    "be able to build a network for the current user and their connections" in {
      val jsonConnectionString = io.Source.fromInputStream(getClass.getResourceAsStream("/connections.json")).mkString
      val jsonConnections = parse(jsonConnectionString)

      val jsonUserString = io.Source.fromInputStream(getClass.getResourceAsStream("/profile.json")).mkString
      val jsonUser = parse(jsonUserString)

      val person = LinkedIn.extractCurrentUser(jsonUser).get
      val people = LinkedIn.extractConnections(jsonConnections).flatten

      Neo4j.createNetwork(person, people) must not(throwAn[Exception])
    }
  }

  val linkedin_id = "QeOH2MK8PL" //neil's id

  "the graph API" should {
    "find all connections in the graph" in {
      Graph.getAllConnections.size must beGreaterThanOrEqualTo(345)
    }
    "find all connection counts by company in the graph" in {
      val companiesAndCounts = Graph.getAllCountsByCompany
      companiesAndCounts.size must beEqualTo(176)
      companiesAndCounts("Elemica") must beEqualTo(41)
    }
    "find all connections for a user in the graph" in {
      Graph.getAllConnectionsForUser(linkedin_id).size must beGreaterThanOrEqualTo(344)
    }
    "find all connection counts by company for a user in the graph" in {
      val companiesAndCounts = Graph.getAllCountsByCompanyForUser(linkedin_id)
      companiesAndCounts.size must beEqualTo(176)
      companiesAndCounts("Elemica") must beEqualTo(41)
    }
    "find depth of connections and counts for a user in the graph" in {
      val depthsAndCounts = Graph.getRelationshipCountsByDepthForUser(linkedin_id)
      depthsAndCounts.size must beEqualTo(1)
      depthsAndCounts("1") must beGreaterThanOrEqualTo(344L)
    }
  }

  step{
    Neo4j.cleanUp()
    CleanupUtil.cleanUp()
  }

}