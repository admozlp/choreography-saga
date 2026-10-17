package choreograpyhsaga.stock.dto;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String imageUrl, BigDecimal price, String description,
                              Integer activeQuantity) {

    public ProductResponse(Long id, String name, String imageUrl, BigDecimal price, String description) {
        this(id, name, imageUrl, price, description, null);
    }
}
