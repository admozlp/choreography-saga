package choreographysaga.bank.controller;


import choreographysaga.bank.dto.ConfirmPaymentRequest;
import choreographysaga.bank.service.BankTransactionService;
import choreographysaga.common.dto.ApiResponse;
import choreographysaga.common.dto.BankResponse;
import choreographysaga.common.dto.StartPaymentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bank-transactions")
public class BankTransactionController {
    private final BankTransactionService service;

    @PostMapping
    public ApiResponse<BankResponse> startPayment(@RequestBody @Valid StartPaymentRequest request) {
        return ApiResponse.success(service.startPayment(request));
    }

    @PostMapping(value = "/confirm", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> confirmPayment(@RequestBody @Valid ConfirmPaymentRequest request) {
        String html = service.confirmPayment(request);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @PostMapping("/handshake")
    public ApiResponse<Void> handshake(String paymentId, String status) {
        service.handshake(paymentId, status);
        return ApiResponse.success(null);
    }

}
