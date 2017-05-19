package requests

import models.Password
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import play.api.libs.Files
import play.api.mvc.{MultipartFormData, Request}

case class UploadZipRecipeForm(
    version: String,
    password: String,
    file: MultipartFormData.FilePart[Files.TemporaryFile]
) {
  def passwordRecord = {
    Password(0L, version, MyBCrypt.createHash(password))
  }
}

object UploadZipRecipeForm {
  def fromReq(req: Request[MultipartFormData[Files.TemporaryFile]]): Option[UploadZipRecipeForm] = {
    val data = req.body.dataParts
    for {
      version <- data.get("version").flatMap(_.headOption)
      password <- data.get("password").flatMap(_.headOption)
      file <- req.body.file("zipFile")
    } yield new UploadZipRecipeForm(version, password, file)
  }
}

object MyBCrypt {
  val bcrypt = new BCryptPasswordEncoder(12)
  def createHash(password: String) = bcrypt.encode(password)
  def authenticate(password: String, hash: String): Boolean =
    bcrypt.matches(password, hash)
}
