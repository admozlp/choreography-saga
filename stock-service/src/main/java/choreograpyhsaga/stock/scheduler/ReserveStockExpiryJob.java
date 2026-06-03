package choreograpyhsaga.stock.scheduler;

import choreograpyhsaga.stock.model.enm.ReserveStockStatus;
import choreograpyhsaga.stock.repository.ReserveStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReserveStockExpiryJob {

    private final ReserveStockRepository repository;

    @Scheduled(cron = "${app.reservation.expiry-cron:0 0 2 * * *}")
    @Transactional
    public void expireStaleReservations() {
        Instant now = Instant.now();
        int expired = repository.expireStaleReservations(
                ReserveStockStatus.RESERVED, ReserveStockStatus.EXPIRED, now);
        log.info("Expired stale RESERVED reservations older than {}: {} rows updated", now, expired);
    }
}
