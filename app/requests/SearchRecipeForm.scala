package requests

import play.api.data.Form
import play.api.data.Forms._

case class SearchRecipeForm(elems: Seq[Long])
object SearchRecipeForm {
  val form = Form(
    mapping(
      "elem" -> seq(longNumber(min = 0L))
    )(SearchRecipeForm.apply)(SearchRecipeForm.unapply)
  )
}
