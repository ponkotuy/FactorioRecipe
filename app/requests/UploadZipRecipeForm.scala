package requests

import play.api.libs.Files
import play.api.mvc.{MultipartFormData, Request}

case class UploadZipRecipeForm(version: String, file: MultipartFormData.FilePart[Files.TemporaryFile])

object UploadZipRecipeForm {
  def fromReq(req: Request[MultipartFormData[Files.TemporaryFile]]): Option[UploadZipRecipeForm] = {
    for {
      version <- req.body.dataParts.get("version").flatMap(_.headOption)
      file <- req.body.file("zipFile")
    } yield new UploadZipRecipeForm(version, file)
  }
}
