package choreograpyhsaga.stock.converter;

import choreograpyhsaga.stock.dto.CreateProductRequest;
import choreograpyhsaga.stock.dto.ProductResponse;
import choreograpyhsaga.stock.model.Product;

public class ProductConverter {
    private ProductConverter() {

    }

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getImageUrl(), product.getPrice(), product.getDescription());
    }

    public static ProductResponse toResponse(Product product, Integer activeQuantity) {
        return new ProductResponse(product.getId(), product.getName(), product.getImageUrl(), product.getPrice(), product.getDescription(), activeQuantity);
    }

    public static Product toEntity(CreateProductRequest request) {
        Product product = new Product();
        product.setDescription(request.description());
        product.setName(request.name());
        product.setPrice(request.price());
        return product;
    }
}
