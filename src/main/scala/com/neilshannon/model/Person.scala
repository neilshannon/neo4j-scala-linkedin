package com.neilshannon.model

case class Person(
  firstName: String,
  lastName: String,
  headline: String,
  linkedin_id: String,
  industry: String,
  pictureUrl: Option[String],
  location: Location,
  positions: Option[Array[Position]]
){

  def toPersonDTO = {
    val position = currentPosition
    PersonDTO(
      firstName,
      lastName,
      headline,
      linkedin_id,
      industry,
      pictureUrl.getOrElse(""),
      location.name,
      position.map(_.title).getOrElse(""),
      position.map(_.company.name).getOrElse(""),
      position.map(_.company.linkedin_id).getOrElse("")
    )
  }

  def currentPosition = {
    positions.map{actualPositions => actualPositions.filter(_.isCurrent).head}
  }
}