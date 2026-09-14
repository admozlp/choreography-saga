package choreographysaga.common.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public record PaginationFilter(Integer page, Integer size, String sortBy, Sort.Direction sortDirection) {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final String DEFAULT_SORT_BY = "id";
    private static final Sort.Direction DEFAULT_DIRECTION = Sort.Direction.DESC;

    public PaginationFilter {
        page = (page == null || page < 0) ? DEFAULT_PAGE : page;
        size = (size == null || size < 1) ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        sortBy = (sortBy == null || sortBy.isBlank()) ? DEFAULT_SORT_BY : sortBy;
        sortDirection = (sortDirection == null) ? DEFAULT_DIRECTION : sortDirection;
    }

    public PageRequest toPageRequest() {
        return PageRequest.of(page, size, sortDirection, sortBy);
    }
}
