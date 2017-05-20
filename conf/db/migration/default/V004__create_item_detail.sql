
create table item_details(
    id bigint not null references item(id) primary key,
    subgroup varchar(255) not null,
    "order" varchar(255) not null,
    stack_size int not null
);
