package model

import model.types.UrlString
import mongo4cats.bson.ObjectId

import java.time.Instant

case class Document(
    _id: ObjectId,
    title: Option[String],
    url: UrlString,
    htmlText: String,
    lastUpdated: Instant)
