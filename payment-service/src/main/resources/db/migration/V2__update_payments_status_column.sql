UPDATE payments SET status = 'CREATED'   WHERE status = '0';
UPDATE payments SET status = 'STARTED'   WHERE status = '1';
UPDATE payments SET status = 'COMPLETED' WHERE status = '2';
UPDATE payments SET status = 'FAILED'    WHERE status = '3';

ALTER TABLE payments
    ALTER COLUMN status SET DEFAULT 'CREATED';