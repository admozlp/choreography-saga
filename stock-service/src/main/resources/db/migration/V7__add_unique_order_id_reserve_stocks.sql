-- order_id is the natural idempotency key: one order => at most one reservation.
-- Prevents duplicate reservations when the reserve call is retried (resilience4j @Retry)
-- or re-delivered. Replaces the non-unique index with a unique one (the constraint
-- creates its own index, so the old plain index is redundant).
DROP INDEX IF EXISTS idx_reserve_stocks_order_id;

ALTER TABLE reserve_stocks
    ADD CONSTRAINT uq_reserve_stocks_order_id UNIQUE (order_id);