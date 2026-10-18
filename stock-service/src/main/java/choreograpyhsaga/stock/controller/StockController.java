package choreograpyhsaga.stock.controller;

import choreographysaga.common.dto.ApiResponse;
import choreograpyhsaga.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{stockId}/quantity")
    public ApiResponse<Integer> getQuantityById(@PathVariable Long stockId) {
        return ApiResponse.success(stockService.getQuantityById(stockId));
    }
}
