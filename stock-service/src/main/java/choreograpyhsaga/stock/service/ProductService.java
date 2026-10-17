package choreograpyhsaga.stock.service;


import choreographysaga.common.converter.PageConverter;
import choreographysaga.common.dto.PageResponse;
import choreographysaga.common.dto.PaginationFilter;
import choreograpyhsaga.stock.converter.ProductConverter;
import choreograpyhsaga.stock.dto.CreateProductRequest;
import choreograpyhsaga.stock.dto.ProductResponse;
import choreograpyhsaga.stock.model.Product;
import choreograpyhsaga.stock.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;
    private final StockService stockService;
    private static final String IMAGE_PATH = "/Users/ademozalp/Desktop/image-store/";

    public PageResponse<ProductResponse> getAll(PaginationFilter filter) {
//        if (5 > 3) {
//            throw new EntityNotFoundException("ürün bulunamadı");
//        }
        PageRequest pageRequest = filter.toPageRequest();
        Page<Product> productPage = repository.findAll(pageRequest);
        return PageConverter.toPageResponse(productPage, ProductConverter::toResponse);
    }

    public ProductResponse getById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        Integer activeQuantity = stockService.getActiveQuantityByProductId(id);
        return ProductConverter.toResponse(product, activeQuantity);
    }

    public ProductResponse create(CreateProductRequest request) {
        Product product = ProductConverter.toEntity(request);
        product.setImageUrl(saveImage(request.image()));
        log.info(product.toString());
        repository.save(product);
        return ProductConverter.toResponse(product);
    }

    private String saveImage(MultipartFile image) {
        try {
            Path imagePath = Paths.get(IMAGE_PATH);

            if (!Files.exists(imagePath)) {
                Files.createDirectories(imagePath);
            }
            String imageName = UUID.randomUUID() + ".jpg";
            Path targetPath = imagePath.resolve(imageName);

            Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Dosya başarıyla kaydedildi");
            return imageName;
        } catch (IOException e) {
            log.error("Dosya kaydedilirken hata oluştu: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<Resource> getImage(String imageName) {
        Path imagePath = Paths.get(IMAGE_PATH);
        Path file = imagePath.resolve(imageName).normalize();

        if (!file.startsWith(IMAGE_PATH)) {
            throw new RuntimeException("Bad request");
        }
        try {
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("image not found");
            }
            String contentType = Files.probeContentType(file);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
