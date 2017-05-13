package models

import scalikejdbc.DBSession
import skinny.orm.{Alias, SkinnyJoinTable}

case class Result(recipeId: Long, itemId: Long, amount: Int)

object Result extends SkinnyJoinTable[Result] {
  override val defaultAlias: Alias[Result] = createAlias("re")

  def create(r: Result)(implicit session: DBSession): Unit =
    createWithAttributes('recipeId -> r.recipeId, 'itemId -> r.itemId, 'amount -> r.amount)
}
