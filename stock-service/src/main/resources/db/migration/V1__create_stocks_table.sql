CREATE TABLE stocks
(
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT  NOT NULL,
    quantity   INTEGER NOT NULL
);

CREATE INDEX idx_stocks_id ON stocks (id);
CREATE INDEX idx_stocks_product_id ON stocks (product_id);