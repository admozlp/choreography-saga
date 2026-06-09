package choreograpyhsaga.stock.service;

import choreographysaga.common.dto.ReserveStockRequest;
import choreograpyhsaga.stock.converter.ReserveStockConverter;
import choreograpyhsaga.stock.model.ReserveStock;
import choreograpyhsaga.stock.model.Stock;
import choreograpyhsaga.stock.model.enm.ReserveStockStatus;
import choreograpyhsaga.stock.repository.ReserveStockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static choreographysaga.common.event.EventTypes.PAYMENT_COMPLETED_EVENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveStockService {
    private final StockService stockService;
    private final ReserveStockRepository repository;
    private final ProcessedEventService processedEventService;

    @Transactional
    public void reserveStock(ReserveStockRequest request) {
        if (repository.existsByOrderId(request.orderId())) {
            log.info("Reservation already exists for orderId: {}, skipping (idempotent)", request.orderId());
            return;
        }

        Stock stock = stockService.findByProductIdAndQuantity(request.productId(), request.quantity());
        ReserveStock reserveStock = ReserveStockConverter.toEntity(request, stock);
        reserveStock.setExpiresAt(Instant.now().plusSeconds(600));
        repository.save(reserveStock);
    }

    @Transactional
    public void cancelReservation(Long orderId, UUID eventId, String eventType) {
        if (processedEventService.isEventProcessed(eventId)) {
            return;
        }

        repository.findByOrderId(orderId).ifPresentOrElse(
                rs -> {
                    rs.setStatus(ReserveStockStatus.CANCELED);
                    log.info("Stock reservation marked as cancelled, orderId: {}, reserveStockId: {}, stockId: {}", orderId, rs.getId(), rs.getStock().getId());
                },
                () -> {
                    log.error("Stock reservation not found with orderId: {}", orderId);
                    throw new EntityNotFoundException("Stock reservation not found with order id: " + orderId);
                }
        );

        processedEventService.markEventAsProcessed(eventId, eventType, String.valueOf(orderId));
    }

    @Transactional
    public void confirmReservation(Long orderId, UUID eventId) {
        if (processedEventService.isEventProcessed(eventId)) {
            return;
        }

        repository.findByOrderId(orderId).ifPresentOrElse(
                rs -> rs.setStatus(ReserveStockStatus.CONFIRMED),
                () -> log.warn("No reservation found in confirmReservation for orderId={}, treating as no-op", orderId)
        );

        processedEventService.markEventAsProcessed(eventId, PAYMENT_COMPLETED_EVENT, String.valueOf(orderId));
    }
}
