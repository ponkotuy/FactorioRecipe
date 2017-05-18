package controllers

import java.nio.file.{Files, Path, Paths}
import javax.inject.Inject

import com.github.tototoshi.play2.json4s.native.Json4s
import models.PersistRecipe
import org.json4s.DefaultFormats
import parsers.RecipeParser
import play.api.mvc.{Action, Controller}
import scalikejdbc.DB

import scala.collection.JavaConverters._

class MainController @Inject()(json4s: Json4s) extends Controller {
  import Responses._

  implicit val formats = DefaultFormats

  def index() = Action(success)

  def loadRecipe() = Action {
    val version = "0.15.10"
    val path = Paths.get("prototypes/recipe")
    val files = Files.list(path)

    files.iterator().asScala.foreach{ file =>
      val recipes = RecipeParser.parse(file)
      val persist = new PersistRecipe(version)
      DB localTx { implicit session =>
        recipes.foreach(persist.apply)
      }
    }
    success
  }
}
