package com.neilshannon.json

import dispatch.classic._
import net.liftweb.json.JsonAST._

trait LiftJsonParsing {
  def parseJson[T](r: Request)(block: JValue => T) =
    r >> { (stm, charset) =>
      block(net.liftweb.json.JsonParser.parse(new java.io.InputStreamReader(stm, charset)))
    }
}