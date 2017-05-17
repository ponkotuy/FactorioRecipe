package models

import scalikejdbc._

class PersistRecipe(version: String) {
  def apply(recipe: parsers.Recipe)(implicit session: DBSession) = {
    val record = new models.Recipe(
      0L,
      recipe.name,
      recipe.energyRequired,
      recipe.category,
      version
    )
    val id = models.Recipe.create(record)
    recipe.ingredients.foreach { ing =>
      val itemId = getItemId(ing.name)
      Ingredient.create(Ingredient(id, itemId, ing.amount))
    }
    recipe.results.foreach { result =>
      val itemId = getItemId(result.name)
      Result.create(Result(id, itemId, result.amount))
    }
  }

  // 存在しない場合はItem作ってIDを返す
  def getItemId(name: String)(implicit session: DBSession): Long = {
    import Aliases.i
    Item.findBy(sqls.eq(i.name, name)).map(_.id)
        .getOrElse(Item.create(name))
  }
}
