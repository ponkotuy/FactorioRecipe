package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class Recipe(
    id: Long,
    name: String,
    time: Double,
    category: Option[String],
    version: String,
    ingredients: Seq[Ingredient] = Nil,
    results: Seq[Result] = Nil
)

object Recipe extends SkinnyCRUDMapperWithId[Long, Recipe] {
  import Aliases.{re, in}

  override val defaultAlias: Alias[Recipe] = createAlias("r")

  override def idToRawValue(id: Long): Any = id
  override def rawValueToId(value: Any): Long = value.toString.toLong

  override def extract(rs: WrappedResultSet, n: ResultName[Recipe]): Recipe =
    autoConstruct(rs, n, "ingredients", "results")

  hasMany[Ingredient](
    many = Ingredient -> in,
    merge = (r, ings) => r.copy(ingredients = ings),
    on = (r, in) => sqls.eq(r.id, in.recipeId)
  ).byDefault

  hasMany[Result](
    many = Result -> re,
    merge = (r, results) => r.copy(results = results),
    on = (r, res) => sqls.eq(r.id, res.recipeId)
  ).byDefault

  def create(r: Recipe)(implicit session: DBSession): Long =
    createWithAttributes('name -> r.name, 'time -> r.time, 'category -> r.category, 'version -> r.version)

  def findAllByResult(resultItemId: Long)(implicit session: DBSession): Seq[Recipe] =
    Recipe.findAllBy(sqls.eq(re.itemId, resultItemId))
}
