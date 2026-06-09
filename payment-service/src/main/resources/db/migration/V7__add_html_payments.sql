-- Store the bank 3DS form returned by startPayment so a retried createPayment for the same
-- order (idempotent replay) can return the original form without creating a second payment /
-- bank transaction. Nullable: populated only once the payment reaches STARTED.
ALTER TABLE payments ADD COLUMN html TEXT;