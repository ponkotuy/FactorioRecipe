package requests

import play.api.libs.Files
import play.api.mvc.{MultipartFormData, Request}

case class UploadZipItemForm(file: MultipartFormData.FilePart[Files.TemporaryFile])

object UploadZipItemForm {
  def fromReq(req: Request[MultipartFormData[Files.TemporaryFile]]): Option[UploadZipItemForm] = {
    req.body.file("zipFile").map(new UploadZipItemForm(_))
  }
}
