package choreographysaga.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {
    private long pageCount;
    private long page;
    private long totalElements;
    private List<T> list;
}
