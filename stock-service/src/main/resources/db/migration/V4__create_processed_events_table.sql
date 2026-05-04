CREATE TABLE processed_events
(
    event_id     UUID         PRIMARY KEY,
    event_type   VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(255),
    processed_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_processed_events_processed_at ON processed_events (processed_at);
CREATE INDEX idx_processed_events_aggregate ON processed_events (aggregate_id, event_type);
