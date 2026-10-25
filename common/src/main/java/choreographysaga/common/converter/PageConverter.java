package choreographysaga.common.converter;

import choreographysaga.common.dto.PageResponse;
import org.springframework.data.domain.Page;

import java.util.function.Function;

public class PageConverter {
    // pipeline test

    public static <T, D> PageResponse<D> toPageResponse(Page<T> page, Function<T, D> converter) {
        return new PageResponse<>(
                page.getTotalPages(),
                page.getNumber(),
                page.getTotalElements(),
                page.getContent().stream().map(converter).toList()
        );
    }
}
