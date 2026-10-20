package choreograpyhsaga.stock.controller;


import choreographysaga.common.dto.ApiResponse;
import choreographysaga.common.dto.PageResponse;
import choreographysaga.common.dto.PaginationFilter;
import choreograpyhsaga.stock.dto.CreateProductRequest;
import choreograpyhsaga.stock.dto.ProductResponse;
import choreograpyhsaga.stock.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @PostMapping("/get-all")
    public ApiResponse<PageResponse<ProductResponse>> getAll(@RequestBody PaginationFilter filter) {
        return ApiResponse.success(service.getAll(filter));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getById(@PathVariable Long id) {
        log.info("product requested");
        return ApiResponse.success(service.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponse> crate(@ModelAttribute @Valid CreateProductRequest request) {
        log.info("create product");
        return ApiResponse.success(service.create(request));
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        return service.getImage(imageName);
    }
}
