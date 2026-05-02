CREATE TABLE reserve_stocks
(
    id         BIGSERIAL PRIMARY KEY,
    quantity   INTEGER     NOT NULL,
    status     VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP   NOT NULL,
    order_id   BIGINT      NOT NULL,
    stock_id   BIGINT      NOT NULL,
    CONSTRAINT fk_reserve_stocks_stock FOREIGN KEY (stock_id) REFERENCES stocks (id)
);

CREATE INDEX idx_reserve_stocks_order_id ON reserve_stocks (order_id);
CREATE INDEX idx_reserve_stocks_stock_id ON reserve_stocks (stock_id);
CREATE INDEX idx_reserve_stocks_status ON reserve_stocks (status);
CREATE INDEX idx_reserve_stocks_expires_at ON reserve_stocks (expires_at);
