package controllers

import javax.inject.Inject
import com.github.tototoshi.play2.json4s.native.Json4s
import models._
import org.json4s.{DefaultFormats, Extraction}
import parsers.RecipeParser
import play.api.mvc.{Action, InjectedController}
import requests.{DeleteForm, MyBCrypt, SearchRecipeForm, UploadZipRecipeForm}
import scalikejdbc._

class RecipeController @Inject()(json4s: Json4s) extends InjectedController {
  import Responses._
  import json4s.implicits._
  implicit val formats = DefaultFormats

  def fromResult(version: String, itemId: Long) = Action { implicit req =>
    SearchRecipeForm.form.bindFromRequest().fold(
      badRequest(_),
      form => Ok(Extraction.decompose(search(version, itemId, form)))
    )
  }

  def versions() = Action {
    val versions = Recipe.findVersions()(AutoSession).sorted.reverse
    Ok(Extraction.decompose(versions))
  }

  def upload() = Action(parse.multipartFormData) { implicit req =>
    UploadZipRecipeForm.fromReq(req).fold(BadRequest("Form error")) { form =>
      ZipUtil.inputStreams(form.file.ref.file).foreach { is =>
        val recipes = RecipeParser.parse(is)
        val persist = new PersistRecipe(form.version)
        DB localTx { implicit session =>
          Password.create(form.passwordRecord)
          recipes.foreach(persist.apply)
        }
      }
      Redirect(routes.MyAssets.at("uploader.html"))
    }
  }

  def deleteVersion(version: String) = Action(parse.multipartFormData) { implicit req =>
    import models.Aliases.r
    val password = DeleteForm.from.bindFromRequest().fold(_ => "", _.passwrod)
    if(!deleteAuth(version, password)) Forbidden("Wrong password")
    else {
      val recipeIds = Recipe.findAllBy(sqls.eq(r.version, version)).map(_.id)
      Result.deleteBy(sqls.in(Result.column.recipeId, recipeIds))
      Ingredient.deleteBy(sqls.in(Ingredient.column.recipeId, recipeIds))
      Recipe.deleteBy(sqls.in(Recipe.column.id, recipeIds))
      success
    }
  }

  private def deleteAuth(version: String, password: String): Boolean = {
    import Aliases.p
    Password.findBy(sqls.eq(p.version, version)).fold(false) { p =>
      MyBCrypt.authenticate(password, p.password)
    }
  }

  private def search(version: String, itemId: Long, form: SearchRecipeForm) = {
    val rawMaterials = fromResultRecursive(version, Material(itemId, 1.0))
    var materials = joinMaterials(rawMaterials)
    form.elems.foreach { elem =>
      materials.find(_.id == elem).foreach { mat =>
        val exclude = fromResultRecursive(version, mat)
        materials = materials.map { m =>
          m.copy(exclude = exclude.find(_.id == m.id).map(_.amount).getOrElse(0.0))
        }
      }
    }
    materials
  }

  private def fromResultRecursive(version: String, material: Material): Seq[Material] = {
    val itemId = material.id
    val recipe = Recipe.findAllByResult(version, itemId)(AutoSession).find(_.results.size == 1)
    recipe.fold(material :: Nil) { reci =>
      val resultCount = reci.results.find(_.itemId == itemId).map(_.amount).getOrElse(1)
      material.copy(time = reci.time / resultCount * 2) ::
          reci.ingredients.flatMap { ing =>
            val next = Material(ing.itemId, material.amount * ing.amount / resultCount)
            fromResultRecursive(version, next)
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
