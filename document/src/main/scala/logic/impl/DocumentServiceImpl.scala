package logic.impl

import logic.DocumentService
import mapper.DocumentMapper
import model.ParsedDocument
import storage.DocumentRepository

case class DocumentServiceImpl[F[_]](documentRepository: DocumentRepository[F]) extends DocumentService[F] {

  /* TODO: add check for already saved html pages but on other urls
   */
  override def processDocument(parsedDocument: ParsedDocument): F[Either[Throwable, Unit]] = {
    val doc = DocumentMapper.toDocument(parsedDocument)
    println(s"ParsedDoc: $parsedDocument")
    documentRepository.save(doc)
  }
}
