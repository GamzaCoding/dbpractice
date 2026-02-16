create table if not exists product (
    id bigserial  primary key,
    product_name VARCHAR(200) not null,
    survey_date DATE not null,
    product_price NUMERIC(15, 2) not null,
    store_name VARCHAR(200) not null,
    maker VARCHAR(200) not null,
    is_sale BOOLEAN,
    is_one_plus_one BOOLEAN
);