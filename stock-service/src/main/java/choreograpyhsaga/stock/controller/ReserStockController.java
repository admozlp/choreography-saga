package choreograpyhsaga.stock.controller;

import choreographysaga.common.dto.ReserveStockRequest;
import choreograpyhsaga.stock.service.ReserveStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/reserve-stocks")
@RequiredArgsConstructor
public class ReserStockController {
    private final ReserveStockService service;

    @PostMapping("/internal/reserve")
    @PreAuthorize("hasAuthority('stock:reserve')")
    public void reserveStock(@RequestBody @Valid ReserveStockRequest request) {
        service.reserveStock(request);
    }
}
