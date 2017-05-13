package controllers

import javax.inject.Inject

import com.github.tototoshi.play2.json4s.native.Json4s
import models.PersistRecipe
import org.json4s.DefaultFormats
import parsers.RecipeParser
import play.api.mvc.{Action, Controller}
import scalikejdbc.DB

class MainController @Inject()(json4s: Json4s) extends Controller {
  import Responses._

  implicit val formats = DefaultFormats

  def index() = Action(success)

  def loadRecipe() = Action {
    val version = "0.15.10"
    val files =
      Seq("recipe", "ammo", "capsule", "equipment", "fluid-recipe", "furnace-recipe", "inserter", "module", "turret")

    files.foreach{ file =>
      val recipes = RecipeParser.parse(s"prototypes/recipe/${file}.lua")
      val persist = new PersistRecipe(version)
      DB localTx { implicit session =>
        recipes.foreach(persist.apply)
      }
    }
    success
  }
}
