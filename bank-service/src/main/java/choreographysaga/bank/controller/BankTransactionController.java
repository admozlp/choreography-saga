package choreographysaga.bank.controller;


import choreographysaga.bank.dto.ConfirmPaymentRequest;
import choreographysaga.bank.service.BankTransactionService;
import choreographysaga.common.dto.ApiResponse;
import choreographysaga.common.dto.BankHandshakeRequest;
import choreographysaga.common.dto.BankTransactionResponse;
import choreographysaga.common.dto.CreateBankTransactionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bank-transactions")
@CrossOrigin("*")
public class BankTransactionController {
    private final BankTransactionService service;

    @PostMapping
    public ApiResponse<BankTransactionResponse> startPayment(@RequestBody @Valid CreateBankTransactionRequest request) {
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
    public ApiResponse<Boolean> handshake(@RequestBody @Valid BankHandshakeRequest request) {
        return ApiResponse.success(service.handshake(request), "Handshake successful");
    }

}
