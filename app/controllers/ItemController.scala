package controllers

import javax.inject.Inject

import com.github.tototoshi.play2.json4s.native.Json4s
import models.Item
import org.json4s.{DefaultFormats, Extraction}
import play.api.mvc.{Action, Controller}

class ItemController @Inject()(json4s: Json4s) extends Controller {
  import json4s._
  implicit val formats = DefaultFormats

  def list() = Action {
    import models.Aliases.i
    val items = Item.findAll(i.id :: Nil)
    Ok(Extraction.decompose(items))
  }
}
