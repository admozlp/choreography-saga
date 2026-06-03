package choreographysaga.payment.controller;

import choreographysaga.common.dto.ApiResponse;
import choreographysaga.common.dto.CreatePaymentRequest;
import choreographysaga.common.dto.PaymentCallbackRequest;
import choreographysaga.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService service;

    @PostMapping
    public ApiResponse<String> createPayment(@RequestBody @Valid CreatePaymentRequest request) {
        return ApiResponse.success(service.createPayment(request), "Payment created successfully");
    }

    @PostMapping("/callback")
    public ModelAndView callback(@ModelAttribute @Valid PaymentCallbackRequest request) {
        return service.callback(request);
    }

    @GetMapping("/success")
    public String success() {
        return "Payment successful";
    }

    @GetMapping("/error")
    public String error(@RequestParam(value = "message", required = false, defaultValue = "Payment failed") String message) {
        return message;
    }
}
