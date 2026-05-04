package choreograpyhsaga.stock.scheduler;

import choreograpyhsaga.stock.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessedEventCleanupJob {

    private final ProcessedEventRepository repository;

    @Value("${app.inbox.retention-days:14}")
    private long retentionDays;

    @Scheduled(cron = "${app.inbox.cleanup-cron:0 0 3 * * *}")
    @Transactional
    public void cleanup() {
        Instant threshold = Instant.now().minus(Duration.ofDays(retentionDays));
        int deleted = repository.deleteProcessedBefore(threshold);
        log.info("Cleaned up processed_events older than {}: {} rows deleted", threshold, deleted);
    }
}
