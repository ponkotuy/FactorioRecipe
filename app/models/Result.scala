package models

import scalikejdbc._
import skinny.orm._

case class Result(recipeId: Long, itemId: Long, amount: Int, item: Option[Item] = None)

object Result extends SkinnyNoIdCRUDMapper[Result] {
  override val defaultAlias: Alias[Result] = createAlias("re")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[Result]): Result = autoConstruct(rs, n, "item")

  def create(r: Result)(implicit session: DBSession): Unit =
    createWithAttributes('recipeId -> r.recipeId, 'itemId -> r.itemId, 'amount -> r.amount)

  belongsTo[Item](
    right = Item,
    merge = (r, item) => r.copy(item = item)
  ).byDefault
}
