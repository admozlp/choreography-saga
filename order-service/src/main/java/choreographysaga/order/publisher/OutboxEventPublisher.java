package choreographysaga.order.publisher;

import choreographysaga.order.model.Outbox;
import choreographysaga.order.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {
    private final OutboxRepository outboxRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publish(Outbox event) {
        outboxRepository.save(event);
    }
}
