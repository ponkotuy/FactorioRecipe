
create unique index ingradient_recipe_item_unique on ingradient(recipe_id, item_id);

create unique index result_recipe_item_unique on result(recipe_id, item_id);
