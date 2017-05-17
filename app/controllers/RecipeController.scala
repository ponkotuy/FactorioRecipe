package controllers

import javax.inject.Inject

import com.github.tototoshi.play2.json4s.native.Json4s
import models.{Ingredient, Item, Recipe, Result}
import org.json4s.{DefaultFormats, Extraction}
import play.api.mvc.{Action, Controller}
import requests.SearchRecipeForm
import scalikejdbc._

class RecipeController @Inject()(json4s: Json4s) extends Controller {
  import Responses._
  import json4s._
  implicit val formats = DefaultFormats

  def fromResult(itemId: Long) = Action { implicit req =>
    SearchRecipeForm.form.bindFromRequest().fold(
      badRequest(_),
      form => Ok(Extraction.decompose(search(itemId, form)))
    )
  }

  def deleteVersion(version: String) = Action {
    import models.Aliases.r
    val recipeIds = Recipe.findAllBy(sqls.eq(r.version, version)).map(_.id)
    Result.deleteBy(sqls.in(Result.column.recipeId, recipeIds))
    Ingredient.deleteBy(sqls.in(Ingredient.column.recipeId, recipeIds))
    Recipe.deleteBy(sqls.in(Recipe.column.id, recipeIds))
    success
  }

  private def search(itemId: Long, form: SearchRecipeForm) = {
    val rawMaterials = fromResultRecursive(Material(itemId, 1.0))
    var materials = joinMaterials(rawMaterials)
    form.elems.foreach { elem =>
      materials.find(_.id == elem).foreach { mat =>
        val exclude = fromResultRecursive(mat)
        materials = materials.map { m =>
          m.copy(exclude = exclude.find(_.id == m.id).map(_.amount).getOrElse(0.0))
        }
      }
    }
    materials
  }

  private def fromResultRecursive(material: Material): Seq[Material] = {
    val itemId = material.id
    val recipe = Recipe.findAllByResult(itemId)(AutoSession).find(_.results.size == 1)
    recipe.fold(material :: Nil) { reci =>
      val resultCount = reci.results.find(_.itemId == itemId).map(_.amount).getOrElse(1)
      material.copy(time = reci.time / resultCount * 2) ::
          reci.ingredients.flatMap { ing =>
            val next = Material(ing.itemId, material.amount * ing.amount / resultCount)
            fromResultRecursive(next)
          }.toList
    }
  }

  private def joinMaterials(materials: Seq[Material]): Seq[Material] = {
    materials.sortBy(-_.id).foldLeft[List[Material]](Nil) { (xs, mat) =>
      xs.headOption.fold(mat :: Nil) { head =>
        if(head.id == mat.id)
          mat.copy(amount = head.amount + mat.amount) :: xs.tail
        else
          mat :: xs
      }
    }
  }
}

case class Material(id: Long, name: String, amount: Double, time: Double = 0, exclude: Double = 0)

object Material {
  def apply(itemId: Long, amount: Double) = new Material(itemId, Item.name(itemId), amount)
}
