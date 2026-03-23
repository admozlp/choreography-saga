package choreograpyhsaga.stock.controller;


import choreographysaga.common.dto.DecreaseStockRequest;
import choreograpyhsaga.stock.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockService service;

    @PutMapping
    public void decreaseStock(@RequestBody @Valid DecreaseStockRequest request) {
        service.decreaseStock(request);
    }
}
