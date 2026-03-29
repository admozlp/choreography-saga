package choreographysaga.bank.controller;


import choreographysaga.bank.service.BankService;
import choreographysaga.common.dto.ApiResponse;
import choreographysaga.common.dto.BankResponse;
import choreographysaga.common.dto.StartPaymentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bank")
public class BankController {
    private final BankService service;

    @PostMapping
    public ApiResponse<BankResponse> startPayment(@RequestBody @Valid StartPaymentRequest request) {
        return ApiResponse.success(service.startPayment(request));
    }

}
