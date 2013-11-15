package com.neilshannon.graph

import org.neo4j.scala._
import com.neilshannon.model.{PersonDTO, Person}
import org.neo4j.graphdb.{Relationship, Node}
import com.neilshannon.model.Person
import scala.Some
import com.neilshannon.model.PersonDTO

object Neo4j extends Neo4jWrapper with SingletonEmbeddedGraphDatabaseServiceProvider with Cypher with Neo4jIndexProvider {
  override def neo4jStoreDir = "/tmp/neo4j-store"

  override def NodeIndexConfig =
    ("PeopleIndex", Some(Map("provider" -> "lucene", "type" -> "fulltext"))) :: Nil


  override def RelationIndexConfig =
    ("ConnectionIndex", Some(Map("provider" -> "lucene", "type" -> "fulltext"))) :: Nil

  def createNetwork(person: Person, people: List[Person]) = {
    val user = createPerson(person)
    people.map { connection =>
      val connectionNode = createPerson(connection)
      createRelationship(user, connectionNode)
    }
  }

  def createPerson(person: Person): Node = {
    findPerson(person.linkedin_id) match {
      case Some(node) => node
      case None => {
        withTx { implicit neo =>
          val node = createNode(person.toPersonDTO)
          indexPerson(node)
          node
        }
      }
    }
  }

  def createRelationship(start: Node, end: Node) = {
    findRelationship(start, end) match {
      case Some(relationship) => relationship
      case None =>
        withTx { implicit neo =>
          val relationship = start --> "CONNECTED" --> end <;
          indexRelationship(relationship, start, end)
        }
    }
  }

  def findPerson(id: String): Option[Node] = {
    withTx { implicit neo =>
      getNodeIndex("PeopleIndex").map(index => {
        index.get("linkedin_id", id).getSingle
      })
      match {
        case Some(null) => None
        case Some(node) => Some(node)
        case _ => None
      }
    }
  }

  def findRelationship(start: Node, end: Node): Option[Relationship] = {
    withTx{ implicit neo =>
      getRelationIndex("ConnectionIndex").map(index => {
        val startId = start.getProperty("linkedin_id")
        val endId = end.getProperty("linkedin_id")
        index.get("start_end_ids", startId + "_" + endId).getSingle
      }) match {
        case Some(null) => None
        case Some(relationship) => Some(relationship)
        case _ => None
      }
    }
  }

  def indexPerson(node: Node) {
    withTx { implicit neo =>
      getNodeIndex("PeopleIndex").map(index => {
        index.putIfAbsent(node, "linkedin_id", node.getProperty("linkedin_id"))
      })
    }
  }

  def indexRelationship(relationship: Relationship, startNode: Node, endNode: Node) {
    withTx{ implicit neo =>
      getRelationIndex("ConnectionIndex").map(index => {
        val startId = startNode.getProperty("linkedin_id")
        val endId = endNode.getProperty("linkedin_id")
        index.putIfAbsent(relationship, "start_end_ids", startId+"_"+endId)
      })
    }
  }

  def executeCypherPersonQuery(query: String, personNodeIdentifier: String) = {
    withTx { implicit neo =>
      query.execute.asCC[PersonDTO](personNodeIdentifier)
    }
  }

  def executeCypherMapQuery(query: String, keyColumn: String, valueColumn: String): Map[String, Long] = {
    var resultMap = Map[String, Long]()
    withTx { implicit neo =>
      query.execute foreach ( row => {
          val key: String = row.get(keyColumn) match {
            case Some(x: String) => x
            case Some(y: Long) => java.lang.Long.toString(y)
            case Some(z: Int) => java.lang.Integer.toString(z)
            case _ => ""
          }
          val value: Long = row.get(valueColumn) match {
            case Some(x: Long) => x
            case _ => 0L
          }
          resultMap += (key -> value)
      })
    }
    resultMap
  }

  def cleanUp() {
    withTx { implicit neo =>
      shutdown
    }
  }

  sys.addShutdownHook{
    cleanUp()
  }

}