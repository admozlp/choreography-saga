package choreographysaga.bank.repository;

import choreographysaga.bank.model.BankTransaction;
import choreographysaga.bank.model.enm.BankTransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    @Query("SELECT COUNT(bt) > 0 FROM  BankTransaction bt WHERE bt.otpCode = :otpCode")
    boolean existsByOtpCode(@Param("otpCode") int otpCode);


    @Query("select  bt from BankTransaction  bt where bt.paymentId = :paymentId and bt.status = :status")
    Optional<BankTransaction> findByPaymentIdAndOtpCodeAndStatus(@Param("paymentId") Long paymentId, @Param("status") BankTransactionStatus status);
}
