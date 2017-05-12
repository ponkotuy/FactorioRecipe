package controllers

import javax.inject.Inject

import com.github.tototoshi.play2.json4s.native.Json4s
import org.json4s.{DefaultFormats, Extraction}
import parsers.{ItemParser, RecipeParser}
import play.api.mvc.{Action, Controller}

class MainController @Inject()(json4s: Json4s) extends Controller {
  import json4s._

  implicit val formats = DefaultFormats

  def index() = Action {
    val items = ItemParser.parse("prototypes/item/item.lua")
    val recipes = RecipeParser.parse("prototypes/recipe/recipe.lua")
    Ok(Extraction.decompose(recipes))
  }
}
