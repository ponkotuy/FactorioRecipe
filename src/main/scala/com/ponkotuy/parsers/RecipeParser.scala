package com.ponkotuy.parsers

import com.ponkotuy.parsers.Recipe.{Ingredients, Results}
import org.luaj.vm2.LuaTable

import scala.util.Try
import scala.collection.breakOut

object RecipeParser extends FactorioParser[Recipe] {
  override def transport(table: LuaTable): Option[Recipe] = Recipe.fromTable(table)
}

case class Recipe(
    typ: String,
    name: String,
    energyRqeuired: Double,
    category: Option[String],
    ingredients: Ingredients,
    results: Results
) {
  def simpleString: String = {
    val list = s"${results} <-" ::
        (ingredients.map { case ItemAmount(item, amount) =>
          s"  ${item}: ${amount}"
        }(breakOut): List[String])
    list.mkString("\n")
  }
}

object Recipe {
  import FactorioParser.tableToSeq
  type Ingredients = Seq[ItemAmount]
  type Results = Seq[ItemAmount]

  def fromTable(table: LuaTable): Option[Recipe] = Try {
    Recipe(
      table.get("type").checkjstring(),
      table.get("name").checkjstring(),
      Try { table.get("energy_required").checkdouble() }.getOrElse(0.5),
      Try { table.get("category").checkjstring() }.toOption,
      ingredientsParser(table.get("ingredients").checktable()),
      resultsParser(table)
    )
  }.toOption

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

case class ItemAmount(name: String, amount: Int)

object ItemAmount {
  def fromTable(table: LuaTable): Option[ItemAmount] = Try {
    ItemAmount(
      Try { table.get("name").checkjstring() }.getOrElse(table.get(1).checkjstring()),
      Try { table.get("amount").checkint() }.getOrElse(table.get(2).checkint())
    )
  }.toOption
}
