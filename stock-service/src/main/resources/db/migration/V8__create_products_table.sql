create table products (
    id         BIGSERIAL PRIMARY KEY,
    name       varchar(256) NOT NULL ,
    image_url   varchar(256) NOT NULL ,
    price      decimal(19,2) NOT NULL ,
    description text NOT NULL
);

create index idx_products_name on products(name);