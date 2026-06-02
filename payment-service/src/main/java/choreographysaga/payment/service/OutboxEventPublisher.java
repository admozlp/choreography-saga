package choreographysaga.payment.service;

import choreographysaga.payment.model.Outbox;
import choreographysaga.payment.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxRepository repository;

    @Transactional
    public void push(Outbox outbox) {
        repository.save(outbox);
    }
}
