package choreographysaga.common.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReserveStockRequest(
        @NotNull(message = "Product ID cannot be null")
        long productId,
        @NotNull(message = "Quantity cannot be null")
        @Positive(message = "Quantity is min 1")
        int quantity,
        @NotNull(message = "Quantity cannot be null")
        Long orderId
) {
}
