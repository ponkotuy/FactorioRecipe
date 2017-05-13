package models

import scalikejdbc.DBSession
import skinny.orm.{Alias, SkinnyJoinTable}

case class Ingredient(recipeId: Long, itemId: Long, amount: Int)

object Ingredient extends SkinnyJoinTable[Ingredient] {
  override val defaultAlias: Alias[Ingredient] = createAlias("ing")
  def create(in: Ingredient)(implicit session: DBSession): Unit =
    createWithAttributes('recipeId -> in.recipeId, 'itemId -> in.itemId, 'amount -> in.amount)
}
