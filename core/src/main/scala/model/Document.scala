package model

import model.types.UrlString
import mongo4cats.bson.ObjectId

case class Document(_id: ObjectId, title: String, url: UrlString, htmlText: String)
