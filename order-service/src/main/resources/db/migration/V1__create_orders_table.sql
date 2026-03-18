CREATE TABLE IF NOT EXISTS orders (
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT       NOT NULL,
    quantity   INTEGER      NOT NULL,
    status     VARCHAR(20)  NOT NULL DEFAULT 'CREATED'
);