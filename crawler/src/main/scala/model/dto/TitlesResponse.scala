package model.dto

import model.types.UrlString

case class TitlesResponse(titles: Map[UrlString, Option[String]])
