package choreographysaga.order.service;

import choreographysaga.order.model.IdempotencyKey;
import choreographysaga.order.model.enm.IdempotencyStatus;
import choreographysaga.order.repository.IdempotencyKeyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyKeyRepository repository;

    @Transactional(readOnly = true)
    public Optional<IdempotencyKey> findByKeyAndOperation(String key, String operation) {
        return repository.findByKeyAndOperation(key, operation);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void claim(String key, String operation, String requestHash) {
        repository.save(IdempotencyKey.builder()
                .key(key)
                .operation(operation)
                .requestHash(requestHash)
                .status(IdempotencyStatus.IN_PROGRESS)
                .build());
        log.debug("Claimed idempotency key: {} operation: {}", key, operation);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void complete(String key, String operation, int httpStatus, String response) {
        IdempotencyKey idempotencyKey = findOrThrow(key, operation);
        idempotencyKey.setStatus(IdempotencyStatus.COMPLETED);
        idempotencyKey.setHttpStatus(httpStatus);
        idempotencyKey.setResponse(response);
        log.debug("Completed idempotency key: {} operation: {}", key, operation);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void fail(String key, String operation, int httpStatus) {
        IdempotencyKey idempotencyKey = findOrThrow(key, operation);
        idempotencyKey.setStatus(IdempotencyStatus.FAILED);
        idempotencyKey.setHttpStatus(httpStatus);
        log.debug("Failed idempotency key: {} operation: {}", key, operation);
    }

    @Scheduled(cron = "${app.idempotency.cleanup-cron:0 0 4 * * *}")
    @Transactional
    public void cleanupExpired() {
        Instant now = Instant.now();
        int deleted = repository.deleteExpiredBefore(now);
        log.info("Cleaned up idempotency_keys with expires_at < {}: {} rows deleted", now, deleted);
    }

    private IdempotencyKey findOrThrow(String key, String operation) {
        return repository.findByKeyAndOperation(key, operation)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Idempotency record not found: key=" + key + " operation=" + operation));
    }
}