-- status VARCHAR(20) ve CHECK constraint olmadığı için 'EXPIRED' değeri ek bir şema değişikliği gerektirmez.
-- Gece çalışan expiry job'ı, süresi geçmiş RESERVED kayıtları hızlı taramak için partial index'ten faydalanır.
CREATE INDEX idx_reserve_stocks_reserved_expires_at
    ON reserve_stocks (expires_at)
    WHERE status = 'RESERVED';
