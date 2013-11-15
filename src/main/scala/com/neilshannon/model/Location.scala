package com.neilshannon.model

case class Location(name: String, country: Country){
  override def toString = {
    name
  }
}

case class Country(code: String)