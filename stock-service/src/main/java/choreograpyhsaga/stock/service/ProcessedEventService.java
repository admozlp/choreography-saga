package choreograpyhsaga.stock.service;

import choreograpyhsaga.stock.model.ProcessedEvent;
import choreograpyhsaga.stock.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessedEventService {
    private final ProcessedEventRepository repository;

    public boolean isEventProcessed(UUID eventId) {
        boolean exists = repository.existsById(eventId);
        log.debug("Checking if event is already processed, eventId: {}, exists: {}", eventId, exists);
        return exists;
    }

    public void markEventAsProcessed(UUID eventId, String eventType, String aggregateId) {
        repository.save(new ProcessedEvent(eventId, eventType, aggregateId));
        log.info("Marked event as processed, eventId: {}, eventType: {}, aggregateId: {}", eventId, eventType, aggregateId);
    }
}
