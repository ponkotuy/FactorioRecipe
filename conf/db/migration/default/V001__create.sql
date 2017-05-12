
create table item(
    id bigserial not null,
    name varchar(255) not null
);

create index item_name_index on item(name);


create table recipe(
    id bigserial not null,
    name varchar(255) not null,
    time float not null,
    category varchar(31) not null,
    version varchar(15) not null
);

create unique index recipe_name_version_unique on recipe(name, version);

create table ingredient(
    recipe_id bigint not null,
    item_id bigint not null
);

create table result(
    recipe_id bigint not null,
    item_id bigint not null
);
