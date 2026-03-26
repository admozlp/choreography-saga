package choreographysaga.payment.controller;

import choreographysaga.common.dto.ApiResponse;
import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService service;

    @PostMapping
    ApiResponse<String> createPayment(@RequestBody @Valid CreatePaymentRequest request) {
        return ApiResponse.success(service.createPayment(request), "Payment created successfully");
    }
}
