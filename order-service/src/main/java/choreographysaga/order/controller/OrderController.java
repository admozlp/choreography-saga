package choreographysaga.order.controller;


import choreographysaga.order.dto.CreateOrderRequest;
import choreographysaga.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @PostMapping
    public String createOrder(@RequestBody @Valid CreateOrderRequest request) {
        return service.createOrder(request);
    }

}
