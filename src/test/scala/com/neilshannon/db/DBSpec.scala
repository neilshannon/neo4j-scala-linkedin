package com.neilshannon.db

import org.specs2.mutable.Specification

class DBSpec extends Specification {
  "the db" should {
    "persist and retrieve a user's access token by user ID" in {
      val testUserID = "23432532ZBC"
      val testToken = "ABC123"

      DB.saveAccessToken(testUserID, testToken)

      val token = DB.getAccessToken(testUserID).get
      token.auth_token must_== testToken
    }
  }
}