package parsers

import org.luaj.vm2.LuaTable
import parsers.Recipe.{Ingredients, Results}

import scala.util.Try
import scala.collection.breakOut

object RecipeParser extends FactorioParser[Recipe] {
  override def transport(table: LuaTable): Option[Recipe] = Recipe.fromTable(table)
}

case class Recipe(
    typ: String,
    name: String,
    energyRequired: Double,
    category: Option[String],
    ingredients: Ingredients,
    results: Results
)

object Recipe {
  type Ingredients = Seq[ItemAmount]
  type Results = Seq[ItemAmount]

  def fromTable(table: LuaTable): Option[Recipe] = Try {
    val t = if(table.keys().exists(_.checkjstring() == "normal")) table.get("normal").checktable() else table
    val Items(ingredients, results, time) = Items.fromTable(t)
    Recipe(
      table.get("type").checkjstring(),
      table.get("name").checkjstring(),
      time,
      Try { table.get("category").checkjstring() }.toOption,
      ingredients,
      results
    )
  }.toOption
}

case class ItemAmount(name: String, amount: Int)

object ItemAmount {
  def fromTable(table: LuaTable): Option[ItemAmount] = Try {
    ItemAmount(
      Try { table.get("name").checkjstring() }.getOrElse(table.get(1).checkjstring()),
      Try { table.get("amount").checkint() }.getOrElse(table.get(2).checkint())
    )
  }.toOption
}

case class Items(ingredients: Ingredients, results: Results, energyRequired: Double)

object Items {
  import FactorioParser.tableToSeq

  def fromTable(table: LuaTable): Items = {
    val ingredients = ingredientsParser(table.get("ingredients").checktable())
    val results = resultsParser(table)
    val time = Try { table.get("energy_required").checkdouble() }.getOrElse(0.5)
    Items(ingredients, results, time)
  }

  def ingredientsParser(table: LuaTable): Ingredients = {
    val tables = tableToSeq(table)(_.checktable())
    tables.flatMap(ItemAmount.fromTable)
  }

  def resultsParser(table: LuaTable): Results = {
    if(table.get("result").isstring()) {
      ItemAmount(
        table.get("result").checkjstring(),
        Try { table.get("result_count").checkint() }.getOrElse(1)
      ) :: Nil
    } else {
      val tables = tableToSeq(table.get("results").checktable())(_.checktable())
      tables.flatMap(ItemAmount.fromTable)
    }
  }
}
