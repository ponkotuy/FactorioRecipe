package controllers

import javax.inject.Inject

import com.github.tototoshi.play2.json4s.native.Json4s
import models.Recipe
import org.json4s.{DefaultFormats, Extraction}
import play.api.mvc.{Action, Controller}
import scalikejdbc._

import scala.collection.breakOut

class RecipeController @Inject()(json4s: Json4s) extends Controller {
  import json4s._
  implicit val formats = DefaultFormats

  def fromResult(itemId: Long) = Action {
    val result = fromResultRecursive(Material(itemId, 1.0))
    Ok(Extraction.decompose(result))
  }

  def fromResultRecursive(material: Material): Seq[Material] = {
    val itemId = material.itemId
    val recipe = Recipe.findAllByResult(itemId)(AutoSession).find(_.results.size == 1)
    recipe.fold(material :: Nil) { reci =>
      val resultCount = reci.results.find(_.itemId == itemId).map(_.amount).getOrElse(1)
      reci.ingredients.flatMap { ing =>
        val next = Material(ing.itemId, material.amount * ing.amount / resultCount)
        fromResultRecursive(next)
      }(breakOut)
    }
  }

  def joinMaterials(materials: Seq[Material]): Seq[Material] = {
    materials.sortBy(-_.itemId).foldLeft[List[Material]](Nil) { (xs, mat) =>
      xs.headOption.fold(mat :: Nil) { head =>
        if(head.itemId == mat.itemId)
          mat.copy(amount = head.amount + mat.amount) :: xs.tail
        else
          mat :: xs
      }
    }
  }

  case class Material(itemId: Long, amount: Double)
}
