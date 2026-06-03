package choreographysaga.payment.repository;

import choreographysaga.payment.model.Payment;
import choreographysaga.payment.model.enm.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Modifying
    @Query("UPDATE Payment p set p.status = :paymentStatus where p.id = :paymentId and p.status = PaymentStatus.STARTED")
    int updateIfStatusStarted(@Param("paymentId") Long paymentId, @Param("paymentStatus") PaymentStatus paymentStatus);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.id = :id")
    Optional<Payment> findByIdForUpdate(@Param("id") Long id);
}