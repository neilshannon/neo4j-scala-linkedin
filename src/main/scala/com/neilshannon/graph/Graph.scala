package com.neilshannon.graph

object Graph {

  val allConnectionsQuery =
    "start s = node:PeopleIndex('*:*') return DISTINCT s;"

  val allConnectionsForUserQuery =
    "start s = node:PeopleIndex(linkedin_id='%s') match s -[:CONNECTED]-> (c) return DISTINCT c;"

  val allCountsByCompanyQuery =
    "start s = node:PeopleIndex('*:*') return DISTINCT s.company as company, count(s) as count;"

  val allCountsByCompanyForUserQuery =
    "start s = node:PeopleIndex(linkedin_id='%s') match s -[:CONNECTED] -> (c) return DISTINCT " +
      "c.company as company, count(c) as count;"

  val relationshipCountsByDepthForUserQuery =
    "start s = node:PeopleIndex(linkedin_id='%s') match path=s-[:CONNECTED*1..3]-> () " +
      "return length(path) as depth, count(length(path)) as count order by depth;"

  def getAllConnections = {
    Neo4j.executeCypherPersonQuery(allConnectionsQuery, "s")
  }

  def getAllConnectionsForUser(person_id: String) = {
    Neo4j.executeCypherPersonQuery(allConnectionsForUserQuery format person_id, "c")
  }

  def getAllCountsByCompany = {
    Neo4j.executeCypherMapQuery(allCountsByCompanyQuery, "company", "count")
  }

  def getAllCountsByCompanyForUser(person_id: String) = {
    Neo4j.executeCypherMapQuery(allCountsByCompanyForUserQuery format person_id, "company", "count")
  }

  def getRelationshipCountsByDepthForUser(person_id: String) = {
    Neo4j.executeCypherMapQuery(relationshipCountsByDepthForUserQuery format person_id, "depth", "count")
  }

}