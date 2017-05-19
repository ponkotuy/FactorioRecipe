package requests

import play.api.data.Form
import play.api.data.Forms._

case class DeleteForm(passwrod: String)

object DeleteForm {
  def from = Form(
    mapping(
      "password" -> text
    )(DeleteForm.apply)(DeleteForm.unapply)
  )
}
