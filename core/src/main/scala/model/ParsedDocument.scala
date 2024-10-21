package model

import model.types.UrlString

case class ParsedDocument(
    title: String,
    url: UrlString,
    htmlText: String)
