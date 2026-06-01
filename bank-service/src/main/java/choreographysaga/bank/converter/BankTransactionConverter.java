package choreographysaga.bank.converter;

import choreographysaga.bank.model.BankTransaction;
import choreographysaga.common.model.enm.BankTransactionStatus;
import choreographysaga.common.dto.StartPaymentRequest;

import java.time.Instant;

public class BankTransactionConverter {
    private BankTransactionConverter() {
    }

    public static BankTransaction toEntity(StartPaymentRequest request, Integer otpCode) {
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setPaymentId(request.paymentId());
        bankTransaction.setAmount(request.amount());
        bankTransaction.setCallbackUrl(request.callbackUrl());
        bankTransaction.setOtpCode(otpCode);
        bankTransaction.setOtpAttemptCount(Short.valueOf("0"));
        bankTransaction.setStatus(BankTransactionStatus.PENDING);
        Instant now = Instant.now();
        bankTransaction.setCreatedAt(now);
        bankTransaction.setExpiresAt(now.plusSeconds(180));
        return bankTransaction;
    }
}
