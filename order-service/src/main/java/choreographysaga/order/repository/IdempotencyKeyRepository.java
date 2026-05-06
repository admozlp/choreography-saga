package choreographysaga.order.repository;

import choreographysaga.order.model.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    Optional<IdempotencyKey> findByKeyAndOperation(String key, String operation);

    @Modifying
    @Query("DELETE FROM IdempotencyKey ik WHERE ik.expiresAt < :threshold")
    int deleteExpiredBefore(@Param("threshold") Instant threshold);
}
