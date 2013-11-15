package com.neilshannon.security

import org.specs2.mutable.Specification

class MD5Spec extends Specification {
  "the MD5 object" should {
    "generate a valid MD5 digest given a String" in {
      val hash = MD5.hash("test" + System.currentTimeMillis())
      hash must be matching("[0-9A-F]{32}")
    }
  }
}