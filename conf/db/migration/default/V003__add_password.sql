
create table passwords(
    id bigserial not null primary key,
    version varchar(15) not null,
    password char(60) not null
);

create unique index password_version_unique on passwords(password);
