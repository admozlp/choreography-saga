package choreograpyhsaga.stock.service;

import choreographysaga.common.dto.ReserveStockRequest;
import choreograpyhsaga.stock.converter.ReserveStockConverter;
import choreograpyhsaga.stock.model.ReserveStock;
import choreograpyhsaga.stock.model.Stock;
import choreograpyhsaga.stock.repository.ReserveStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ReserveStockService {
    private final StockService stockService;
    private final ReserveStockRepository repository;


    @Transactional
    public void reserveStock(ReserveStockRequest request) {
        Stock stock = stockService.findByProductIdAndQuantity(request.productId(), request.quantity());
        ReserveStock reserveStock = ReserveStockConverter.toEntity(request, stock);
        reserveStock.setExpiresAt(Timestamp.from(Instant.now().plusSeconds(600)));
        repository.save(reserveStock);
    }
}
