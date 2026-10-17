package choreograpyhsaga.stock.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record CreateProductRequest(@NotEmpty(message = "name is required") String name,
                                   @NotEmpty(message = "description is required") String description,
                                   @NotNull(message = "price is required") BigDecimal price,
                                   @NotNull(message = "image is required") MultipartFile image) {

}
