package com.neilshannon.config

import com.typesafe.config.ConfigFactory

object Configuration {
  private val config = ConfigFactory.load()

  def apiKey = {
    config.getString("linkedin.apiKey")
  }

  def secretKey = {
    config.getString("linkedin.secretKey")
  }

  def authRedirectUri = {
    baseUri + redirectUri
  }

  def oauth2AuthUri = {
    config.getString("linkedin.oauth2AuthUri")
  }

  def baseUri = {
    config.getString("neo4jlinkedin.baseUri")
  }

  def redirectUri = {
    config.getString("neo4jlinkedin.authRedirectUri")
  }

  def accessTokenUri(authCode: String) = {
    config.getString("linkedin.accessTokenUri") format (authCode, authRedirectUri, apiKey, secretKey)
  }

  def connectionsUri(accessToken: String) = {
    config.getString("linkedin.connectionsUri") format accessToken
  }

  def profileUri(accessToken: String) = {
    config.getString("linkedin.profileUri") format accessToken
  }

}