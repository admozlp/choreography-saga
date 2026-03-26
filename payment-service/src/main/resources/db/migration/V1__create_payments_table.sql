CREATE TABLE payments
(
    id       BIGSERIAL PRIMARY KEY,
    order_id BIGINT           NOT NULL,
    amount   DECIMAL(19, 2)   NOT NULL,
    status   VARCHAR(20)      NOT NULL DEFAULT 'STARTED'
);

CREATE INDEX idx_payments_order_id ON payments (order_id);
CREATE INDEX idx_payments_status ON payments (status);
