package com.neilshannon.security

import java.security.MessageDigest

object MD5 {
  def hash(s: String) = {
    MessageDigest.getInstance("MD5").digest(s.getBytes).map("%02X".format(_)).mkString
  }
}