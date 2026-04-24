package choreograpyhsaga.stock.model;


import choreograpyhsaga.stock.model.enm.ReserveStockStatus;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "reserve_stocks")
public class ReserveStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReserveStockStatus status;

    @Column(name = "expires_at", nullable = false)
    private Timestamp expiresAt;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;
}
