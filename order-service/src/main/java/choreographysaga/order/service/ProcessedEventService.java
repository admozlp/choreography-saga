package choreographysaga.order.service;

import choreographysaga.order.model.ProcessedEvent;
import choreographysaga.order.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessedEventService {
    private final ProcessedEventRepository repository;

    public boolean isProcessed(UUID eventId) {
        boolean exists = repository.existsById(eventId);
        log.debug("Checking if event is already processed, eventId: {}, exists: {}", eventId, exists);
        return exists;
    }

    @Transactional
    public void markAsProcessed(UUID eventId, String eventType, String aggreateId) {
        repository.save(new ProcessedEvent(eventId, eventType, aggreateId, Instant.now()));
        log.info("Event mark as processed, eventId {}, eventType: {}, aggregateId: {}", eventId, eventType, aggreateId);
    }

}
