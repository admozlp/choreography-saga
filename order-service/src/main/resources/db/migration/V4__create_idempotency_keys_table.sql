create  table if not exists idempotency_keys (
    id BIGSERIAL primary key,
    idempotency_key varchar(255) not null,
    operation varchar(255) not null,
    request_hash VARCHAR(64) NOT NULL,
    status VARCHAR(64) NOT NULL DEFAULT 'IN_PROGRESS',
    http_status SMALLINT,
    response jsonb,
    created_at TIMESTAMPTZ not null default now(),
    expires_at TIMESTAMPTZ not null default (now() + interval '1 day')
);

create unique index idx_idempotency_keys_key_operation on idempotency_keys (idempotency_key, operation);
create index idx_idempotency_keys_expires_at on idempotency_keys (expires_at);

