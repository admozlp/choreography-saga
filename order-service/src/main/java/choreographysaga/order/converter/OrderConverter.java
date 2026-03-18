package choreographysaga.order.converter;

import choreographysaga.order.dto.CreateOrderRequest;
import choreographysaga.order.model.Order;

public class OrderConverter {
    private OrderConverter() {
    }

    public static Order toEntity(CreateOrderRequest request) {
        return Order.builder()
                .productId(request.productId())
                .quantity(request.quantity())
                .build();
    }
}
