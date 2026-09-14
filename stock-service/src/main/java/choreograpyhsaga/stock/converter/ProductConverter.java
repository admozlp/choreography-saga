package choreograpyhsaga.stock.converter;

import choreograpyhsaga.stock.dto.ProductResponse;
import choreograpyhsaga.stock.model.Product;

public class ProductConverter {
    private ProductConverter() {

    }

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getImageUrl(), product.getPrice(), product.getDescription());
    }
}
