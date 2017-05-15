package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyJoinTable, SkinnyNoIdCRUDMapper}

case class Ingredient(recipeId: Long, itemId: Long, amount: Int, item: Option[Item] = None)

object Ingredient extends SkinnyNoIdCRUDMapper[Ingredient] {
  override val defaultAlias: Alias[Ingredient] = createAlias("ing")

  override def extract(rs: WrappedResultSet, n: ResultName[Ingredient]): Ingredient = autoConstruct(rs, n, "item")

  def create(in: Ingredient)(implicit session: DBSession): Unit =
    createWithAttributes('recipeId -> in.recipeId, 'itemId -> in.itemId, 'amount -> in.amount)

  belongsTo[Item](
    right = Item,
    merge = (in, item) => in.copy(item = item)
  ).byDefault
}
