package choreograpyhsaga.stock.controller;


import choreographysaga.common.dto.ApiResponse;
import choreographysaga.common.dto.PageResponse;
import choreographysaga.common.dto.PaginationFilter;
import choreograpyhsaga.stock.dto.ProductResponse;
import choreograpyhsaga.stock.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;


    @PostMapping("/get-all")
    public ApiResponse<PageResponse<ProductResponse>> getAll(@RequestBody PaginationFilter filter) {
        return ApiResponse.success(service.getAll(filter));
    }
}
