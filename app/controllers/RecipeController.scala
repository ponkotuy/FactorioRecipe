package controllers

import javax.inject.Inject

import com.github.tototoshi.play2.json4s.native.Json4s
import models.Recipe
import org.json4s.{DefaultFormats, Extraction}
import play.api.mvc.{Action, Controller}
import scalikejdbc._

class RecipeController @Inject()(json4s: Json4s) extends Controller {
  import json4s._
  implicit val formats = DefaultFormats

  def fromResult(itemId: Long) = Action {
    import models.Aliases.re
    val recipe = Recipe.findAllBy(sqls.eq(re.itemId, itemId))
    Ok(Extraction.decompose(recipe))
  }
}
