package com.ponkotuy

import com.ponkotuy.parsers.{ItemParser, RecipeParser}

object Main extends App {
  val items = ItemParser.parse("prototypes/item/item.lua")
  val recipes = RecipeParser.parse("prototypes/recipe/recipe.lua")
  recipes.map(_.simpleString).foreach(println)
}
