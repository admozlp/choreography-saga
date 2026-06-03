package choreographysaga.order.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Builder.Default
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.STOCK_WILL_BE_RESERVED;


    public enum OrderStatus {
        STOCK_WILL_BE_RESERVED,
        STOCK_RESERVED,
        PAYMENT_CREATED,
        PAYMENT_COMPLETED,
        PAYMENT_FAILED,
    }
}
