package choreograpyhsaga.stock.service;


import choreograpyhsaga.stock.dto.DecreaseStockRequest;
import choreograpyhsaga.stock.model.Stock;
import choreograpyhsaga.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository repository;

    // TOOO: Implement optimistic locking to handle concurrent stock updates more efficiently.
    // add custom exception handling for better error management and clarity.
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void decreaseStock(DecreaseStockRequest request) {
        log.info("Decreasing stock for productId: {} with quantity: {}", request.productId(), request.quantity());
        Stock stock = repository.findbyProductId(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + request.productId()));

        if (stock.getQuantity() < request.quantity()) {
            throw new RuntimeException("Insufficient stock for productId: " + request.productId());
        }

        stock.setQuantity(stock.getQuantity() - request.quantity());
        repository.save(stock);
        log.info("Stock decreased for productId: {} with quantity: {}", request.productId(), request.quantity());
    }
}
