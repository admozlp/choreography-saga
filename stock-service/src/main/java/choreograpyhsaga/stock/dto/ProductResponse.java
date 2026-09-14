package choreograpyhsaga.stock.dto;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String imageUrl, BigDecimal price, String description) {
}
