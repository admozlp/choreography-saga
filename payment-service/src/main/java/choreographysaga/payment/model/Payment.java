package choreographysaga.payment.model;


import choreographysaga.payment.model.enm.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    // Bank 3DS form returned by startPayment; stored so a retried createPayment for the same
    // order can replay the original form (idempotency) instead of starting a second bank transaction.
    @Column(columnDefinition = "TEXT")
    private String html;
}
