package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class Recipe(
    id: Long,
    name: String,
    time: Double,
    category: Option[String],
    version: String,
    ingredients: Seq[Item] = Nil,
    results: Seq[Item] = Nil
)

object Recipe extends SkinnyCRUDMapperWithId[Long, Recipe] {
  override val defaultAlias: Alias[Recipe] = createAlias("r")

  override def idToRawValue(id: Long): Any = id
  override def rawValueToId(value: Any): Long = value.toString.toLong

  override def extract(rs: WrappedResultSet, n: ResultName[Recipe]): Recipe =
    autoConstruct(rs, n, "ingredients", "results")

  hasManyThrough[Ingredient, Item](
    through = Ingredient -> Ingredient.createAlias("ing"),
    many = Item -> Item.createAlias("i2"),
    merge = (r, items) => r.copy(ingredients = items),
    throughOn = (r, in) => sqls.eq(r.id, in.recipeId),
    on = (in, i) => sqls.eq(in.itemId, i.id)
  ).byDefault

  hasManyThrough[Item](
    through = Result,
    many = Item,
    merge = (r, items) => r.copy(results = items)
  ).byDefault

  def create(r: Recipe)(implicit session: DBSession): Long =
    createWithAttributes('name -> r.name, 'time -> r.time, 'category -> r.category, 'version -> r.version)
}
