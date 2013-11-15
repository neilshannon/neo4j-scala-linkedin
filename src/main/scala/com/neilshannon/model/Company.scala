package com.neilshannon.model

case class Company(linkedin_id: String, industry: String, name: String, size: Option[String], `type`: Option[String])