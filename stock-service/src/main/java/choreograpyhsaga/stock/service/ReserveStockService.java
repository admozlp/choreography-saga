package choreograpyhsaga.stock.service;

import choreographysaga.common.dto.ReserveStockRequest;
import choreograpyhsaga.stock.converter.ReserveStockConverter;
import choreograpyhsaga.stock.model.ReserveStock;
import choreograpyhsaga.stock.model.Stock;
import choreograpyhsaga.stock.model.enm.ReserveStockStatus;
import choreograpyhsaga.stock.repository.ReserveStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static choreographysaga.common.event.EventTypes.STOCK_RESERVATION_COMPENSATION_EVENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveStockService {
    private final StockService stockService;
    private final ReserveStockRepository repository;
    private final ProcessedEventService processedEventService;

    @Transactional
    public void reserveStock(ReserveStockRequest request) {
        Stock stock = stockService.findByProductIdAndQuantity(request.productId(), request.quantity());
        ReserveStock reserveStock = ReserveStockConverter.toEntity(request, stock);
        reserveStock.setExpiresAt(Instant.now().plusSeconds(600));
        repository.save(reserveStock);
    }

    @Transactional
    public void cancelReservation(Long orderId, UUID eventId) {
        if (processedEventService.isEventProcessed(eventId)) {
            return;
        }

        repository.findByOrderId(orderId).ifPresentOrElse(
                rs -> rs.setStatus(ReserveStockStatus.CANCELED),
                () -> log.warn("No reservation found for orderId={}, treating as no-op", orderId)
        );

        processedEventService.markEventAsProcessed(eventId, STOCK_RESERVATION_COMPENSATION_EVENT, String.valueOf(orderId));
    }
}
