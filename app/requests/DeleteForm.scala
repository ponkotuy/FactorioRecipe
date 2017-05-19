package requests

import play.api.data.Form
import play.api.data.Forms._

object DeleteForm {
  def from = Form("password" -> text)
}
