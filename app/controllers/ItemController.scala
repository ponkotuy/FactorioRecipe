package controllers

import javax.inject.Inject

import com.github.tototoshi.play2.json4s.native.Json4s
import models.{Item, ItemDetail}
import org.json4s.{DefaultFormats, Extraction}
import parsers.ItemParser
import play.api.mvc.{Action, Controller}
import requests.UploadZipItemForm
import scalikejdbc.DB

class ItemController @Inject()(json4s: Json4s) extends Controller {
  import json4s._
  import Responses._
  import models.PersistItem.getItemId
  implicit val formats = DefaultFormats

  def list() = Action {
    import models.Aliases.i
    val items = Item.findAll(i.name :: Nil)
    Ok(Extraction.decompose(items))
  }

  def show(itemId: Long) = Action {
    Item.findById(itemId).fold(notFound(s"itemId = ${itemId}")) { item =>
      Ok(Extraction.decompose(item))
    }
  }

  def upload() = Action(parse.multipartFormData){ implicit req =>
    UploadZipItemForm.fromReq(req).fold(BadRequest("Form error")){ form =>
      DB localTx { implicit session =>
        ZipUtil.inputStreams(form.file.ref.file).foreach{ is =>
          ItemParser.parse(is).foreach{ item =>
            val itemId = getItemId(item.name)
            ItemDetail.deleteById(itemId)
            ItemDetail.create(item.detail(itemId))
          }
        }
      }
      success
    }
  }
}
