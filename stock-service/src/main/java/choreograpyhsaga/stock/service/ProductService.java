package choreograpyhsaga.stock.service;


import choreographysaga.common.converter.PageConverter;
import choreographysaga.common.dto.PageResponse;
import choreographysaga.common.dto.PaginationFilter;
import choreograpyhsaga.stock.converter.ProductConverter;
import choreograpyhsaga.stock.dto.ProductResponse;
import choreograpyhsaga.stock.model.Product;
import choreograpyhsaga.stock.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;

    public PageResponse<ProductResponse> getAll(PaginationFilter filter) {
        PageRequest pageRequest = filter.toPageRequest();
        Page<Product> productPage = repository.findAll(pageRequest);
        return PageConverter.toPageResponse(productPage, ProductConverter::toResponse);
    }
}
