-- order_id is the natural idempotency key: one order => at most one payment.
-- Prevents duplicate payments / double bank transactions when createPayment is retried
-- (resilience4j @Retry) or re-delivered. The insert happens before the bank call, so the
-- constraint short-circuits a retry before it can start a second bank transaction.
-- Replaces the non-unique index with the unique one (the constraint creates its own index).
DROP INDEX IF EXISTS idx_payments_order_id;

ALTER TABLE payments
    ADD CONSTRAINT uq_payments_order_id UNIQUE (order_id);