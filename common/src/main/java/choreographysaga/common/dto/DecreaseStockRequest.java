package choreographysaga.common.dto;

import jakarta.validation.constraints.NotNull;

public record DecreaseStockRequest(
        @NotNull(message = "Product ID cannot be null")
        long productId,
        @NotNull(message = "Quantity cannot be null")
        int quantity
) {
}
