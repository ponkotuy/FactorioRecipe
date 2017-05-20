package models

import scalikejdbc._

class PersistRecipe(version: String) {
  import PersistItem.getItemId

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
}
