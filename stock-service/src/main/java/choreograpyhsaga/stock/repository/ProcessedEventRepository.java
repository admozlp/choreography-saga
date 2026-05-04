package choreograpyhsaga.stock.repository;

import choreograpyhsaga.stock.model.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {

    @Modifying
    @Query("DELETE FROM ProcessedEvent pe WHERE pe.processedAt < :threshold")
    int deleteProcessedBefore(@Param("threshold") Instant threshold);
}
