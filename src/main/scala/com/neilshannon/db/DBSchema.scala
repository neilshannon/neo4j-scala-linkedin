package com.neilshannon.db

import org.squeryl.{KeyedEntity, Schema}
import org.squeryl.PrimitiveTypeMode._

class Token(val id: Long, var user_id: String, var auth_token: String) extends KeyedEntity[Long]{
  def this() = this(0,"","")
}

object DBSchema extends Schema {
  val tokens = table[Token]

  on(tokens)(t =>
    declare(
      t.id is(unique, autoIncremented),
      t.auth_token is dbType("varchar(255)")
    )
  )
}