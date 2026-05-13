CREATE TABLE  IF NOT EXISTS bank_transactions
(
    id           BIGSERIAL PRIMARY KEY,
    payment_id   BIGINT         NOT NULL,
    amount       DECIMAL(19, 2) NOT NULL,
    callback_url VARCHAR(512)   NOT NULL,
    otp_code          INTEGER        NOT NULL,
    otp_attempt_count SMALLINT       NOT NULL DEFAULT 0 CHECK (otp_attempt_count BETWEEN 0 AND 3),
    created_at        TIMESTAMPTZ    NOT NULL,
    expires_at        TIMESTAMPTZ    NOT NULL,
    status            VARCHAR(100)   NOT NULL
);

CREATE INDEX idx_bank_transactions_payment_id ON bank_transactions (payment_id);
CREATE INDEX idx_bank_transactions_otp_code ON bank_transactions (otp_code);
CREATE INDEX idx_bank_transactions_status ON bank_transactions (status);