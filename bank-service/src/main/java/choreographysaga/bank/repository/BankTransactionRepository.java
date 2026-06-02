package choreographysaga.bank.repository;

import choreographysaga.bank.model.BankTransaction;
import choreographysaga.common.model.enm.BankTransactionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    @Query("SELECT COUNT(bt) > 0 FROM  BankTransaction bt WHERE bt.otpCode = :otpCode")
    boolean existsByOtpCode(@Param("otpCode") int otpCode);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select  bt from BankTransaction  bt where bt.paymentId = :paymentId and bt.status = :status")
    Optional<BankTransaction> findByPaymentIdAndStatus(@Param("paymentId") Long paymentId, @Param("status") BankTransactionStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select  bt from BankTransaction  bt where bt.paymentId = :paymentId")
    Optional<BankTransaction> findByPaymentId(@Param("paymentId") Long paymentId);
}
