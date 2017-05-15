
create unique index ingredient_recipe_item_unique on ingredient(recipe_id, item_id);

create unique index result_recipe_item_unique on result(recipe_id, item_id);
