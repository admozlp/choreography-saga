package choreographysaga.order.dto;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull(message = "Product ID is required")
        long productId,
        @NotNull(message = "Quantity is required")
        int quantity
) {
}
