package com.neilshannon.db

import org.squeryl.Session
import org.squeryl.adapters.H2Adapter
import org.squeryl.PrimitiveTypeMode._
import DBSchema._
import org.clapper.avsl.Logger
import org.squeryl.SessionFactory

object DB {

  val log = Logger(classOf[DB])

  initialize()

  def initialize() {
    Class.forName("org.h2.Driver")

    SessionFactory.concreteFactory = Some(()=>
      Session.create(
        java.sql.DriverManager.getConnection("jdbc:h2:mem:linkedin_users;DB_CLOSE_DELAY=-1", "sa", ""),
        new H2Adapter)
    )
    transaction {
      DBSchema.create
    }
  }

  def getAccessToken(userID: String): Option[Token] = {
    transaction {
      try{
        Some(tokens.where(t => t.user_id === userID).single)
      }
      catch{
        case ex: Exception => {
          log.error(ex)
          None
        }
      }
    }
  }

  def saveAccessToken(userID: String, token: String) = {
    transaction {
      tokens.insert(new Token(0, userID, token))
    }
    log.info("Access token saved for user ID [" +  userID + "]")
  }

  def removeAccessToken(userID: String) = {
    transaction {
      tokens.deleteWhere(t => t.user_id === userID)
    }
  }

}

class DB {}