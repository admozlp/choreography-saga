package choreograpyhsaga.stock.converter;

import choreographysaga.common.dto.ReserveStockRequest;
import choreograpyhsaga.stock.model.ReserveStock;
import choreograpyhsaga.stock.model.Stock;
import choreograpyhsaga.stock.model.enm.ReserveStockStatus;

public class ReserveStockConverter {
    private ReserveStockConverter() {
    }

    public static ReserveStock toEntity(ReserveStockRequest request, Stock stock) {
        return ReserveStock.builder()
                .quantity(request.quantity())
                .status(ReserveStockStatus.RESERVED)
                .orderId(request.orderId())
                .stock(stock)
                .build();
    }
}
